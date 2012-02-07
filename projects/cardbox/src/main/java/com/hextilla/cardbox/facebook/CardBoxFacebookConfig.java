package com.hextilla.cardbox.facebook;

import com.hextilla.cardbox.server.CardBoxConfig;

public class CardBoxFacebookConfig 
{
	public static String getClientId()
	{
		return _clientId;
	}
	 
	public static String getAppSecret()
	{
		return _appSecret;
	}
	 
	public static String getRedirectUri()
	{
		return _redirectUri;
	}
	 
	public static String getPermissions()
	{
		return _perms;
	}
	 
	public static String getLoginRedirectURL() {
	    return "https://www.facebook.com/dialog/oauth?client_id=" + _clientId +
	    	   "&redirect_uri=" + _redirectUri + "&scope=" + _perms;
	}
	
	public static String getAuthURL(String authCode) {
	    return "https://graph.facebook.com/oauth/access_token?client_id=" +  _clientId +
			   "&redirect_uri=" + _redirectUri +  "&client_secret="+ _appSecret +
			   "&code=" + authCode;
	}
	
	public static String getUserDataURL(String accessToken) {
		return "https://graph.facebook.com/me?access_token=" + accessToken;
	}
	
	/** Load and cache our Facebook configuration data statically from the CardBoxConfig */
	protected static final String _clientId = CardBoxConfig.config.getValue("web.fb.client_id", "");
	protected static final String _appSecret = CardBoxConfig.config.getValue("web.fb.app_secret", "");
	protected static final String _redirectUri = CardBoxConfig.config.getValue("web.fb.redirect_uri", "");
	protected static final String _perms = CardBoxConfig.config.getValue("web.fb.perms", "");
}
