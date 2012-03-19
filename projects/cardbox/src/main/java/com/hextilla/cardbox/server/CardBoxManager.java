//
// CardBox library - framework for matchmaking networked games
// Copyright (C) 2005-2011 Three Rings Design, Inc., All Rights Reserved
// http://github.com/threerings/game-gardens
//
// This library is free software; you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published
// by the Free Software Foundation; either version 2.1 of the License, or
// (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package com.hextilla.cardbox.server;

import java.io.File;
import java.io.FileReader;

import java.security.MessageDigest;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.samskivert.io.PersistenceException;
import com.samskivert.io.StreamUtil;
import com.samskivert.jdbc.WriteOnlyUnit;
import com.samskivert.util.HashIntSet;
import com.samskivert.util.IntIntMap;
import com.samskivert.util.Interval;
import com.samskivert.util.Invoker;
import com.samskivert.util.ResultListenerList;

import com.threerings.getdown.data.Resource;

import com.threerings.presents.annotation.MainInvoker;
import com.threerings.presents.client.InvocationService.ResultListener;
import com.threerings.presents.data.ClientObject;
import com.threerings.presents.server.InvocationException;
import com.threerings.presents.server.InvocationManager;
import com.threerings.presents.server.PresentsDObjectMgr;
import com.threerings.presents.util.PersistingUnit;
import com.threerings.presents.util.ResultAdapter;

import com.threerings.crowd.data.BodyObject;
import com.threerings.crowd.data.PlaceConfig;
import com.threerings.crowd.server.PlaceManager;
import com.threerings.crowd.server.PlaceRegistry;

import com.threerings.parlor.data.Table;
import com.threerings.parlor.data.TableConfig;
import com.threerings.parlor.game.data.GameConfig;
import com.threerings.parlor.game.server.GameManager;
import com.threerings.parlor.game.server.GameManagerDelegate;
import com.threerings.parlor.server.ParlorManager;
import com.threerings.parlor.server.ParlorSender;
import com.threerings.parlor.server.TableManager;
import com.threerings.util.Name;

import com.hextilla.cardbox.lobby.data.LobbyConfig;
import com.hextilla.cardbox.lobby.data.LobbyObject;
import com.hextilla.cardbox.lobby.server.LobbyManager;

import com.hextilla.cardbox.data.GameDefinition;
import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.hextilla.cardbox.data.HexDeck;
import com.hextilla.cardbox.data.TableMatchConfig;
import com.hextilla.cardbox.server.persist.GameRecord.Status;
import com.hextilla.cardbox.server.persist.GameRecord;
import com.hextilla.cardbox.server.persist.SessionRecord;
import com.hextilla.cardbox.util.CardBoxClassLoader;
import com.hextilla.cardbox.util.CardBoxUtil;

import static com.hextilla.cardbox.Log.log;
import static com.hextilla.cardbox.data.CardBoxCodes.*;

/**
 * Manages the server side of the CardBox services.
 */
@Singleton
public class CardBoxManager extends ParlorManager
    implements CardBoxProvider
{
    /**
     * Provides access to {@link GameRecord} info for the CardBox manager.
     */
    public interface GameRepository
    {
        /** Loads the persistent data for a game. */
        public GameRecord loadGame (int gameId)
            throws PersistenceException;

        /** Records playtime to a game's persistent record. */
        public void incrementPlaytime (int gameId, int minutes)
            throws PersistenceException;

        /** Updates the number of players online for a game. */
        public void updateOnlineCount (int gameId, int players)
            throws PersistenceException;
    }

    @Inject public CardBoxManager (InvocationManager invmgr)
    {
    	super(invmgr);
        // register ourselves as providing the cardbox service
        invmgr.registerDispatcher(new CardBoxDispatcher(this), TOYBOX_GROUP);
    }

    /**
     * Prepares the cardbox manager for operation.
     */
    public void init (GameRepository gamerepo)
        throws PersistenceException
    {
        // make a note of our server services
        _gamerepo = gamerepo;

        if (_gamerepo != null) {
            // periodically write our occupancy information to the database
            _popval = new Interval(_omgr) {
                @Override
                public void expired () {
                    publishOccupancy();
                }
            };
            _popval.schedule(60 * 1000L, true);
        }
        
        _cancelledInvites.setSentinel(INVITE_SENTINEL);

        log.info("CardBoxManager ready [rsrcdir=" + CardBoxConfig.getResourceDir() + "].");
    }

    /**
     * Prepares the cardbox manager for operation in development mode where it only hosts the lobby
     * for a single game, which we will create immediately rather than on-demand.
     */
    public void setDevelopmentMode (File gameConfig)
        throws PersistenceException
    {
        // create a fake game record for this game and resolve its lobby
        GameRecord game = new GameRecord();
        GameDefinition gamedef = null;
        try {
            game.gameId = -1;
            game.name = "Hextilla";
            game.maintainerId = 1;
            game.setStatus(Status.READY);
            game.definition = StreamUtil.toString(new FileReader(gameConfig));

            // compute the digests of all the files
            gamedef = game.parseGameDefinition();
            File jar = new File(CardBoxConfig.getResourceDir(), gamedef.getMediaPath(game.gameId));
            log.info("Reading " + jar + "...");
            MessageDigest md = MessageDigest.getInstance("MD5");
            game.digest = Resource.computeDigest(jar, md, null);

        } catch (Exception e) {
            log.warning("Failed to load game config [path=" + gameConfig + "].", e);
            return;
        }

        try {
            resolveLobby(game, null);
        } catch (InvocationException ie) {
            log.warning("Failed to resolve lobby [game=" + game + "].", ie);
        }
    }

    /**
     * Returns the custom class loader that should be used for the
     * specified place.
     */
    public ClassLoader getClassLoader (PlaceConfig config)
    {
        if (config instanceof CardBoxGameConfig) {
            CardBoxGameConfig tconfig = (CardBoxGameConfig)config;
            String ident = tconfig.getManagerClassName();
            CardBoxClassLoader loader = _loaders.get(ident);
            // create a classloader if we haven't yet, or if our
            // underlying jar files have changed since we created one
            if (loader == null || !loader.isUpToDate()) {
                loader = CardBoxUtil.createClassLoader(
                    CardBoxConfig.getResourceDir(), tconfig.getGameId(),
                    tconfig.getGameDefinition());
                _loaders.put(ident, loader);
            }
            return loader;

        } else {
            return null;
        }
    }

    /**
     * Called to report the total playtime in a particular game. This
     * records the playtime persistently.
     *
     * @param game the game whose time is being reported.
     * @param playtime the total playtime in milliseconds.
     */
    public void recordPlaytime (final GameRecord game, long playtime)
    {
        // we don't record playtime if we're in development mode
        if (_gamerepo == null) {
            return;
        }

        int mins = (int)Math.round(playtime / ONE_MINUTE);
        if (mins > ODDLY_LONG) {
            log.warning("Game in play for oddly long time " +
                        "[game=" + game.name + ", mins=" + mins + "].");
        }
        mins = Math.min(mins, MAX_PLAYTIME);
        if (mins <= 0) {
            return;
        }

        log.info("Recording playtime [game=" + game.name +
                 ", mins=" + mins + "].");

        final int fmins = mins;
        _invoker.postUnit(new WriteOnlyUnit("updatePlaytime(" + game.gameId + ", " + mins + ")") {
            @Override
            public void invokePersist () throws Exception {
                _gamerepo.incrementPlaytime(game.gameId, fmins);
            }
        });
    }

    // documentation inherited from interface
    public void getLobbyOid (ClientObject caller, final int gameId, final ResultListener rl)
        throws InvocationException
    {
        // look to see if we have already resolved a lobby for this game
        Integer lobbyOid = _lobbyOids.get(gameId);
        if (lobbyOid != null) {
            rl.requestProcessed(lobbyOid);
            return;
        }

        // if we are currently loading this lobby, add this listener to the list of penders
        ResultListenerList<Integer> penders = _penders.get(gameId);
        if (penders != null) {
            penders.add(new ResultAdapter<Integer>(rl));
            return;
        }

        // load the game information from the database
        _invoker.postUnit(new PersistingUnit("resolveLobby(" + gameId + ")", rl) {
            @Override
            public void invokePersistent () throws Exception {
                if ((_game = _gamerepo.loadGame(gameId)) == null) {
                    throw new InvocationException(INTERNAL_ERROR);
                }
            }
            @Override
            public void handleSuccess () {
                try {
                    // start the lobby resolution. if this fails we will catch the failure and
                    // report it to the caller
                    resolveLobby(_game, rl);
                } catch (InvocationException ie) {
                    rl.requestFailed(ie.getMessage());
                }
            }
            protected GameRecord _game;
        });
    }

    /**
     * Resolves a lobby for the specified game definition. When the lobby is fully resolved, all
     * pending listeners will be notified of its creation. See {@link #_penders}.
     *
     * @param game the metadata for the game whose lobby we will create.
     */
    public void resolveLobby (final GameRecord game, ResultListener rl)
        throws InvocationException
    {
        log.info("Resolving " + game.which() + ".");

        try {
            PlaceManager pmgr = _plreg.createPlace(
                new LobbyConfig(game.gameId, game.parseGameDefinition()));

            // let our lobby manager know about its game and ourselves
            ((LobbyManager)pmgr).init(this, game);

            // register ourselves in the lobby table
            int ploid = pmgr.getPlaceObject().getOid();
            _lobbyOids.put(game.gameId, ploid);

            // inform any resolution penders of the lobby oid
            ResultListenerList<Integer> listeners = _penders.remove(game.gameId);
            if (listeners != null) {
                listeners.requestCompleted(ploid);
            }

            // and inform the calling resolver if there was one
            if (rl != null) {
                rl.requestProcessed(ploid);
            }

        } catch (InstantiationException e) {
            log.warning("Failed to create game lobby [game=" + game.which() + "]", e);
            throw new InvocationException(INTERNAL_ERROR);
        }
    }

    /**
     * Called by the {@link LobbyManager} when it shuts down.
     */
    public void lobbyDidShutdown (final GameRecord game)
    {
        if (_lobbyOids.remove(game.gameId) == null) {
            log.warning("Lobby shut down for which we have no registration " +
                        "[game=" + game.which() + "].");
        } else {
            log.info("Unloading lobby '" + game.which() + "'.");
        }

        if (_gamerepo != null) {
            // clear out the number of players online count for this game
            _invoker.postUnit(new Invoker.Unit() {
                @Override
                public boolean invoke () {
                    try {
                        _gamerepo.updateOnlineCount(game.gameId, 0);
                    } catch (Exception e) {
                        log.warning("Failed to clear online count " +
                                "[game=" + game.name + "].", e);
                    }
                    return false;
                }
            });
        }
    }
    
    @Override
    public void cancelInvite (BodyObject source, int inviteId)
    {
    	// We need to look up this invite and notify the invitee that the game's off.
    	Invitation invite = super._invites.remove(inviteId);
    	if (invite != null) {
    		String cancel_msg = "Invite " + inviteId + " extended to you has been cancelled.";
	    	ParlorSender.sendInviteResponse(
	                invite.invitee, invite.inviteId, INVITATION_REFUSED, cancel_msg);
	    	_cancelledInvites.add(inviteId);
    	}
    }
    
    @Override
    protected void processAcceptedInvitation (Invitation invite)
    {
        try {
        	LobbyManager lmgr = (LobbyManager)_plreg.getPlaceManager(_lobbyOids.get(invite.config.getGameId()));
            log.info("Processing accepted invitation [invite=" + invite + "].");

            // configure the game config with the player info
            invite.config.players = new Name[] { invite.invitee.getVisibleName(),
                                                 invite.inviter.getVisibleName() };
            
            TableConfig tconfig = new TableConfig();
            tconfig.minimumPlayerCount = 2;
            tconfig.desiredPlayerCount = 2;
            
            // We need to check if someone's accepted an invite from themselves for some reason
            if (invite.invitee.getVisibleName().equals(invite.inviter.getVisibleName()))
    		{
            	throw new Exception("User " + invite.invitee.getVisibleName().toString() + 
            			" attempted to invite themselves to a game");
    		}
            
            // Ensure that both of the "invitational" parties are actually still in the lobby by the time 
            if (!(lmgr.isInLobby(invite.inviter) && lmgr.isInLobby(invite.invitee)))
            {
            	throw new Exception("One or more users participating in this invitation are no longer " +
            			"in the original lobby, aborting. " + "invite=" + invite + ", invitee=" + invite.config.players[0]
            					+ ", inviter " + invite.config.players[1]);
            }
            
            if (_cancelledInvites.remove(invite.inviteId))
            {
            	throw new Exception("User " + invite.invitee.getVisibleName().toString() + 
            			" attempted to accept a cancelled invitation");
            }
            
            TableManager tablemgr = lmgr.getTableManager();
            Table table = tablemgr.createTable(invite.inviter, tconfig, invite.config);
            if (table != null) {
            	tablemgr.joinTable(invite.invitee, table.tableId, 1, null);
            } else {
            	log.warning("Invitational table could not be created", "invite", invite);
            }
        } catch (Exception e) {
            log.warning("Unable to process accepted invitation", "invite", invite, e);
        }
    }
    
    @Override
    protected void createGameManager (GameConfig config)
            throws InstantiationException, InvocationException
    {
    }

    /**
     * Creates a game based on the supplied configuration.
     */
    public GameManager createGame (GameConfig config)
        throws InvocationException
    {
        // TODO: various complicated bits to pass this request off to the standalone game server
        try {
        	if (config instanceof CardBoxGameConfig && config.getGameId() != -1)
        	{
        		HexDeck deck = _cbcmgr.getCards();
        		((CardBoxGameConfig)config).setDeck(deck);
        	}
        	
            PlaceManager pmgr = _plreg.createPlace(config);

            // add a delegate that will record the game's playtime upon completion
            pmgr.addDelegate(new GameManagerDelegate() {
                @Override
                public void gameWillStart () {
                    _started = System.currentTimeMillis();
                }
                @Override
                public void gameDidEnd () {
                    long playtime = System.currentTimeMillis() - _started;
                    log.info("CardBoxManager: Game ended","playtime", playtime);
                }
                protected long _started;
            });

            return (GameManager)pmgr;

        } catch (InstantiationException ie) {
            log.warning("Failed to create manager for game [config=" + config + "]", ie);
            throw new InvocationException(INTERNAL_ERROR);

        } catch (UnsupportedClassVersionError ucve) {
            throw new InvocationException(TOYBOX_MSGS, "e.code_version_incorrect");
        }
    }

    /**
     * Publishes our lobby and game occupancy figures to the database.
     */
    protected void publishOccupancy ()
    {
        // note the number of occupants in all games
        final IntIntMap occs = new IntIntMap();
        for (int gameId : _lobbyOids.keySet()) {
            LobbyManager lmgr = (LobbyManager)_plreg.getPlaceManager(_lobbyOids.get(gameId));
            if (lmgr == null) {
                continue;
            }
            occs.put(gameId, ((LobbyObject)lmgr.getPlaceObject()).countOccupants(_omgr));
        }

        // then update the database
        _invoker.postUnit(new Invoker.Unit() {
            @Override
            public boolean invoke () {
                for (IntIntMap.IntIntEntry entry : occs.entrySet()) {
                    try {
                        _gamerepo.updateOnlineCount(entry.getKey(), entry.getValue());
                    } catch (Exception e) {
                        log.warning("Failed to clear online count " +
                                "[gameId=" + entry.getKey() + "].", e);
                    }
                }
                return false;
            }
        });
    }

    /** Provides information on {@link GameRecord}s. */
    protected GameRepository _gamerepo;

    /** Handles distributed object business. */
    @Inject protected PresentsDObjectMgr _omgr;
    
    /** Handles management of our card set. */
    @Inject protected CardBoxCardManager _cbcmgr;

    /** Handles database business. */
    @Inject protected @MainInvoker Invoker _invoker;

    /** Handles creation of places. */
    @Inject protected PlaceRegistry _plreg;

    /** Contains pending listeners for lobbies in the process of being resolved. */
    protected Map<Integer,ResultListenerList<Integer>> _penders = Maps.newHashMap();

    /** Contains a mapping from game identifier strings to lobby oids for lobbies that have been
     * resolved. */
    protected Map<Integer,Integer> _lobbyOids = Maps.newHashMap();

    /** Maps game identifiers to custom class loaders. In general this will only have one mapping,
     * but we'll be general just in case.  */
    protected Map<String,CardBoxClassLoader> _loaders = Maps.newHashMap();
    
    /** Keep a set of invite IDs we've cancelled in case people try to accept them */
    protected HashIntSet _cancelledInvites = new HashIntSet();
    protected static final int INVITE_SENTINEL = -2;

    /** Periodically writes out the number of users online in each game to a file. */
    protected Interval _popval;

    /** One minute in milliseconds. */
    protected static final double ONE_MINUTE = 60 * 1000L;

    /** The maximum playtime we will record for a game, in minutes. (This is to avoid booching the
     * stats if something goes awry.) */
    protected static final int MAX_PLAYTIME = 60;

    /** If a game is in play longer than this many minutes, we log a warning when recording its
     * playtime to catch funny business. */
    protected static final int ODDLY_LONG = 120;
}
