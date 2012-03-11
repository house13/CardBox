package com.hextilla.cardbox.lobby.friendlist;

import java.awt.MediaTracker;

import javax.swing.ImageIcon;
import com.hextilla.cardbox.client.CardBoxUI;
import com.hextilla.cardbox.facebook.CardBoxName;

// A friend object
public class FriendEntry
	implements Comparable<FriendEntry>
{
	private CardBoxName _name = null;
	private ImageIcon _displayPic = null;
	// Also encapsulate game/table data if any
	
	public FriendEntry(CardBoxName name)
	{
		_name = name;
	}
	
	public FriendEntry(CardBoxName name, ImageIcon pic)
	{
		_name = name;
		_displayPic = pic;
	}
	
	public CardBoxName getName()
	{
		return _name;
	}
	
	public ImageIcon getDisplayPic()
	{
		if (_displayPic == null || _displayPic.getImageLoadStatus() != MediaTracker.COMPLETE)
		{
			return CardBoxUI.getDefaultDisplayPic();
		}
		return _displayPic;
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
	
}
