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
		_inlisteners.add(il);
	}
	
	public void addResultListener(InvitationResultListener rl)
	{
		_relisteners.add(rl);
	}
	
	public boolean removeInvitationListener(InvitationListener il)
	{
		return _inlisteners.remove(il);
	}
	
	public boolean removeResultListener(InvitationResultListener rl)
	{
		return _relisteners.remove(rl);
	}
	
	public boolean hasPending()
	{
		return _incoming != null || _pending.size() > 0;
	}
	
	public boolean hasOutgoing()
	{
		return _outgoing != null;
	}
	
	/** Remove the given invite from our pending set, then accept it */
	public void accept()
	{
		if (_incoming == null) {
			log.info("No incoming message to accept");
			return;
		}
			
		int id = _incoming.inviteId;
		Invitation removed = _pending.remove(id);
		if (removed != null) {
			_incoming.accept();
			_incoming = null;
		}
	}
	
	/** Remove the given invite from our pending set, then refuse it */
	public void refuse(String msg)
	{
		if (_incoming == null) {
			log.info("No incoming message to refuse");
			return;
		}
		
		int id = _incoming.inviteId;
		Invitation removed = _pending.remove(id);
		if (removed != null) {
			_incoming.refuse(msg);
			_incoming = null;
		}
	}
	
	public void cancelOutgoing()
	{
		if (_outgoing != null)
			_outgoing.cancel();
	}
	
	/** The _incoming Invite has been handled, grab the next one in the queue */
	public Invitation getNextInvite()
	{
		Invitation invite = null;
		while (!_incomingQ.isEmpty())
		{
			int id = _incomingQ.remove();
			invite = _pending.get(id);
			if (invite != null)
			{
				_incoming = invite;
				return invite;
			}
		}
		_incoming = invite;
		return invite;
	}
	
	/** The public interface everyone will use to send invitations */
	public void sendInvitation(CardBoxName friend)
	{
		// You can't send an invite to yourself
		//if (friend.equals((CardBoxName)_ctx.getUsername()))
		//	return;
		if (_outgoing == null)
			_outgoing = _pdtr.invite(friend, _config, this);
	}
	
	/** Should be called whenever you're leaving the lobby */
	public void clearInvitations()
	{
		if (_outgoing != null)
		{
			_outgoing.cancel();
			log.info("Cancelled my outgoing invitation");
			_outgoing = null;
		}
		if (_incoming != null)
		{
			_incoming.refuse("User leaving the lobby");
			log.info("Cancelled my outgoing invitation");
			_outgoing = null;
		}
		for (int id : _incomingQ)
		{
			Invitation invite = _pending.remove(id);
			if (invite != null)
			{
				invite.refuse("User leaving the lobby");
				log.info("Refusing pending invitation", "id", id);
			}
		}
		_incomingQ.clear();
		_pending.clear();
		_inlisteners.clear();
		_relisteners.clear();
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
		if (_incoming == null && _pending.size() == 0) {
			_incoming = invite;
			log.info("Incoming Invite being pushed to listeners");
			for (InvitationListener listener : _inlisteners)
			{
				listener.invitationIncoming(invite);
			}
		} else {
			log.info("Incoming Invite being pushed onto queue");
			_incomingQ.add(invite.inviteId);
		}
	}
	
	protected void outgoingHandled()
	{
		if (_outgoing == null)
			for (InvitationResultListener listener : _relisteners)
			{
				listener.outgoingHandled();
			}
	}
	
	/** 
	 * Called when an invitation is cancelled by the inviting player.
	 * @param invite The received invitation
	 */
	@Override
	public void invitationCancelled(Invitation invite) 
	{
		// Won't ever get called by anything (ParlorDirector doesn't handle cancellations well)
		log.info("Invitation cancelled", "invite", invite); 
		//_pending.remove(invite.inviteId);
	}
	
	/** END   InvitationHandler methods */
	
	// InvitationResponseObserver permits us to listen to invites we've sent
	/** BEGIN InvitationResponseObserver methods */
	
	@Override
	public void invitationAccepted(Invitation invite)
	{
		log.info("Invitation was accepted!!!!", "invite", invite);
		_outgoing = null;
		outgoingHandled();
	}
	
	@Override
	public void invitationRefused(Invitation invite, String message)
	{
		// Invitation cancellation overrides the refusal function...
		if (_outgoing != null && _outgoing.inviteId == invite.inviteId)
		{
			log.info("Invitation was refused!!!!", "invite", invite, "message", message);
			_outgoing = null;
			outgoingHandled();
		} else if (_incoming != null && _incoming.inviteId == invite.inviteId) {
			// They only really need to know if the current invite being handled got cancelled.
			for (InvitationListener listener : _inlisteners)
			{
				listener.invitationCancelled(invite);
			}
			_incoming = null;
		} else if (_pending.containsKey(invite.inviteId)) {
			// Just less work to do later on I guess
			_pending.remove(invite.inviteId);
			log.info("Invitation has been cancelled by the inviter", "invite", invite, "message", message);
		}
	}
	
	@Override
	public void invitationCountered(Invitation invite, GameConfig config)
	{
		log.info("Invitation was countered!!!!", "invite", invite);
		_outgoing = null;
		outgoingHandled();
	}
	
	/** END   InvitationResponseObserver methods */
	
	/** The giver of life and services */
	protected CardBoxContext _ctx;
	
	protected ParlorDirector _pdtr;
	
	/** The friendly game configuration we use in our invitations */
	protected GameConfig _config = null;
	
	/** Limit you to a single outgoing invite at a time. */
	protected Invitation _outgoing = null;
	
	/** Hang onto the incoming invite currently being processed */
	protected Invitation _incoming = null;
	
	protected Vector<InvitationListener> _inlisteners = new Vector<InvitationListener>();
	protected Vector<InvitationResultListener> _relisteners = new Vector<InvitationResultListener>();
	
	protected LinkedList<Integer> _incomingQ = new LinkedList<Integer>();
	protected HashIntMap<Invitation> _pending = new HashIntMap<Invitation>();
}
