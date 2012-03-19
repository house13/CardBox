package com.hextilla.cardbox.lobby.invite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JPanel;

import com.hextilla.cardbox.client.CardBoxUI;
import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.swing.CardBoxButton;
import com.hextilla.cardbox.swing.PlayerCountPanel;
import com.hextilla.cardbox.util.CardBoxContext;
import com.samskivert.swing.MultiLineLabel;
import com.threerings.crowd.data.PlaceObject;
import com.threerings.parlor.client.Invitation;
import com.threerings.util.MessageBundle;

import static com.hextilla.cardbox.lobby.Log.log;

public class InvitationPanel extends JPanel
	implements InvitationListener 
{
	public InvitationPanel (CardBoxContext ctx)
	{
		log.info("Setting up the InvitationPanel");
		_ctx = ctx;
		_idtr = _ctx.getInvitationDirector();
		
		//this.setLayout(new GridLayout(1,1,0,0));
		
		//this.setLayout(new GridLayout(1, 1, 0, 0));
		
		_inviteMsg = new MultiLineLabel("", MultiLineLabel.LEFT);
		_inviteMsg.setFont(CardBoxUI.AppFontSmall);
		
		_accept = new CardBoxButton(_ctx.xlate(INVITE_MSGS, ACCEPT_MSG));
		_accept.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				accept();
			}
		});
		
		_refuse = new CardBoxButton(_ctx.xlate(INVITE_MSGS, REFUSE_MSG));
		_refuse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refuse();
			}
		});
		
		_inviteMsg.setMaximumSize(STATUS_MAX_SIZE);
		_inviteMsg.setPreferredSize(STATUS_MAX_SIZE);
		_inviteMsg.setMinimumSize(STATUS_MIN_SIZE);
		
		_accept.setMaximumSize(BUTTON_MAX_SIZE);
		_accept.setPreferredSize(BUTTON_MAX_SIZE);
		_accept.setMinimumSize(BUTTON_MIN_SIZE);
		
		_refuse.setMaximumSize(BUTTON_MAX_SIZE);
		_refuse.setPreferredSize(BUTTON_MAX_SIZE);
		_refuse.setMinimumSize(BUTTON_MIN_SIZE);
		
		//createDefaultPanel();
		//this.add(_defaultPanel);
		
		createInvitationPanel();
		this.add(_invitePanel);
		
		_idtr.addInvitationListener(this);
		
		log.info("InvitationPanel set up!!");
	}

	@Override
	public void invitationIncoming(Invitation invite)
	{
		log.info("InvitationPanel: Invitation Incoming!", "invite", invite);
		_incoming = invite;
		updated();
	}
	
	@Override
	public void setBackground(Color bg)
	{
		if (_defaultPanel != null)
			_defaultPanel.setBackground(bg);
		if (_invitePanel != null)
			_invitePanel.setBackground(bg);
	}
	
	@Override
	public void setForeground(Color fg)
	{
		if (_defaultPanel != null)
			_defaultPanel.setForeground(fg);
		if (_invitePanel != null)
			_invitePanel.setForeground(fg);
	}
	
	public void accept()
	{
		if (_incoming != null)
		{
			_incoming.accept();
			if (!getNext())
				_incoming = null;
			updated();
		}
	}
	
	public void refuse()
	{
		if (_incoming != null)
		{
			_incoming.cancel();
			if (!getNext())
				_incoming = null;
			updated();
		}
	}
	
	public void willEnterPlace(PlaceObject place) {
		_lobj = place;			
	}
	
	protected boolean getNext()
	{
		if (_idtr.hasPending())
		{
			_incoming = _idtr.getNextInvite();
			return true;
		}
		
		return false;
	}
	
	protected boolean updated()
	{
		if (_incoming == null)
		{
			log.info("herp");
			clear();
			createDefaultPanel();
			this.add(_defaultPanel);
			repaint();
			return true;
		}

		CardBoxName opponent = (CardBoxName)_incoming.opponent;
		String invite = MessageBundle.tcompose(INVITE_MSG, opponent.getFriendlyName().toString());
		invite = _ctx.xlate(INVITE_MSGS, invite); 
		_inviteMsg.setText(invite);
		
		log.info("Setting text to: " + invite);
		
		clear();
		createInvitationPanel();
		this.add(_invitePanel);
		
		repaint();
		
		return true;
	}
	
	protected void createDefaultPanel()
	{
		_defaultPanel = new PlayerCountPanel(_ctx, 0);	
		_defaultPanel.setBackground(getBackground());
	}
	
	protected void createInvitationPanel()
	{
		_invitePanel = new JPanel();
		GroupLayout layout = new GroupLayout(_invitePanel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
		_invitePanel.setLayout(layout);
		_invitePanel.setBackground(getBackground());
		
        // Horizontal Grouping
        // parallel{ sequential{ parallel{optsButton, _matchMaker}, friendPanel}, chatPane}
        layout.setHorizontalGroup(
        		layout.createSequentialGroup()
			    		  .addComponent(_inviteMsg)	  
	    				  .addComponent(_accept)
	    				  .addComponent(_refuse)
	    		);    
        
        // Horizontal Grouping
        // sequential{ parallel{ sequential{optsButton, _matchMaker}, friendPanel}, chatPane}
        layout.setVerticalGroup(
        		layout.createParallelGroup(GroupLayout.Alignment.CENTER, true)
    				  .addComponent(_inviteMsg)
    				  .addComponent(_accept)
    				  .addComponent(_refuse)
	    		 
	    		);
	}
	
	protected void clear()
	{
		this.removeAll();
		_invitePanel = null;
		_defaultPanel = null;
		//this.setLayout(new GridLayout(1, 1, 0, 0));
	}
	
	protected CardBoxContext _ctx;
	
	protected PlaceObject _lobj;
	
	protected InvitationDirector _idtr;
	
	protected CardBoxButton _accept;
	protected CardBoxButton _refuse;
	
	protected MultiLineLabel _inviteMsg;
	
	protected JPanel _invitePanel = null;
	protected PlayerCountPanel _defaultPanel = null;
	
	protected Invitation _incoming = null;
	
	protected static final String INVITE_MSGS = "client.friend";
	
	protected static final String ACCEPT_MSG = "m.accept";
	protected static final String REFUSE_MSG = "m.refuse";
	protected static final String INVITE_MSG = "m.invite_received";
	
	protected static Dimension STATUS_MAX_SIZE = new Dimension(180, 40);
    protected static Dimension STATUS_MIN_SIZE = new Dimension(100, 25); 
    
    protected static Dimension BUTTON_MAX_SIZE = new Dimension(90, 40);
    protected static Dimension BUTTON_MIN_SIZE = new Dimension(50, 25);   
}
