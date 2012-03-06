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

package com.hextilla.cardbox.lobby.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.InputStream;

import javax.imageio.ImageIO;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

import com.samskivert.swing.MultiLineLabel;
import com.samskivert.swing.util.SwingUtil;

import com.threerings.media.image.BufferedMirage;
import com.threerings.media.image.ImageUtil;
import com.threerings.media.image.Mirage;
import com.threerings.util.MessageBundle;

import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.PlaceObject;

import com.hextilla.cardbox.client.OccupantList;
import com.hextilla.cardbox.client.CardBoxUI;
import com.hextilla.cardbox.client.chat.ChatPanel;
import com.hextilla.cardbox.data.GameDefinition;
import com.hextilla.cardbox.data.TableMatchConfig;
import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.hextilla.cardbox.util.CardBoxContext;

import com.hextilla.cardbox.lobby.data.LobbyCodes;
import com.hextilla.cardbox.lobby.data.LobbyConfig;
import com.hextilla.cardbox.lobby.data.LobbyObject;
import com.hextilla.cardbox.lobby.HextillaLobbyPanel.HextillaLobbyPanel;

import static com.hextilla.cardbox.lobby.Log.log;

/**
 * Displays the main CardBox match-making lobby interface.
 */
public class LobbyPanel extends JPanel
    implements PlaceView
{
    /**
     * Constructs a new lobby panel and the associated user interface
     * elements.
     */
    public LobbyPanel (CardBoxContext ctx)
    {
        _ctx = ctx;
        _msgs = _ctx.getMessageManager().getBundle(LobbyCodes.LOBBY_MSGS);

        // we want a five pixel border around everything
    	setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

    	// Gridbag layout lets us do some wise shit
    	setLayout(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);        
    	
    	// Add the page title
        _title = new MultiLineLabel("", MultiLineLabel.CENTER);
        _title.setFont(CardBoxUI.fancyFont);
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        add(_title, c);
        
        // Main Panel, populated based on the games download progress
        _main = new JPanel(new GridLayout(1,1));
        c.weightx = 1.0;
        c.weighty = 10.0;
        c.gridwidth = GridBagConstraints.REMAINDER; 
        c.gridheight = 10;      
        add(_main, c);        

        // load up our background image
        try {
            InputStream bgin = getClass().getClassLoader().getResourceAsStream(
                "rsrc/media/lobby_background.png");
            if (bgin != null) {
                _bgimg = new BufferedMirage(ImageIO.read(bgin));
            }
        } catch (Exception e) {
            log.warning("Failed to load background image.", e);
        }

        // properly configure all of our components
        SwingUtil.applyToHierarchy(this, _colorizer);      
    }
    
    // documentation inherited
    public void willEnterPlace (PlaceObject plobj)
    {
        _lobj = (LobbyObject)plobj;
        _title.setText(_lobj.name);
    }

    // documentation inherited
    public void didLeavePlace (PlaceObject plobj)
    {
    }

    // documentation inherited
    @Override
    protected void paintComponent (Graphics g)
    {
        super.paintComponent(g);

        // tile our background image
        if (_bgimg != null) {
            Graphics2D gfx = (Graphics2D)g;
            ImageUtil.tileImage(gfx, _bgimg, 0, 0, getWidth(), getHeight());
        }
    }

    /** Used to de-opaquify and set the right backgrounds in the right
     * places. */
    protected SwingUtil.ComponentOp _colorizer = new SwingUtil.ComponentOp() {
        public void apply (Component comp) {
            if (comp instanceof JPanel) {
                ((JPanel)comp).setOpaque(false);
            } else if (comp instanceof JCheckBox) {
                ((JCheckBox)comp).setOpaque(false);
                comp.setForeground(Color.white);
            } else if (comp instanceof JSlider) {
                ((JSlider)comp).setOpaque(false);
                comp.setForeground(Color.white);
            } else if (comp instanceof JScrollPane) {
                ((JScrollPane)comp).getViewport().setBackground(
                    CardBoxUI.LIGHT_BLUE);
            } else if (comp instanceof JLabel) {
                comp.setForeground(Color.white);
            } else if (comp instanceof JList ||
                       comp instanceof JComboBox) {
                comp.setBackground(CardBoxUI.LIGHT_BLUE);
            }
        }
    };
    
    // Set game config data (after it has loaded)
	public void setGameConfig(LobbyConfig config) {
		// TODO Auto-generated method stub
		_config = config;	
	}   
	
    public void loadGamePanel (LobbyConfig config)
    {
        // create our match-making view
        JComponent hextillaView = createGamePanel(_ctx, config);
        if (hextillaView != null) {
        	_main.add(hextillaView);
            if (hextillaView instanceof HextillaLobbyPanel) {
                // because we're adding our match making view after we've
                // already entered our place, we need to fake an entry
                ((HextillaLobbyPanel) hextillaView).init(_lobj);
            }
            // properly configure all of our components (limiting to a
            // depth of six is a giant hack but I'm too lazy to do the
            // serious dicking around that is needed to do the "right"
            // thing; fucking user interfaces)
            SwingUtil.applyToHierarchy(this, 6, _colorizer);
            SwingUtil.refresh(_main);
        }
    }
    
    protected JComponent createGamePanel (
            CardBoxContext ctx, LobbyConfig config)
        {
            return new HextillaLobbyPanel(ctx, config);
        }    

    /** Giver of life and services. */
    protected CardBoxContext _ctx;

    /** Our translation messages. */
    protected MessageBundle _msgs;

    /** Contains the main view. */
    protected JPanel _main;

    /** Our lobby distributed object. */
    protected LobbyObject _lobj;

    /** Displays the game title. */
    protected MultiLineLabel _title;

    /** Our background image. */
    protected Mirage _bgimg;
    
    // Game config
    protected LobbyConfig _config;
}
