package com.hextilla.cardbox.lobby.invite;

import com.hextilla.cardbox.util.CardBoxContext;
import com.threerings.parlor.client.Invitation;
import com.threerings.parlor.client.InvitationHandler;
import com.threerings.parlor.client.InvitationResponseObserver;
import com.threerings.parlor.game.data.GameConfig;
import com.threerings.presents.client.BasicDirector;

public class InvitationDirector extends BasicDirector 
	implements InvitationHandler, InvitationResponseObserver
{
	
	public InvitationDirector(CardBoxContext ctx)
	{
		super(ctx);
		_ctx = ctx;
	}

	// InvitationHandler permits us to accept invitations from others
	/** BEGIN InvitationHandler methods */
	
	/** 
	 * Called when an invitation is received from another player.
	 * @param invite The received invitation
	 */
	@Override
	public void invitationReceived(Invitation invite) 
	{
		// Draw a dialog
	}
	
	/** 
	 * Called when an invitation is cancelled by the inviting player.
	 * @param invite The received invitation
	 */
	@Override
	public void invitationCancelled(Invitation invite) 
	{
		// Dismiss dialog, 
	}
	
	/** END   InvitationHandler methods */
	
	// InvitationResponseObserver permits us to listen to invites we've sent
	/** BEGIN InvitationResponseObserver methods */
	
	@Override
	public void invitationAccepted(Invitation invite)
	{
	}
	
	@Override
	public void invitationRefused(Invitation invite, String message)
	{
	}
	
	@Override
	public void invitationCountered(Invitation invite, GameConfig config)
	{
	}
	
	/** END   InvitationResponseObserver methods */
	
	/** The giver of life and services */
	protected CardBoxContext _ctx;
}
