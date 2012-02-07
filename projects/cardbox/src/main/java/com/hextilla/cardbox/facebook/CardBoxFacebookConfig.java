package com.hextilla.cardbox.facebook;

import com.hextilla.cardbox.server.CardBoxConfig;

public class CardBoxFacebookConfig 
{
	protected static String getClientId()
	{
		return CardBoxConfig.config.getValue("web.fb.client_id", "");
	}
	 
	protected static String getAppSecret()
	{
		return CardBoxConfig.config.getValue("web.fb.app_secret", "");
	}
	 
	protected static String getRedirectUri()
	{
		return CardBoxConfig.config.getValue("web.fb.redirect_uri", "");
	}
	 
	protected static String getPermissions()
	{
		return CardBoxConfig.config.getValue("web.fb.perms", "");
	}
	 
	public static String getLoginRedirectURL() {
	    return "https://www.facebook.com/dialog/oauth?client_id=" + getClientId() +
	    	   "&redirect_uri=" + getRedirectUri() + "&scope=" + getPermissions();
	}
	
	public static String getAuthURL(String authCode) {
	    return "https://graph.facebook.com/oauth/access_token?client_id=" +  getClientId() +
			   "&redirect_uri=" + getRedirectUri() +  "&client_secret="+ getAppSecret() +
			   "&code=" + authCode;
	}
	
	public static String getUserDataURL(String accessToken) {
		return "https://graph.facebook.com/me?access_token=" + accessToken;
	}
}
