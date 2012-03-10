package com.hextilla.cardbox.facebook.client;

import com.hextilla.cardbox.data.CardBoxUserObject;
import com.hextilla.cardbox.facebook.UserWithPicture;
import com.hextilla.cardbox.util.CardBoxContext;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Connection;
import com.restfb.Parameter;

import java.util.List;

import com.samskivert.util.StringUtil;

import com.threerings.presents.client.BasicDirector;
import com.threerings.presents.client.Client;

public class SocialDirector extends BasicDirector 
{
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
		init(client);
	}
	
	public FriendSet getFriends ()
	{
		if (_fbclient == null) return null;
		
		FriendSet friends = new FriendSet();
		Connection<UserWithPicture> friendList = _fbclient.fetchConnection("me/friends", 
				UserWithPicture.class, Parameter.with("fields", "id, name, picture"));
		
		for (List<UserWithPicture> friendPage : friendList) {
			for (UserWithPicture friend : friendPage) {
				friends.add(friend);
;			}
		}
		
		return friends;
	}
	

	protected String _token;
	
	protected CardBoxContext _ctx;
	protected FacebookClient _fbclient;
}
