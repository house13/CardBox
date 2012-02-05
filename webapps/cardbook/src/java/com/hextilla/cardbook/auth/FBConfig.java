package com.hextilla.cardbook.auth;

public class FBConfig {
    // get these from your FB Dev App
	private static final String client_id = "154269514645710";
    private static final String app_secret = "bda6ac724410ed82c3b0a09586ece71f";

    // set this to your servlet URL for the authentication servlet/filter
    private static final String redirect_uri = "http://hextilla.com:8080/cardbook/fbauth";
    /// set this to the comma-separated list of extended permissions you want
    private static final String perms = "email,publish_stream";

    public static String getAppId() {
        return client_id;
    }

    public static String getAppSecret() {
        return app_secret;
    }

    public static String getLoginRedirectURL() {
        return "https://www.facebook.com/dialog/oauth?client_id=" + client_id +
        	"&redirect_uri=" + redirect_uri + "&scope=" + perms;
    }

    public static String getAuthURL(String authToken) {
        return "https://graph.facebook.com/oauth/access_token?client_id=" + client_id +
        	"&redirect_uri=" + redirect_uri + 
        	"&client_secret="+ app_secret + "&code=" + authToken;
    }
}
