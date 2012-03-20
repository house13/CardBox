package com.hextilla.cardbox.lobby.invite;

import static com.hextilla.cardbox.lobby.Log.log;

import java.awt.event.ActionEvent;

import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.util.CardBoxContext;
import com.threerings.util.MessageBundle;

public class InvitationContext
	implements ButtonContext, InvitationResultListener
{
	public InvitationContext (CardBoxContext ctx, CardBoxName friend, ButtonContext parent)
	{
		this(ctx, friend);
		_parent = parent;
	}
	
	public InvitationContext (CardBoxContext ctx, CardBoxName friend)
	{
		log.info("InvitationContext: created", "friend", friend);
		_ctx = ctx;
		_friend = friend;
		_idtr = _ctx.getInvitationDirector();
		_idtr.addResultListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(_mode) {
		case INVITE_MODE:
			_idtr.sendInvitation(_friend);
			break;
		case CANCEL_MODE:
			_idtr.cancelOutgoing();
			break;
		}
		updateMode();
		if (_parent != null)
			_parent.refresh();
	}
	@Override
	public String getName() {
		return __NAME;
	}
	@Override
	public String getText() {
		String text = "";
		switch(_mode) {
		case INVITE_MODE:
			text = MessageBundle.tcompose(INVITE_MSG, _friend.getFriendlyName().toString());
			text = _ctx.xlate(BUTTON_MSGS, text); 
			break;
		case CANCEL_MODE:
			text = _ctx.xlate(BUTTON_MSGS, CANCEL_MSG);
			break;
		}
		return text;
	}
	@Override
	public boolean getEnabled() {
		return true;
	}
	@Override
	public void outgoingHandled() {
		// Should re-enable the invite button
		if (_parent != null) {
			updateMode();
			_parent.refresh();
		}
			
	}
	@Override
	public void addObserver(ButtonContextObserver ob) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updateContext(Object ob) {
		// We should only have one InvitationContext per user
	}
	@Override
	public void clear() {
		_idtr.removeResultListener(this);
	}
	
	@Override
	public void refresh()
	{
		// no-op
	}
	
	protected void updateMode()
	{
		if (_idtr.hasOutgoing()) {
			_mode = CANCEL_MODE;
		} else {
			_mode = INVITE_MODE;
		}
	}
	
	protected CardBoxContext _ctx;
	
	protected InvitationDirector _idtr;
	
	protected CardBoxName _friend;
	
	protected  ButtonContext _parent = null;
	protected  ButtonContext _child = null;
	
	protected byte _mode = INVITE_MODE;
	
	protected static final String BUTTON_MSGS = "client.friend";
	protected static final String INVITE_MSG = "m.button_invite";
	protected static final String CANCEL_MSG = "m.button_cancel";
	
	protected static final byte INVITE_MODE = 1;
	protected static final byte CANCEL_MODE = 2;
	
	protected static final String __NAME = "InvitationContext";
}
