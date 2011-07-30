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

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import com.samskivert.depot.PersistenceContext;
import com.samskivert.jdbc.ConnectionProvider;
import com.samskivert.jdbc.StaticConnectionProvider;
import com.samskivert.util.Lifecycle;
import com.samskivert.util.StringUtil;

import com.threerings.util.Name;

import com.threerings.presents.net.AuthRequest;
import com.threerings.presents.server.Authenticator;
import com.threerings.presents.server.SessionFactory;
import com.threerings.presents.server.ClientResolver;
import com.threerings.presents.server.PresentsSession;

import com.threerings.crowd.data.PlaceConfig;
import com.threerings.crowd.server.CrowdServer;
import com.threerings.crowd.server.PlaceManager;
import com.threerings.crowd.server.PlaceRegistry;

import com.threerings.parlor.server.ParlorManager;

import com.hextilla.cardbox.server.persist.CardBoxRepository;

import static com.hextilla.cardbox.Log.log;

/**
 * The main entry point and general organizer of everything that goes on
 * in the CardBox game server process.
 */
public class CardBoxServer extends CrowdServer
{
    /** Configures dependencies needed by the CardBox services. */
    public static class CardBoxModule extends CrowdServer.CrowdModule
    {
        @Override protected void configure () {
            super.configure();
            bind(PlaceRegistry.class).to(CardBoxPlaceRegistry.class);
            bind(Authenticator.class).to(CardBoxConfig.getAuthenticator());
            bind(ConnectionProvider.class).toInstance(
                new StaticConnectionProvider(CardBoxConfig.getJDBCConfig()));
        }
    }

    /**
     * The main entry point for the CardBox server.
     */
    public static void main (String[] args)
    {
        runServer(new CardBoxModule(), new PresentsServerModule(CardBoxServer.class));
    }

    @Override // from CrowdServer
    public void init (Injector injector)
        throws Exception
    {
        super.init(injector);

        // configure the client manager to use the appropriate client class
        _clmgr.setDefaultSessionFactory(new SessionFactory() {
            @Override
            public Class<? extends PresentsSession> getSessionClass (AuthRequest areq) {
                return CardBoxSession.class;
            }
            @Override
            public Class<? extends ClientResolver> getClientResolverClass (Name username) {
                return CardBoxClientResolver.class;
            }
        });

        // determine whether we've been run in test mode with a single game configuration
        String gconfig = System.getProperty("game_conf");
        PersistenceContext pctx = null;
        CardBoxRepository cardrepo = null;
        if (StringUtil.isBlank(gconfig)) {
            pctx = new PersistenceContext();
            cardrepo = new CardBoxRepository(pctx);
            pctx.init(CardBoxRepository.GAME_DB_IDENT, _conprov, null);
            pctx.initializeRepositories(true);
        }
        _cardmgr.init(cardrepo);
        if (!StringUtil.isBlank(gconfig)) {
            _cardmgr.setDevelopmentMode(new File(gconfig));
        }

        log.info("CardBox server initialized.");
    }

    /**
     * Returns the port on which the connection manager will listen for client connections.
     */
    @Override
    protected int[] getListenPorts ()
    {
        return new int[] { CardBoxConfig.getServerPort() };
    }
    
    /**
     * Returns the address that the server will be bound to.
     * Returning null will bind to the wildcard address.
     * We can optionally modify this if we need to bind to a specific address.
     */
    @Override
    protected String getBindHostname ()
    {
    	return null;
    }

    @Singleton
    protected static class CardBoxPlaceRegistry extends PlaceRegistry {
        @Inject public CardBoxPlaceRegistry (Lifecycle cycle) {
            super(cycle);
        }
        @Override protected PlaceManager createPlaceManager (PlaceConfig config) throws Exception {
            ClassLoader loader = _cardmgr.getClassLoader(config);
            if (loader == null) {
                return super.createPlaceManager(config);
            }
            PlaceManager mgr = (PlaceManager)Class.forName(
                config.getManagerClassName(), true, loader).newInstance();
            _injector.injectMembers(mgr);
            return mgr;
        }
        @Inject protected CardBoxManager _cardmgr;
    }

    @Inject protected ParlorManager _parmgr;
    @Inject protected CardBoxManager _cardmgr;
    @Inject protected ConnectionProvider _conprov;
}
