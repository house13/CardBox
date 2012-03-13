package com.hextilla.cardbox.facebook.client;

import java.util.Hashtable;

import javax.swing.ImageIcon;

import com.hextilla.cardbox.client.CardBoxUI;
import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.facebook.UserWithPicture;
import com.hextilla.cardbox.util.CardBoxContext;

import static com.hextilla.cardbox.Log.log;

public class FriendSet 
{
	public FriendSet (CardBoxContext ctx)
	{
		_ctx = ctx;
		_friends = new Hashtable<Long, UserWithPicture>();
		_pictures = new Hashtable<Long, ImageIcon>();
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
	
	public boolean isFriend (Long fbId)
	{
		return _friends.containsKey(fbId);
	}
	
	/**
	 * Checks our picture cache for the given Facebook ID. If we've haven't cached
	 *   a pic for that user, tell the Social Director to get on that.
	 * @param fbId Unique Facebook ID for desired friend
	 * @return Whether that picture was cached at time of call
	 */
	public boolean checkPic(CardBoxName friend)
	{
		Long friendId = new Long(friend.getFacebookId());
		boolean isCached = _pictures.containsKey(friendId);
		if (!isCached && isFriend(friendId))
		{
			new Thread(new PicDownloader(_ctx, friend, getFriend(friendId).getPicture())).start();
		}
		return isCached;
	}
	
	public ImageIcon getPic(CardBoxName friend)
	{
	    Long friendId = new Long(friend.getFacebookId());
	    boolean isCached = checkPic(friend);
	    if (!isCached)
	    {
	    	log.info("Display pic not cached, returning default", "friend", friend.getFriendlyName().toString());
	        return CardBoxUI.getDefaultDisplayPic();
	    }
	    log.info("Display pic was cached, retrieving it from the table", "friend", friend.getFriendlyName().toString());
	    return _pictures.get(friendId);
	}
	
	public void setPicFromRaw(Long fbId, String bytes)
	{
		if (bytes == null) {
			log.info("Argument to FriendSet.setPicFromRaw was null, taking no action");
			return;
		}
		ImageIcon pic = CardBoxUI.renderDisplayPic(bytes);
		log.info("Display picture has been downloaded and properly rendered", "userid", fbId, "size", bytes.length() + " B");
		_pictures.put(fbId, pic);
	}
	
	protected class PicDownloader implements Runnable
	{
		public PicDownloader(CardBoxContext ctx, CardBoxName friend, String url)
		{
			_ctx = ctx;
			_friend = friend;
			_url = url;
		}
		
		@Override
		public void run() 
		{
			try {
				_ctx.getSocialDirector().downloadPic(_friend, _url);
			} catch (Exception e) {
				log.warning("Could not download display picture as requested for user [" + _friend.getFriendlyName().toString() + "]", e);
			}
		}
		
		CardBoxContext _ctx;
		CardBoxName _friend;
		String _url;
	}
	
	/** The giver of life and services */
	protected CardBoxContext _ctx;
	
	protected Hashtable<Long, UserWithPicture> _friends;
	protected Hashtable<Long, ImageIcon> _pictures;
}
