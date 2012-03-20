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
	public FriendSelectionContext(CardBoxContext ctx, ButtonContext parent)
	{
		this(ctx);
		_parent = parent;
	}
	
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
		if (_child != null)
			return _child.getText();
		
		return _ctx.xlate(BUTTON_MSGS, DEFAULT_MSG);
	}

	@Override
	public boolean getEnabled() {
		// Ideally we want to avoid doing the work ourselves.
		if (_selected == null)
			return false;
		if (_child == null)
			return false;
		
		return _child.getEnabled();
	}

	@Override
	public void addObserver(ButtonContextObserver ob) {
		_observers.add(ob);
	}

	@Override
	public void clear() {
		_observers.clear();
		_child = null;
		if (_selected != null)
			_selected.getStatus().removeListener(this);
		_selected = null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (_child != null) {
			_child.actionPerformed(e);
			updated();
		}
			
	}

	// In this context, an update means a friend was selected
	@Override
	public void updateContext(Object ob) {
		if (ob instanceof FriendEntry)
		{
			FriendEntry fe = (FriendEntry)ob;
			setFriend(fe);
			updated();
		} else {
			setFriend(null);
			updated();
		}
	}
	
	public void setFriend(FriendEntry fe)
	{
		if (fe != null)
		{
			if (_selected != null)
				_selected.getStatus().removeListener(this);
			_selected = fe;
			if (_child != null)
				_child.clear();
			switch (_selected.getStatus().getStatus())
			{
			case OnlineStatus.ONLINE:
				_child = new InvitationContext(_ctx, _selected.getName(), this);
				break;
			default:
				_child = null;
				break;
			}
			_selected.getStatus().addListener(this);
		} else {
			_selected = null;
		}
	}
	
	@Override
	public void statusUpdated(CardBoxName user, OnlineStatus status) {
		// Update our delegate only if it's for the same person...
		if (_selected != null && _selected.getName().equals(user))
		{
			if (_child != null)
				_child.clear();
			switch (status.getStatus())
			{
			case OnlineStatus.ONLINE:
				_child = new InvitationContext(_ctx, user, this);
				break;
			default:
				_child = null;
				break;
			}
			log.info("FriendSelectionContext: Status updated to", "status", status);
			updated();
		}
	}
	
	@Override
	public void refresh()
	{
		// Only service the request if it's probably the child making it
		if (_child != null)
		{
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
	
	/** Generally this context works on the top level */
	protected  ButtonContext _parent = null;
	
	/** Our main job is to intelligently handle updates from the friend list, delegate out the work */
	protected ButtonContext _child = null;
	
	protected Vector<ButtonContextObserver> _observers = new Vector<ButtonContextObserver>();
	
	protected static final String __NAME = "FriendSelectionContext";
	protected static final String BUTTON_MSGS = "client.friend";
	protected static final String DEFAULT_MSG = "m.button_default";
}
