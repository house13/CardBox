package com.hextilla.cardbox.lobby.invite;

import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.util.CardBoxContext;
import com.threerings.parlor.client.Invitation;
import com.threerings.parlor.client.InvitationHandler;
import com.threerings.parlor.client.InvitationResponseObserver;
import com.threerings.parlor.client.ParlorDirector;
import com.threerings.parlor.game.data.GameConfig;
import com.threerings.presents.client.BasicDirector;

import static com.hextilla.cardbox.lobby.Log.log;

public class InvitationDirector extends BasicDirector 
	implements InvitationHandler, InvitationResponseObserver
{
	
	public InvitationDirector(CardBoxContext ctx)
	{
		super(ctx);
		_ctx = ctx;
		_pdtr = _ctx.getParlorDirector();
		_pdtr.setInvitationHandler(this);
	}
	
	public void init(GameConfig gc)
	{
		_config = gc;
	}
	
	/** The public interface everyone will use to send invitations */
	public void sendInvitation(CardBoxName friend)
	{
		_pdtr.invite(friend, _config, this);
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
		log.info("Accepting invitation!!!!", "invite", invite);
		invite.accept();
	}
	
	/** 
	 * Called when an invitation is cancelled by the inviting player.
	 * @param invite The received invitation
	 */
	@Override
	public void invitationCancelled(Invitation invite) 
	{
		log.info("Invitation cancelled", "invite", invite); 
	}
	
	/** END   InvitationHandler methods */
	
	// InvitationResponseObserver permits us to listen to invites we've sent
	/** BEGIN InvitationResponseObserver methods */
	
	@Override
	public void invitationAccepted(Invitation invite)
	{
		log.info("Invitation was accepted!!!!", "invite", invite);
	}
	
	@Override
	public void invitationRefused(Invitation invite, String message)
	{
		log.info("Invitation was refused!!!!", "invite", invite);
	}
	
	@Override
	public void invitationCountered(Invitation invite, GameConfig config)
	{
		log.info("Invitation was countered!!!!", "invite", invite);
	}
	
	/** END   InvitationResponseObserver methods */
	
	/** The giver of life and services */
	protected CardBoxContext _ctx;
	
	protected ParlorDirector _pdtr;
	
	/** The friendly game configuration we use in our invitations */
	protected GameConfig _config = null;
}
