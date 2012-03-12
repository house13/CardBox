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

package com.hextilla.cardbox.client;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.Window;

import javax.swing.JPanel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import com.samskivert.servlet.user.Password;
import com.samskivert.swing.Controller;
import com.samskivert.swing.ControllerProvider;
import com.samskivert.util.Config;
import com.samskivert.util.RunQueue;
import com.samskivert.util.StringUtil;

import com.threerings.media.FrameManager;
import com.threerings.util.IdleTracker;
import com.threerings.util.KeyDispatcher;
import com.threerings.util.MessageManager;
import com.threerings.util.Name;

import com.threerings.presents.client.Client;
import com.threerings.presents.dobj.DObjectManager;
import com.threerings.presents.net.Credentials;
import com.threerings.presents.net.UsernamePasswordCreds;

import com.threerings.crowd.chat.client.ChatDirector;
import com.threerings.crowd.chat.data.ChatCodes;
import com.threerings.crowd.client.BodyService;
import com.threerings.crowd.client.LocationDirector;
import com.threerings.crowd.client.OccupantDirector;
import com.threerings.crowd.client.PlaceController;
import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.PlaceConfig;

import com.threerings.parlor.client.ParlorDirector;

import com.hextilla.cardbox.client.chat.CardBoxChatDirector;
import com.hextilla.cardbox.client.chat.ChatPanel;
import com.hextilla.cardbox.client.chat.FriendChatPanel;
import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.hextilla.cardbox.data.CardBoxCodes;
import com.hextilla.cardbox.facebook.CardBoxCredentials;
import com.hextilla.cardbox.facebook.client.SocialDirector;
import com.hextilla.cardbox.util.CardBoxContext;

import static com.hextilla.cardbox.Log.log;

/**
 * The CardBox client takes care of instantiating all of the proper managers and loading up all of
 * the necessary configuration and getting the client bootstrapped.
 */
public class CardBoxClient
{
    /** Provides acccess to the context in which we're running, either an application or an
     * applet. */
    public interface Shell
    {
        /** Sets the window title, if possible. */
        public void setTitle (String title);

        /** Returns the window in which we're running. */
        public Window getWindow ();

        /** Returns our frame manager. */
        public FrameManager getFrameManager ();

        /** Configures our content pane. */
        public void setContentPane (Container root);

        /** Instructs the shell to wire up logoff when it is closed. */
        public void bindCloseAction (CardBoxClient client);
    }

    /**
     * Initializes a new client and provides it with a frame in which to display everything.
     */
    public void init (Shell shell)
        throws IOException
    {
        // create our context
        _ctx = createContextImpl();

        // create the directors/managers/etc. provided by the context
        createContextServices();

        // keep this for later
        _shell = shell;

        // load up our user interface bits
        CardBoxUI.init(_ctx);
        initFonts();
        
        // use the game name as our title if we have one
        String title = _ctx.xlate(CardBoxCodes.TOYBOX_MSGS, "m.app_title");
        try {
            title = System.getProperty("game_name", title);
        } catch (SecurityException se) {
            // alas
        }
        _shell.setTitle(title);
        _shell.bindCloseAction(this);
        _keydisp = new KeyDispatcher(_shell.getWindow());

        // create our client controller
        _cctrl = new ClientController(_ctx, this);

        // stuff our top-level pane into the top-level of our shell
        _shell.setContentPane(_root);

        // start our idle tracker
        IdleTracker idler = new IdleTracker(ChatCodes.DEFAULT_IDLE_TIME, LOGOFF_DELAY) {
            @Override
            protected long getTimeStamp () {
                return _shell.getFrameManager().getTimeStamp();
            }
            @Override
            protected void idledIn () {
                updateIdle(false);
            }
            @Override
            protected void idledOut () {
                updateIdle(true);
            }
            protected void updateIdle (boolean isIdle) {
                if (_ctx.getClient().isLoggedOn()) {
                    log.info("Setting idle " + isIdle + ".");
                    BodyService bsvc = _ctx.getClient().requireService(BodyService.class);
                    bsvc.setIdle(isIdle);
                }
            }
            @Override
            protected void abandonedShip () {
                if (_client.isLoggedOn()) {
                    _client.logoff(true);
                }
            }
        };
        idler.start(null, _shell.getWindow(), _ctx.getClient().getRunQueue());
    }

    /**
     * Returns a reference to the context in effect for this client. This reference is valid for
     * the lifetime of the application.
     */
    public CardBoxContext getContext ()
    {
        return _ctx;
    }

    /**
     * Returns a reference to the main client controller.
     */
    public ClientController getClientController ()
    {
        return _cctrl;
    }
    
    /** 
     * @return Whether we're running our client in development mode
     */
    public boolean inDevMode ()
    {
    	return _devmode;
    }
    
    /**
     * Denote that the client is running in devmode. This will only ever be set once.
     */
    public void setDevMode ()
    {
    	_devmode = true;
    }
    
    /**
     * @param session_id The (encrypted) hex string identifying our session authenticated through Facebook
     */
    public void setSession (String session_id)
    {
    	_session = session_id;
    }
    
    /**
     * Creates the appropriate type of credentials from the supplied session ID
     */
    public Credentials createCredentials ()
    {
        return new CardBoxCredentials(_session);
    }

    /**
     * Creates the appropriate type of credentials from the supplied session ID
     */
    public Credentials createCredentials (String session_id)
    {
    	setSession(session_id);
        return new CardBoxCredentials(session_id);
    }

    /**
     * Sets the main user interface panel.
     */
    public void setMainPanel (JPanel panel)
    {
        // remove the old panel
        _root.removeAll();
        // add the new one
        _root.add(panel, BorderLayout.CENTER);
        // swing doesn't properly repaint after adding/removing children
        _root.revalidate();
        _root.repaint();
    }

    /**
     * Creates the {@link CardBoxContext} implementation that will be passed around to all of the
     * client code. Derived classes may wish to override this and create some extended context
     * implementation.
     */
    protected CardBoxContext createContextImpl ()
    {
        return new CardBoxContextImpl();
    }

    /**
     * Creates and initializes the various services that are provided by the context. Derived
     * classes that provide an extended context should override this method and create their own
     * extended services. They should be sure to call <code>super.createContextServices</code>.
     */
    protected void createContextServices ()
        throws IOException
    {
        // create the handles on our various services
        _client = new Client(null, RunQueue.AWT);

        // we use this to handle i18n
        _msgmgr = new MessageManager(MESSAGE_MANAGER_PREFIX);

        // create our managers and directors
        _locdir = createLocationDirector();
        _occdir = new OccupantDirector(_ctx);
        _chatdir = new CardBoxChatDirector(_ctx, ChatPanel.CHAT_MSGS);
        _fchatdir = new CardBoxChatDirector(_ctx, ChatPanel.CHAT_MSGS);
        _pardtr = new ParlorDirector(_ctx);
        _carddtr = new CardBoxDirector(_ctx);
        _socialdtr = new SocialDirector(_ctx); 
    }

    /**
     * Given a subdirectory name (that should correspond to the calling service), returns a file
     * path that can be used to store local data.
     */
    public static String localDataDir (String subdir)
    {
        String appdir = System.getProperty("appdir");
        if (StringUtil.isBlank(appdir)) {
            appdir = ".cardbox";
            String home = System.getProperty("user.home");
            if (!StringUtil.isBlank(home)) {
                appdir = home + File.separator + appdir;
            }
        }
        return appdir + File.separator + subdir;
    }

    /**
     * Creates our custom location director.
     */
    protected LocationDirector createLocationDirector ()
    {
        return new CardBoxLocationDirector();
    }

    /** Makes our client controller visible to the dispatch system. */
    protected class RootPanel extends JPanel
        implements ControllerProvider
    {
        public RootPanel () {
            super(new BorderLayout());
        }

        public Controller getController () {
            return _cctrl;
        }
    }

    /** Handles using custom classloaders to load place code. */
    protected class CardBoxLocationDirector extends LocationDirector
    {
        public CardBoxLocationDirector () {
            super(CardBoxClient.this._ctx);
        }

        @Override
        protected PlaceController createController (PlaceConfig config) {
            if (config instanceof CardBoxGameConfig) {
                CardBoxGameConfig cardcfg = (CardBoxGameConfig)config;
                String ccls = cardcfg.getGameDefinition().controller;
                ClassLoader loader = _carddtr.getClassLoader(config);
                try {
                    return (PlaceController)Class.forName(ccls, true, loader).newInstance();
                } catch (Exception e) {
                    log.warning("Failed to instantiate game controller", "class", ccls,
                                "cloader", loader, e);
                    return null;
                }
            } else {
                return super.createController(config);
            }
        }
    }

    /**
     * The context implementation. This provides access to all of the objects and services that are
     * needed by the operating client.
     */
    protected class CardBoxContextImpl extends CardBoxContext
    {
        /**
         * Apparently the default constructor has default access, rather than protected access,
         * even though this class is declared to be protected. Why, I don't know, but we need to be
         * able to extend this class elsewhere, so we need this.
         */
        protected CardBoxContextImpl ()
        {
        }

        public Client getClient ()
        {
            return _client;
        }
        
        public String getSession ()
        {
        	return _session;
        }
        
        @Override
        public boolean isFacebookEnabled()
        {
        	return !inDevMode();
        }

        public DObjectManager getDObjectManager ()
        {
            return _client.getDObjectManager();
        }

        public Config getConfig ()
        {
            return _config;
        }

        public LocationDirector getLocationDirector ()
        {
            return _locdir;
        }

        public OccupantDirector getOccupantDirector ()
        {
            return _occdir;
        }

        public CardBoxChatDirector getChatDirector ()
        {
            return _chatdir;
        }
        
        public CardBoxChatDirector getFriendChatDirector ()
        {
            return _fchatdir;
        }          

        @Override
        public MessageManager getMessageManager ()
        {
            return _msgmgr;
        }

        public ParlorDirector getParlorDirector ()
        {
            return _pardtr;
        }

        public void setPlaceView (PlaceView view)
        {
            setMainPanel((JPanel)view);
        }

        public void clearPlaceView (PlaceView view)
        {
            // we'll just let the next place view replace our old one
        }

        @Override
        public CardBoxDirector getCardBoxDirector ()
        {
            return _carddtr;
        }
        
        @Override
        public SocialDirector getSocialDirector ()
        {
            return _socialdtr;
        }

        @Override
        public FrameManager getFrameManager ()
        {
            return _shell.getFrameManager();
        }

        @Override
        public KeyDispatcher getKeyDispatcher ()
        {
            return _keydisp;
        }
    }

    private void initFonts()
    {
    	UIDefaults defaults = UIManager.getLookAndFeelDefaults();

		//  Build of Map of items and a Map of attributes for each item

		for ( @SuppressWarnings("rawtypes")
		Enumeration enumm = defaults.keys(); enumm.hasMoreElements(); )
		{
			Object key = enumm.nextElement();
			Object value = defaults.get( key );

			if (value instanceof Font)
			{
				UIManager.getDefaults().put(key.toString(), CardBoxUI.AppFontSmall);
			}
			
		}
    }
    
    protected CardBoxContext _ctx;
    protected Shell _shell;
    protected RootPanel _root = new RootPanel();
    protected Config _config = new Config("cardbox");

    protected Client _client;
    protected ClientController _cctrl;
    protected MessageManager _msgmgr;
    protected KeyDispatcher _keydisp;

    protected LocationDirector _locdir;
    protected OccupantDirector _occdir;
    protected CardBoxChatDirector _chatdir;
    protected CardBoxChatDirector _fchatdir;
    protected ParlorDirector _pardtr;
    protected CardBoxDirector _carddtr;
    protected SocialDirector _socialdtr;
    
    protected String _session;
    
    protected boolean _devmode = false;

    /** The prefix prepended to localization bundle names before looking them up in the
     * classpath. */
    protected static final String MESSAGE_MANAGER_PREFIX = "rsrc.i18n";

    /** The time in milliseconds after which we log off an idle user. */
    protected static final long LOGOFF_DELAY = 8L * 60L * 1000L;
}
