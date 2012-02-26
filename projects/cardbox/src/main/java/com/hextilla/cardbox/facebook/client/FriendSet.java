package com.hextilla.cardbox.facebook.client;

import java.util.Hashtable;

import com.hextilla.cardbox.facebook.UserWithPicture;

public class FriendSet 
{
	public FriendSet ()
	{
		_friends = new Hashtable<Long, UserWithPicture>();
	}
	
	public void add (UserWithPicture friend)
	{
		if (friend != null) 
		{
			_friends.put(Long.valueOf(friend.getId()), friend);
		}
	}
	
	public boolean isFriend (long fbId)
	{
		return _friends.containsKey(new Long(fbId));
	}
	
	protected Hashtable<Long, UserWithPicture> _friends;
}
