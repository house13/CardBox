package com.hextilla.cardbox.facebook;

import com.restfb.Facebook;
import com.restfb.types.User;

public class UserWithPicture extends User 
{
	public String getPicture ()
	{
		return picture;
	}
	
	public String toString ()
	{
		return this.getName();
	}
	
	@Facebook
	private String picture;
}
