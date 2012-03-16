package com.hextilla.cardbox.lobby.friendlist;

import java.awt.Font;

import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.util.CardBoxContext;
import com.threerings.util.MessageBundle;

public class OnlineStatus
{
	public OnlineStatus (CardBoxContext ctx, CardBoxName user)
	{
		_ctx = ctx;
		_user = user;
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
	
	/**
	 * User State Table
	 * ################
	 * ONLINE - User in lobby, has no games
	 * WAITING - User in lobby, in matchmaking
	 * INGAME - User elsewhere, has a game
	 */
	public static final int ONLINE = 200;
	public static final int WAITING = 250;
	public static final int INGAME = 300;
	
	protected static final String STATUS_MSGS = "client.friend";
	
	protected static final String ONLINE_MSG = "m.online";
	protected static final String WAITING_MSG = "m.waiting";
	protected static final String INGAME_MSG = "m.ingame";
	
	protected CardBoxContext _ctx;
	/** The user whose status we represent */
	protected CardBoxName _user;
	protected int _status = ONLINE;
}
