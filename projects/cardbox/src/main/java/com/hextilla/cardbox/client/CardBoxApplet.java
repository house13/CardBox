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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.IOException;
import java.net.URL;

import com.threerings.media.FrameManager;
import com.threerings.media.ManagedJApplet;

import com.threerings.presents.client.Client;

import static com.hextilla.cardbox.Log.log;

/**
 * Launches a CardBox game from an applet.
 */
@SuppressWarnings("serial")
public class CardBoxApplet extends ManagedJApplet
    implements CardBoxClient.Shell
{
    // from interface CardBoxSession.Shell
    public void setTitle (String title)
    {
    }

    // from interface CardBoxSession.Shell
    public void bindCloseAction (CardBoxClient client)
    {
        // no need to do anything here
    }

    @Override // from Applet
    public void init ()
    {
        super.init();

        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext", "true");
        
        log.info("Java: " + System.getProperty("java.version") +
            ", " + System.getProperty("java.vendor") + ")");

        // create our frame manager
        _framemgr = FrameManager.newInstance(this);

        // configure our server and port
        String server = getParameter("server");
        int port = getIntParameter("port", -1);
        int game_id = getIntParameter("game_id", -1);
        String session_id = getParameter("session_id");
        String resourceURL = getParameter("resource_url");
        
        try {
            // create and initialize our client instance, initializing in dev mode if applicable
            _client = createClient();
            if (server.equalsIgnoreCase("localhost") && game_id == -1) _client.setDevMode();
            _client.init(this);
        } catch (IOException ioe) {
            log.warning("Failed to create CardBoxSession.", ioe);
            return;
        }
        
        if (server == null || port <= 0) {
            log.warning("Failed to obtain server and port parameters [server=" + server +
                        ", port=" + port + "].");
            return;
        }
        Client client = _client.getContext().getClient();
        log.info("Using [server=" + server + ", port=" + port + "].");
        client.setServer(server, new int[] { port });

        // and our resource url
        CardBoxDirector carddtr = _client.getContext().getCardBoxDirector();
        
        try {
            carddtr.setResourceURL(new URL(resourceURL));
        } catch (Exception e) {
            log.warning("Invalid resource_url supplied '" +
                        resourceURL + "': " + e + ".");
        }
        
        // and our authenticated session ID
        _client.setSession(session_id);

        // and our game id and game oid
        carddtr.setGameId(game_id, getIntParameter("game_oid", -1));  
        
        setBackground(Color.black);         
    }

    @Override // from Applet
    public void start ()
    {
        super.start();
        _framemgr.start();
        
        // We'll ask the width and height by this 
        dim = getSize(); 
        // Create an offscreen image to draw on 
        // Make it the size of the applet, this is just perfect larger 
        // size could slow it down unnecessary. 
        offscreen = createImage(dim.width,dim.height); 
        // by doing this everything that is drawn by bufferGraphics 
        // will be written on the offscreen image. 
        bufferGraphics = offscreen.getGraphics();        
    }

    @Override // from Applet
    public void stop ()
    {
        super.stop();
        _framemgr.stop();

        // if we're logged on, log off
        if (_client != null) {
            Client client = _client.getContext().getClient();
            if (client != null && client.isLoggedOn()) {
                client.logoff(true);
            }
        }
    }

    /** Helpy helper function. */
    protected int getIntParameter (String name, int defvalue)
    {
        try {
            return Integer.parseInt(getParameter(name));
        } catch (Exception e) {
            return defvalue;
        }
    }

    /**
     * Creates our client implementation.
     */
    protected CardBoxClient createClient ()
    {
        return new CardBoxClient();
    }

    public void paint(Graphics g)  
    {    
    	bufferGraphics.clearRect(0,0,dim.width,dim.width); 
        super.paintAll(bufferGraphics); 
        g.drawImage(offscreen,0,0,this); 
    }

    // Always required for good double-buffering. 
    // This will cause the applet not to first wipe off 
    // previous drawings but to immediately repaint. 
    // the wiping off also causes flickering. 
    // Update is called automatically when repaint() is called.

    public void update(Graphics g) 
    { 
         paint(g); 
    } 
    
    protected CardBoxClient _client;
    protected FrameManager _framemgr;
    
    Graphics bufferGraphics; 
    // The image that will contain everything that has been drawn on 
    // bufferGraphics. 
    Image offscreen; 
    // To get the width and height of the applet. 
    Dimension dim; 
    int curX, curY;
}
