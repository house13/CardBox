package com.hextilla.cardbox.lobby.friendlist;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import com.hextilla.cardbox.client.CardBoxUI;
import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.util.CardBoxContext;
import com.samskivert.swing.MultiLineLabel;

// A friend object
public class FriendEntry
	implements Comparable<FriendEntry>
{
	public FriendEntry(CardBoxContext ctx, CardBoxName name)
	{
		this(ctx, name, null);
	}
	
	public FriendEntry(CardBoxContext ctx, CardBoxName name, ImageIcon pic)
	{
		_ctx = ctx;
		_name = name;
		_pic = pic;
		_status = new OnlineStatus(_ctx, name);
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
	
	public OnlineStatus getStatus()
	{
		return _status;
	}
	
	public MultiLineLabel printStatus()
	{
		MultiLineLabel label = new MultiLineLabel(_status.toString(), MultiLineLabel.LEFT, SwingConstants.HORIZONTAL, 0);
		label.setFont(CardBoxUI.AppFontItalicExtraSmall);
		return label;
	}
	
	public boolean update(FriendEntry other)
	{
		boolean changed = false;
		if (this.equals(other)) {
			_pic = other.getDisplayPic();
			_status = other.getStatus();
			changed = true;
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
	
	public static String formatUsername (String username)
	{
		return "(" + username + ")";
	}
	
	protected CardBoxContext _ctx;
	
	protected CardBoxName _name = null;
	protected ImageIcon _pic = null;
	protected OnlineStatus _status = null;
	// Also encapsulate game/table data if any
}
