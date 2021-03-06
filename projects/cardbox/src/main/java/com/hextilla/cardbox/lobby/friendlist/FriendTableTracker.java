package com.hextilla.cardbox.lobby.friendlist;

import static com.hextilla.cardbox.lobby.Log.log;

import java.util.Arrays;
import java.util.Hashtable;

import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.facebook.client.FriendSet;
import com.hextilla.cardbox.facebook.client.SocialDirector;
import com.hextilla.cardbox.util.CardBoxContext;
import com.threerings.parlor.client.SeatednessObserver;
import com.threerings.parlor.client.TableObserver;
import com.threerings.parlor.data.Table;
import com.threerings.util.Name;

public class FriendTableTracker
	implements TableObserver, SeatednessObserver
{
	public FriendTableTracker(CardBoxContext ctx)
	{
		log.info("Creating a new FriendTableTracker");
		_ctx = ctx;
		_sdtr = _ctx.getSocialDirector();
		_friends = _sdtr.getFriends();
	}
	
	/** We should be able to discern a user's state, utilizing the FriendTracker interface */
	public byte getUserStatus(CardBoxName user)
	{
		if (_sdtr.isOnlineFriend(user))
		{
			Table tb = _friendMap.get(user);
			if (tb != null) {
				if (tb.gameOid != -1) {
					return OnlineStatus.INGAME;
				} else {
					return OnlineStatus.WAITING;
				}
			} else {
				return OnlineStatus.ONLINE;
			}
		}
		// We're in a bad way
		return 0;
	}

	@Override
	public void tableAdded(Table table)
	{
		for (Name player : table.players)
		{
			if (player instanceof CardBoxName)
			{
				CardBoxName user = (CardBoxName)player;
				if (_sdtr.isOnlineFriend(user))
				{
					log.info("Table Added", "friend", user, "players", Arrays.toString(table.players), "gameOid", table.gameOid);
					Table oldtable = _friendMap.put(user, table);
					// If a non-null value is returned, this user was still in a table
					if (oldtable != null)
					{
						// maybe do some logic here to find out what's going on
					}
					oldtable = _tableMap.put(table.tableId, table);
					// The game might actually be started already
					if (table.gameOid != -1) 
					{
						_sdtr.updateStatus(user, OnlineStatus.INGAME);
					} else {
						_sdtr.updateStatus(user, OnlineStatus.WAITING);
					}
				}
			}
		}
	}

	@Override
	public void tableUpdated(Table table)
	{
		// If Table.gameOid != -1, the game has started
		log.info("Table Updated", "players", Arrays.toString(table.players), "gameOid", table.gameOid);
		if (table.gameOid != -1)
		{
			for (Name player : table.players)
			{
				if (player instanceof CardBoxName)
				{
					CardBoxName user = (CardBoxName)player;
					if (_sdtr.isOnlineFriend(user))
					{
						Table oldtable = _friendMap.put(user, table);
						_tableMap.put(table.tableId, table);
						// If a non-null value is returned, this user was still in a table
						_sdtr.updateStatus(user, OnlineStatus.INGAME);
					}
				}
			}
		}
	}

	@Override
	public void tableRemoved(int tableId)
	{
		Table tb = _tableMap.remove(tableId);
		if (tb != null)
		{
			for (Name player : tb.players)
			{
				if (player instanceof CardBoxName)
				{
					CardBoxName user = (CardBoxName)player;
					Table oldtb = _friendMap.remove(user);
					if (_sdtr.isOnlineFriend(user))
					{	
						_sdtr.updateStatus(user, OnlineStatus.ONLINE);
					}
				}
			}
		} else {
			// If someone cancels matchmaking
			
		}
	}
	
	public void clear ()
	{
		_tableMap.clear();
		_friendMap.clear();
	}
	
	protected CardBoxContext _ctx;
	
	protected SocialDirector _sdtr;
	
	protected FriendSet _friends;
	
	protected Hashtable<Integer,Table> _tableMap = new Hashtable<Integer,Table>();
	/** Maps friend names to tables they're playing in */
	protected Hashtable<CardBoxName,Table> _friendMap = new Hashtable<CardBoxName,Table>();
	@Override
	public void seatednessDidChange(boolean isSeated) {
	}
}
