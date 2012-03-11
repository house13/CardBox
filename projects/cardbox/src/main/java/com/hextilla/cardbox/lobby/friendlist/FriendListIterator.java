package com.hextilla.cardbox.lobby.friendlist;

import java.util.Iterator;

import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.facebook.client.SocialDirector.FriendIterator;

public class FriendListIterator implements FriendIterator 
{
	public FriendListIterator(Iterator<FriendEntry> it)
	{
		_iterator = it;
	}

	@Override
	public CardBoxName next()
	{
		if (hasNext()) {
			FriendEntry fe = _iterator.next();
			return fe.getName();
		} else {
			return null;
		}
	}

	@Override
	public boolean hasNext()
	{
		return _iterator.hasNext();
	}

	protected Iterator<FriendEntry> _iterator;
}
