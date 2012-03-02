package com.hextilla.cardbox.lobby.friendlist;

import javax.swing.ImageIcon;
import com.hextilla.cardbox.client.CardBoxUI;
import com.hextilla.cardbox.facebook.UserWithPicture;

// A friend object
public class FriendEntry {
	private String _name;
	private ImageIcon _displayPic = null;
	
	public FriendEntry(String name)
	{
		// For now just make it contain some text
		_name = name;
	}

	public FriendEntry(UserWithPicture user)
	{
		// For now just make it contain some text
		_name = user.getName();
		_displayPic = user.getPicture();
	}
	
	public String Name()
	{
		return _name;
	}
	
	public ImageIcon DisplayPic()
	{
		if (_displayPic == null)
		{
			return CardBoxUI.getDefaultDisplayPic();
		}
		return _displayPic;
	}
	
}
