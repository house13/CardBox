package com.hextilla.cardbox.lobby.friendlist;

import java.awt.event.ActionEvent;
import java.util.Vector;

import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.lobby.friendlist.OnlineStatus.StatusObserver;
import com.hextilla.cardbox.lobby.invite.ButtonContext;
import com.hextilla.cardbox.lobby.invite.ButtonContextObserver;
import com.hextilla.cardbox.lobby.invite.InvitationContext;
import com.hextilla.cardbox.util.CardBoxContext;

import static com.hextilla.cardbox.lobby.Log.log;

public class FriendSelectionContext 
	implements ButtonContext, StatusObserver
{
	public FriendSelectionContext(CardBoxContext ctx)
	{
		log.info("FriendSelectionContext Created");
		_ctx = ctx;
	}

	@Override
	public String getName() {
		return __NAME;
	}

	@Override
	public String getText() {
		if (_delegate != null)
			return _delegate.getText();
		
		return _ctx.xlate(BUTTON_MSGS, DEFAULT_MSG);
	}

	@Override
	public boolean getEnabled() {
		// Ideally we want to avoid doing the work ourselves.
		if (_selected == null)
			return false;
		if (_delegate == null)
			return false;
		
		return _delegate.getEnabled();
	}

	@Override
	public void addObserver(ButtonContextObserver ob) {
		_observers.add(ob);
	}

	@Override
	public void clear() {
		_observers.clear();
		_delegate = null;
		_selected = null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (_delegate != null)
			_delegate.actionPerformed(e);
	}

	// In this context, an update means a friend was selected
	@Override
	public void updateContext(Object ob) {
		if (ob instanceof FriendEntry)
		{
			FriendEntry fe = (FriendEntry)ob;
			setFriend(fe);
			updated();
		}
	}
	
	public void setFriend(FriendEntry fe)
	{
		_selected = fe;
		if (_selected != null)
		{
			switch (_selected.getStatus().getStatus())
			{
			case OnlineStatus.ONLINE:
			case OnlineStatus.WAITING:
			case OnlineStatus.LEAVING:
				_delegate = new InvitationContext(_ctx, _selected.getName());
				break;
			default:
				_delegate = null;
				break;
			}
		}
	}
	
	@Override
	public void statusUpdated(CardBoxName user, OnlineStatus status) {
		log.info("FriendSelectionContext: Status updated to", "status", status);
		// Update our delegate only if we're making a state transition
		if (_selected != null && !_selected.getStatus().equals(status))
		{
			switch (status.getStatus())
			{
			case OnlineStatus.ONLINE:
			case OnlineStatus.WAITING:
			case OnlineStatus.LEAVING:
				_delegate = new InvitationContext(_ctx, user);
				break;
			default:
				_delegate = null;
				break;
			}
			updated();
		}
	}
	
	protected void updated()
	{
		if (_selected != null)
		{
			
		}
		
		for (ButtonContextObserver ob : _observers)
		{
			ob.contextUpdated();
		}
	}
	
	protected CardBoxContext _ctx;
	
	protected FriendEntry _selected = null;
	
	/** Our main job is to intelligently handle updates from the friend list, delegate out the work */
	protected ButtonContext _delegate = null;
	
	protected Vector<ButtonContextObserver> _observers = new Vector<ButtonContextObserver>();
	
	protected static final String __NAME = "FriendSelectionContext";
	protected static final String BUTTON_MSGS = "client.friend";
	protected static final String DEFAULT_MSG = "m.button_default";
}
