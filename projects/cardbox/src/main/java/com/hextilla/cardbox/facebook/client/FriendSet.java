package com.hextilla.cardbox.facebook.client;

import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.ImageIcon;

import com.hextilla.cardbox.facebook.UserWithPicture;

public class FriendSet 
{
	class Downloader implements Runnable
	{
	  public Downloader(){}
	  public void run() {
		  ImageIcon icon;
		  String picture;
		  while(true) {
			  while(!_downloadList.isEmpty()) {
				   Long fbId = _downloadList.pop();
				   picture = getFriend(fbId).getPicture();
				   icon = new ImageIcon(picture);
			       _pictures.put(fbId, icon);
			  }  
		  }
	  }
	}
	public FriendSet ()
	{
		_friends = new Hashtable<Long, UserWithPicture>();
		_pictures = new Hashtable<Long, ImageIcon>();
		//Thread t = new Thread(new Downloader());
		//t.run();
	}
	
	public void add (UserWithPicture friend)
	{
		if (friend != null) 
		{
			_friends.put(Long.valueOf(friend.getId()), friend);
		}
	}
	
	public UserWithPicture getFriend (Long fbId)
	{
		return _friends.get(fbId);
	}
	
	public boolean isFriend (long fbId)
	{
		return _friends.containsKey(new Long(fbId));
	}
	
	public ImageIcon getImage(long fbId){
		if (_pictures.containsKey(new Long(fbId)))
		{
		  return _pictures.get(new Long(fbId));
		}
		return null;
	}
	
	protected Hashtable<Long, UserWithPicture> _friends;
	protected LinkedList<Long> _downloadList;
	protected Hashtable<Long, ImageIcon> _pictures;
}
