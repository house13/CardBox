package com.hextilla.cardbox.lobby.friendlist;

import java.util.Vector;

import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.util.CardBoxContext;

public class OnlineStatus
{
	public interface StatusObserver
	{
		public void statusUpdated (CardBoxName user, OnlineStatus status);
	}
	
	public OnlineStatus (CardBoxContext ctx, CardBoxName user)
	{
		this(ctx, user, ONLINE);
	}
	
	public OnlineStatus (CardBoxContext ctx, CardBoxName user, byte status)
	{
		_ctx = ctx;
		_user = user;
		_status = status;
	}
	
	public boolean setStatus (byte status)
	{
		// Ignore changes that 
		if (_status != status) {
			byte from = _status;
			_status = status;
			if (status == ONLINE || status == INGAME)
				updateNotify();
			return true;
		} else {
			return false;
		}
	}
	
	public boolean setStatus (OnlineStatus status)
	{
		return setStatus(status.getStatus());
	}
	
	public byte getStatus ()
	{
		return _status;
	}
	
	public void addListener (StatusObserver so)
	{
		if (so != null)
			_observers.add(so);
	}
	
	public void clearListeners ()
	{
		_observers.clear();
	}
	
	@Override
	public String toString ()
	{
		String message;
		
		switch (_status) {
		case ONLINE:
			message = ONLINE_MSG;
			break;
		case WAITING:
			message = WAITING_MSG;
			break;
		case INGAME:
			message = INGAME_MSG;
			break;
		default:
			message = ONLINE_MSG;
			break;
		}
		
		return _ctx.xlate(STATUS_MSGS, message); 
	}
	
	protected void updateNotify ()
	{
		for (StatusObserver observer : _observers)
		{
			observer.statusUpdated(_user, this);
		}
	}
	
	/**
	 * User State Table
	 * ################
	 * ONLINE - User in lobby, has no games
	 * WAITING - User in lobby, in matchmaking
	 * INGAME - User elsewhere, has a game
	 */
	public static final byte ONLINE = 1;
	public static final byte WAITING = 2;
	public static final byte INGAME = 4;
	
	protected static final String STATUS_MSGS = "client.friend";
	
	protected static final String ONLINE_MSG = "m.online";
	protected static final String WAITING_MSG = "m.waiting";
	protected static final String INGAME_MSG = "m.ingame";
	
	protected CardBoxContext _ctx;
	/** The user whose status we represent */
	protected CardBoxName _user;
	/** Our current status value */
	protected byte _status;
	
	protected Vector<StatusObserver> _observers = new Vector<StatusObserver>();
}
