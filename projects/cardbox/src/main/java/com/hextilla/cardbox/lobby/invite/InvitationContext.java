package com.hextilla.cardbox.lobby.invite;

import static com.hextilla.cardbox.lobby.Log.log;

import java.awt.event.ActionEvent;

import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.util.CardBoxContext;
import com.threerings.util.MessageBundle;

public class InvitationContext
	implements ButtonContext
{
	public InvitationContext (CardBoxContext ctx, CardBoxName friend)
	{
		log.info("InvitationContext: created", "friend", friend);
		_ctx = ctx;
		_friend = friend;
		_idtr = _ctx.getInvitationDirector();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		_idtr.sendInvitation(_friend);
		
	}
	@Override
	public String getName() {
		return __NAME;
	}
	@Override
	public String getText() {
		String invite = MessageBundle.tcompose(INVITE_MSG, _friend.getFriendlyName().toString());
		invite = _ctx.xlate(BUTTON_MSGS, invite); 
		return invite;
	}
	@Override
	public boolean getEnabled() {
		return true;
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
		
	}
	
	protected CardBoxContext _ctx;
	
	protected InvitationDirector _idtr;
	
	protected CardBoxName _friend;
	
	protected static final String BUTTON_MSGS = "client.friend";
	protected static final String INVITE_MSG = "m.button_invite";
	
	protected static final String __NAME = "InvitationContext";
}
