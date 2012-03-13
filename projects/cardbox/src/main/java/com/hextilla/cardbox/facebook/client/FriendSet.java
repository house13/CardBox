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
			try {
				_ctx.getSocialDirector().downloadPic(friend, getFriend(friendId).getPicture());
			} catch (Exception e) {
				log.warning("Could not download display picture as requested for user [" + getFriend(friendId).toString() + "]", e);
			}
		}
		return isCached;
	}
	
	public ImageIcon getPic(CardBoxName friend)
	{
	    Long friendId = new Long(friend.getFacebookId());
	    boolean isCached = checkPic(friend);
	    if (!isCached)
	    {
	            return CardBoxUI.getDefaultDisplayPic();
	    }
	    return _pictures.get(friendId);
	}
	
	public void setPicFromRaw(Long fbId, String bytes)
	{
		ImageIcon pic = CardBoxUI.renderDisplayPic(bytes);
		_pictures.put(fbId, pic);
	}
	
	/** The giver of life and services */
	protected CardBoxContext _ctx;
	
	protected Hashtable<Long, UserWithPicture> _friends;
	protected Hashtable<Long, ImageIcon> _pictures;
}
