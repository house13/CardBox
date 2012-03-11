package com.hextilla.cardbox.facebook.client;

import com.hextilla.cardbox.data.CardBoxUserObject;
import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.facebook.UserWithPicture;
import com.hextilla.cardbox.util.CardBoxContext;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Connection;
import com.restfb.Parameter;

import java.util.Iterator;
import java.util.List;

import com.samskivert.util.StringUtil;

import com.threerings.presents.client.BasicDirector;
import com.threerings.presents.client.Client;

public class SocialDirector extends BasicDirector 
{
	public interface FriendIterator
	{
		/** Need a means for iterating over our list of online friends */
		public CardBoxName next();
		public boolean hasNext();
	}
	
	public interface FriendTracker
	{
		/** Need a means for components to determining if user is one of our online friends */
		public boolean isOnlineFriend(CardBoxName friend);
		
		/** Need to provide a means for iterating over our list of online friends */
		public FriendIterator getOnlineFriendIterator();
		
		/** Need to provide a means to update the view when a user's display pic changes. */
		public void imageUpdated(CardBoxName friend);
	}

	public SocialDirector (CardBoxContext ctx) {
		super(ctx);
		_ctx = ctx;
	}
	
	public void init (Client client)
	{
		CardBoxUserObject user = (CardBoxUserObject)client.getClientObject();
		_token = user.getSession();
		_fbclient = StringUtil.isBlank(_token) ? null : new DefaultFacebookClient(_token);
	}
	
	@Override
	public void clientDidLogon (Client client)
	{
		super.clientDidLogon(client);
		if (_ctx.isFacebookEnabled())
			init(client);
	}
	
	public synchronized FriendSet getFriends ()
	{
		if (_fbclient == null) return new FriendSet();
		if (_friends == null)
		{
			FriendSet friends = new FriendSet();
			Connection<UserWithPicture> friendList = _fbclient.fetchConnection("me/friends", 
					UserWithPicture.class, Parameter.with("fields", "id, name, picture"));
			
			for (List<UserWithPicture> friendPage : friendList) {
				for (UserWithPicture friend : friendPage) {
					friends.add(friend);
				}
			}
			_friends = friends;
		}
		return _friends;
	}
	
	/** Allows us to provide online-tracking throughout the client */
	public void setFriendTracker(FriendTracker ft)
	{
		_tracker = ft;
	}
	
	/** Delegate responsibility to the FriendTracker, if available */
	public boolean isOnlineFriend(CardBoxName friend)
	{
		return (_tracker == null) ? false : _tracker.isOnlineFriend(friend);
	}
	
	/** Delegate responsibility to the FriendTracker, if available */
	public FriendIterator getOnlineFriendIterator()
	{
		return (_tracker == null) ? null : _tracker.getOnlineFriendIterator();
	}
	
	/** Delegate responsibility to the FriendTracker, if available */
	public void imageUpdated(CardBoxName friend)
	{
		if (_tracker != null)
			_tracker.imageUpdated(friend);
	}
	

	protected String _token;
	
	protected FriendSet _friends = null;
	
	protected CardBoxContext _ctx;
	protected FacebookClient _fbclient = null;
	
	// Delegate the responsibility of tracking online friends using the FriendTracker interface
	protected FriendTracker _tracker = null;
	protected FriendIterator _iterator = null;
}
