package com.hextilla.cardbox.lobby.friendlist;

import javax.swing.ImageIcon;
import com.hextilla.cardbox.client.CardBoxUI;
import com.hextilla.cardbox.facebook.CardBoxName;

// A friend object
public class FriendEntry
	implements Comparable<CardBoxName>
{
	private CardBoxName _name = null;
	private ImageIcon _displayPic = null;
	// Also encapsulate game/table data if any
	
	public FriendEntry(CardBoxName name)
	{
		_name = name;
	}
	
	public CardBoxName getName()
	{
		return _name;
	}
	
	public ImageIcon getDisplayPic()
	{
		if (_displayPic == null)
		{
			return CardBoxUI.getDefaultDisplayPic();
		}
		return _displayPic;
	}

	@Override
	public int compareTo(CardBoxName cbn) {
		return _name.compareTo(cbn);
	}
	
	@Override 
	public boolean equals (Object other)
	{
		if (other instanceof CardBoxName)
		{
			return _name.equals((CardBoxName)other);
		}
		else if (other instanceof FriendEntry)
		{
			return _name.equals(((FriendEntry)other).getName());
		}
		return false;
	}
	
}
