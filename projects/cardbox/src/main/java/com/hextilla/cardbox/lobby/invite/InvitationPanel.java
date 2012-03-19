package com.hextilla.cardbox.lobby.invite;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
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
	public InvitationPanel (CardBoxContext ctx, JPanel dp)
	{
		_ctx = ctx;
		_idtr = _ctx.getInvitationDirector();
		
		this.setLayout(new CardLayout());
		
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
		
		_defaultPanel = dp;
		this.add(_defaultPanel, DEFAULT_CARD);
		
		createInvitationPanel();
		this.add(_invitePanel, INVITE_CARD);
		
		_idtr.addInvitationListener(this);
		
		CardLayout cl = (CardLayout)this.getLayout();
		cl.show(this, DEFAULT_CARD);
	}

	@Override
	public void invitationIncoming(Invitation invite)
	{
		log.info("InvitationPanel: Invitation Incoming!", "invite", invite);
		_incoming = invite;
		updated();
	}
	
	@Override
	public void invitationCancelled(Invitation invite)
	{
		// Our currently-being-processed incoming invite got cancelled, bummer.
		if (_incoming != null && _incoming.inviteId == invite.inviteId) {
			// We could probably use this opportunity to offer some feedback to the user
			if (!getNext())
				_incoming = null;
			updated();
		} else {
			log.info("InvitationPanel: Some sort of Invitation Cancelled!", "invite", invite);
		}
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
			_idtr.accept();
			if (!getNext())
				_incoming = null;
			updated();
		}
	}
	
	public void refuse()
	{
		if (_incoming != null)
		{
			_idtr.refuse("Invite Refused by " + _ctx.getUsername());
			if (!getNext())
				_incoming = null;
			updated();
		}
	}
	
	protected boolean getNext()
	{
		if (_idtr.hasPending())
		{
			_incoming = _idtr.getNextInvite();
			return true;
		} else {
			_incoming = null;
			return false;
		}
	}
	
	protected boolean updated()
	{
		CardLayout cl = (CardLayout)this.getLayout();
		if (_incoming == null)
		{
			log.info("herp");
			cl.show(this, DEFAULT_CARD);
			repaint();
			return true;
		}

		CardBoxName opponent = (CardBoxName)_incoming.opponent;
		String invite = MessageBundle.tcompose(INVITE_MSG, opponent.getFriendlyName().toString());
		invite = _ctx.xlate(INVITE_MSGS, invite); 
		_inviteMsg.setText(invite);
		
		log.info("Setting text to: " + invite);
		
		cl.show(this, INVITE_CARD);
		
		repaint();
		
		return true;
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
		_idtr.removeInvitationListener(this);
		//this.setLayout(new GridLayout(1, 1, 0, 0));
	}
	
	protected CardBoxContext _ctx;
	
	protected PlaceObject _lobj;
	
	protected InvitationDirector _idtr;
	
	protected CardBoxButton _accept;
	protected CardBoxButton _refuse;
	
	protected MultiLineLabel _inviteMsg;
	
	protected JPanel _invitePanel = null;
	protected JPanel _defaultPanel = null;
	
	protected Invitation _incoming = null;
	
	protected static final String DEFAULT_CARD = "default";
	protected static final String INVITE_CARD = "invite";
	
	protected static final String INVITE_MSGS = "client.friend";
	
	protected static final String ACCEPT_MSG = "m.accept";
	protected static final String REFUSE_MSG = "m.refuse";
	protected static final String INVITE_MSG = "m.invite_received";
	
	protected static Dimension STATUS_MAX_SIZE = new Dimension(180, 40);
    protected static Dimension STATUS_MIN_SIZE = new Dimension(100, 25); 
    
    protected static Dimension BUTTON_MAX_SIZE = new Dimension(90, 40);
    protected static Dimension BUTTON_MIN_SIZE = new Dimension(50, 25); 
}
