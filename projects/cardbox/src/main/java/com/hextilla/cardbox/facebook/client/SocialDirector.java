package com.hextilla.cardbox.facebook.client;

import com.hextilla.cardbox.data.CardBoxUserObject;
import com.hextilla.cardbox.util.CardBoxContext;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.samskivert.util.StringUtil;

import com.threerings.presents.client.BasicDirector;

public class SocialDirector extends BasicDirector 
{
	public SocialDirector (CardBoxContext ctx) {
		super(ctx);
		_ctx = ctx;
	}
	
	public void init ()
	{
		CardBoxUserObject user = (CardBoxUserObject)_ctx.getClient().getClientObject();
		_token = user.getSession();
		_fbclient = StringUtil.isBlank(_token) ? null : new DefaultFacebookClient(_token);
		
	}
	

	protected String _token;
	
	protected CardBoxContext _ctx;
	protected FacebookClient _fbclient;
}
