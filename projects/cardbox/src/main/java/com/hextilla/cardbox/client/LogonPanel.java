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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.samskivert.servlet.user.Password;
import com.samskivert.util.StringUtil;
import com.samskivert.swing.GroupLayout;
import com.samskivert.swing.HGroupLayout;
import com.samskivert.swing.MultiLineLabel;
import com.samskivert.swing.VGroupLayout;

import com.threerings.media.image.BufferedMirage;
import com.threerings.media.image.ImageUtil;
import com.threerings.media.image.Mirage;

import com.threerings.util.MessageBundle;

import com.threerings.presents.client.Client;
import com.threerings.presents.client.ClientObserver;
import com.threerings.presents.client.LogonException;

import com.hextilla.cardbox.swing.CardBoxButton;
import com.hextilla.cardbox.util.CardBoxContext;

import static com.hextilla.cardbox.Log.log;

public class LogonPanel extends JPanel
    implements ActionListener, ClientObserver
{
    public LogonPanel (CardBoxContext ctx, CardBoxClient client)
    {
        // keep these around for later
        _ctx = ctx;
        _client = client;
        _msgs = _ctx.getMessageManager().getBundle("client.logon");

	setLayout(new VGroupLayout());
        setBackground(CardBoxUI.LIGHT_BLUE);

        // stick the logon components into a panel that will stretch them
        // to a sensible width
        JPanel box = new JPanel(
            new VGroupLayout(VGroupLayout.NONE, VGroupLayout.NONE,
                             5, VGroupLayout.CENTER)) {
            @Override
            public Dimension getPreferredSize () {
                Dimension psize = super.getPreferredSize();
                psize.width = Math.max(psize.width, 300);
                return psize;
            }
        };
        box.setOpaque(false);
        add(box);

        // load our background imagery
        try {
            _bgimg = ImageIO.read(
                getClass().getClassLoader().getResourceAsStream(
                    "rsrc/media/logon_background.png"));
            _flowers = new BufferedMirage(
                ImageIO.read(
                    getClass().getClassLoader().getResourceAsStream(
                        "rsrc/media/lobby_background.png")));
        } catch (Exception e) {
            log.warning("Failed to load background image.", e);
        }

        // try obtaining our title text from a system property
        String tstr = null;
        try {
            tstr = System.getProperty("game_name");
        } catch (Throwable t) {
        }
        if (tstr == null) {
            tstr = _msgs.get("m.default_title");
        }

        // create a big fat label
        MultiLineLabel title = new MultiLineLabel(tstr.toUpperCase(), MultiLineLabel.CENTER);
        title.setFont(CardBoxUI.TitleFontLarge);
        box.add(title);

        // float the logon bits side-by-side inside the wider panel
        HGroupLayout hlay = new HGroupLayout();
        hlay.setOffAxisJustification(HGroupLayout.BOTTOM);
        JPanel hbox = new JPanel(hlay);
        hbox.setOpaque(false);
        box.add(hbox);

        if (_client.inDevMode())
        {
        	// Create our token text box 
        	VGroupLayout vlay = new VGroupLayout();
        	vlay.setOffAxisJustification(VGroupLayout.RIGHT);
        	JPanel subbox = new JPanel(vlay);
        	subbox.setOpaque(false);
        	hbox.add(subbox);
        	JPanel bar = new JPanel(new HGroupLayout(GroupLayout.NONE));
        	bar.add(new JLabel("Token"));
        	bar.setOpaque(false);
        	_token = new JTextField();
        	_token.setPreferredSize(new Dimension(200, 20));
        	_token.setActionCommand("logon");
        	_token.addActionListener(this);
        	bar.add(_token);
        	subbox.add(bar);
        }

        // create the logon button bar
        _logon = new CardBoxButton(_msgs.get("m.logon"));
        _logon.setActionCommand("logon");
        _logon.addActionListener(this);
        hbox.add(_logon);

        _status = new MultiLineLabel(_msgs.get("m.please_logon"));
        box.add(_status);

        // we'll want to listen for logon failure
        _ctx.getClient().addClientObserver(this);
    }

    /**
     * Informs the logon panel that we're auto-logging on. This clears out any authentication info
     * that was slurped in from prefs and disables the interface.
     */
    public void setAutoLoggingOn ()
    {
        setLogonEnabled(false);
    }

    public void actionPerformed (ActionEvent e)
    {
	String cmd = e.getActionCommand();
	if (cmd.equals("logon")) {
        logon();
	} else {
	    System.out.println("Unknown action event: " + cmd);
	}
    }

    // documentation inherited from interface
    public void clientWillLogon (Client client)
    {
        // nada
    }

    // documentation inherited from interface
    public void clientDidLogon (Client client)
    {
        _status.setText(_msgs.get("m.logon_success") + "\n");
    }

    // documentation inherited from interface
    public void clientDidLogoff (Client client)
    {
        _status.setText(_msgs.get("m.logged_off") + "\n");
        setLogonEnabled(true);
    }

    // documentation inherited from interface
    public void clientDidClear (Client client)
    {
        // nada
    }

    // documentation inherited from interface
    public void clientFailedToLogon (Client client, Exception cause)
    {
        String msg;
        if (cause instanceof LogonException) {
            msg = MessageBundle.compose("m.logon_failed", cause.getMessage());
        } else {
            msg = MessageBundle.tcompose("m.logon_failed", cause.getMessage());
            cause.printStackTrace(System.err);
        }
        _status.setText(_msgs.xlate(msg) + "\n");
        setLogonEnabled(true);
    }

    // documentation inherited from interface
    public void clientObjectDidChange (Client client)
    {
        // nothing we can do here...
    }

    // documentation inherited from interface
    public void clientConnectionFailed (Client client, Exception cause)
    {
        String msg = MessageBundle.tcompose("m.connection_failed",
                                            cause.getMessage());
        _status.setText(_msgs.xlate(msg) + "\n");
        setLogonEnabled(true);
    }

    // documentation inherited from interface
    public boolean clientWillLogoff (Client client)
    {
        // no vetoing here
        return true;
    }

    // documentation inherited
    @Override
    protected void paintComponent (Graphics g)
    {
        super.paintComponent(g);
        int width = getWidth(), height = getHeight();

        // first tile the flowers
        if (_flowers != null) {
            Graphics2D gfx = (Graphics2D)g;
            ImageUtil.tileImage(gfx, _flowers, 0, 0, width, height);
        }

        // then draw the background image centered on top of that
        if (_bgimg != null) {
            g.drawImage(_bgimg, (width - _bgimg.getWidth())/2,
                        (height - _bgimg.getHeight())/2, null);
        }
    }

    protected void logon ()
    {
        // disable further logon attempts until we hear back
        setLogonEnabled(false);

        String server = _ctx.getClient().getHostname();
        int port = _ctx.getClient().getPorts()[0];
        String msg = MessageBundle.tcompose("m.logging_on", server, String.valueOf(port));
        _status.setText(_msgs.xlate(msg) + "\n");

        if (_client.inDevMode())
        {
        	// In dev mode, we get our session ID from the user, who kindly pasted it into
        	// the provided text box. Take this opportunity to gather that info.
        	String session_id = _token.getText().trim();
        	if (!StringUtil.isBlank(session_id))
        	{
        		_client.setSession(session_id);
        	}
        	
        }
        
    	// Since the CardBoxClient has its session identifier loaded by the applet,
        // that makes our job quite easy.
        Client client = _ctx.getClient();
        client.setCredentials(_client.createCredentials());
        client.logon();
    }

    protected void setLogonEnabled (boolean enabled)
    {
        _logon.setEnabled(enabled);
    }

    protected CardBoxContext _ctx;
    protected CardBoxClient _client;
    protected MessageBundle _msgs;
    
    protected JTextField _token;
    protected JButton _logon;
    protected MultiLineLabel _status;

    protected BufferedImage _bgimg;
    protected Mirage _flowers;
}
