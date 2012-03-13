package com.hextilla.cardbox.lobby.friendlist;

import javax.swing.ImageIcon;
import com.hextilla.cardbox.client.CardBoxUI;
import com.hextilla.cardbox.facebook.CardBoxName;

// A friend object
public class FriendEntry
	implements Comparable<FriendEntry>
{
	public FriendEntry(CardBoxName name)
	{
		this(name, null);
	}
	
	public FriendEntry(CardBoxName name, ImageIcon pic)
	{
		_name = name;
		_pic = pic;
	}
	
	public CardBoxName getName()
	{
		return _name;
	}
	
	public ImageIcon getDisplayPic()
	{
		if (_pic == null)
		{
			return CardBoxUI.getDefaultDisplayPic();
		}
		return _pic;
	}
	
	public boolean update(FriendEntry other)
	{
		boolean changed = false;
		if (this.equals(other)) {
			_pic = other.getDisplayPic();
			changed = true;
			
			// Also potentially update game data
		}
		return changed;
	}

	@Override
	public int compareTo(FriendEntry fe)
	{
		return _name.compareTo(fe.getName());
	}
	
	@Override 
	public boolean equals (Object other)
	{
		if (other instanceof FriendEntry)
		{
			return _name.equals(((FriendEntry)other).getName());
		}
		else if (other instanceof CardBoxName)
		{
			return _name.equals((CardBoxName)other);
		}
		return false;
	}
	
	protected CardBoxName _name = null;
	protected ImageIcon _pic = null;
	// Also encapsulate game/table data if any
}
