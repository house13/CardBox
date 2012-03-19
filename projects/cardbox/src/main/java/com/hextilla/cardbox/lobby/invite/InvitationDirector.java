package com.hextilla.cardbox.lobby.invite;

import java.util.LinkedList;
import java.util.Vector;

import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.util.CardBoxContext;
import com.samskivert.util.HashIntMap;
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
	
	public void addInvitationListener(InvitationListener il)
	{
		_listeners.add(il);
	}
	
	public boolean hasPending()
	{
		return _pending.size() > 0;
	}
	
	public boolean hasOutgoing()
	{
		return _outgoing != null;
	}
	
	public Invitation getNextInvite()
	{
		Invitation invite = null;
		while (!_incoming.isEmpty())
		{
			int id = _incoming.remove();
			invite = _pending.remove(id);
			if (invite != null)
			{
				return invite;
			}
		}
		return invite;
	}
	
	/** The public interface everyone will use to send invitations */
	public void sendInvitation(CardBoxName friend)
	{
		// You can't send an invite to yourself
		if (friend.equals((CardBoxName)_ctx.getUsername()))
			return;
		if (_outgoing == null)
			_outgoing = _pdtr.invite(friend, _config, this);
	}
	
	/** Should be called whenever you're leaving the lobby */
	public void clearInvitations()
	{
		if (_outgoing != null)
		{
			_outgoing.cancel();
			_outgoing = null;
		}
		for (int id : _incoming)
		{
			Invitation invite = _pending.remove(id);
			if (invite != null)
			{
				invite.cancel();
			}
		}
		_incoming.clear();
		_pending.clear();
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
		_pending.put(invite.inviteId, invite);
		// Notify the listeners only if this is our only invite.
		if (_pending.size() == 1) {
			for (InvitationListener listener : _listeners)
			{
				listener.invitationIncoming(invite);
			}
		} else {
			_incoming.add(invite.inviteId);
		}
	}
	
	/** 
	 * Called when an invitation is cancelled by the inviting player.
	 * @param invite The received invitation
	 */
	@Override
	public void invitationCancelled(Invitation invite) 
	{
		log.info("Invitation cancelled", "invite", invite); 
		_pending.remove(invite.inviteId);
	}
	
	/** END   InvitationHandler methods */
	
	// InvitationResponseObserver permits us to listen to invites we've sent
	/** BEGIN InvitationResponseObserver methods */
	
	@Override
	public void invitationAccepted(Invitation invite)
	{
		log.info("Invitation was accepted!!!!", "invite", invite);
		_outgoing = null;
	}
	
	@Override
	public void invitationRefused(Invitation invite, String message)
	{
		log.info("Invitation was refused!!!!", "invite", invite);
		_outgoing = null;
	}
	
	@Override
	public void invitationCountered(Invitation invite, GameConfig config)
	{
		log.info("Invitation was countered!!!!", "invite", invite);
		_outgoing = null;
	}
	
	/** END   InvitationResponseObserver methods */
	
	/** The giver of life and services */
	protected CardBoxContext _ctx;
	
	protected ParlorDirector _pdtr;
	
	/** The friendly game configuration we use in our invitations */
	protected GameConfig _config = null;
	
	/* Limit you to a single outgoing invite at a time. */
	protected Invitation _outgoing = null;
	
	protected Vector<InvitationListener> _listeners = new Vector<InvitationListener>();
	
	protected LinkedList<Integer> _incoming = new LinkedList<Integer>();
	protected HashIntMap<Invitation> _pending = new HashIntMap<Invitation>();
}
