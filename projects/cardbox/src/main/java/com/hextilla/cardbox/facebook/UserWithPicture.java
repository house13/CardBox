package com.hextilla.cardbox.facebook;

import javax.swing.ImageIcon;

import com.restfb.Facebook;
import com.restfb.types.User;

public class UserWithPicture extends User 
{
	public ImageIcon getPicture ()
	{
		if( picture == null) {
			String fbid = getId();
			picture =  new ImageIcon("https://graph.facebook.com/"+fbid+"/picture");
		}
		return picture;
	}
	
	@Facebook
	private ImageIcon picture = null;
}
