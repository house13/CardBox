//
//CardBox library - framework for matchmaking networked games
//Copyright (C) 2005-2011 Three Rings Design, Inc., All Rights Reserved
//http://github.com/threerings/game-gardens
//
//This library is free software; you can redistribute it and/or modify it
//under the terms of the GNU Lesser General Public License as published
//by the Free Software Foundation; either version 2.1 of the License, or
//(at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this library; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package com.hextilla.cardbook.logic;

import java.util.Calendar;
import java.util.logging.Level;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.restfb.FacebookClient;
import com.restfb.DefaultFacebookClient;
import com.restfb.types.User;

import com.samskivert.servlet.user.InvalidUsernameException;
import com.samskivert.servlet.user.Username;
import com.samskivert.servlet.util.FriendlyException;
import com.samskivert.servlet.util.ParameterUtil;

import com.samskivert.util.StringUtil;
import com.samskivert.util.Tuple;
import com.samskivert.velocity.Application;
import com.samskivert.velocity.InvocationContext;
import com.samskivert.velocity.Logic;

import com.hextilla.cardbox.data.CardBoxCodes;
import com.hextilla.cardbox.server.CardBoxConfig;
import com.hextilla.cardbox.server.persist.GameRecord;

import com.hextilla.cardbook.CardbookApp;
import com.hextilla.cardbook.auth.FBUserManager;

import com.hextilla.cardbox.server.persist.FBUserRecord;
import com.hextilla.cardbox.server.persist.FBUserRepository;

import com.hextilla.cardbox.facebook.CardBoxFacebookConfig;

import static com.hextilla.cardbook.Log.log;

/**
*  OAuth 2.0 authentication through Facebook, and
*  handling user authentication accordingly
*/
public class fbauth implements Logic
{
	 // documentation inherited
	 public void invoke (Application app, InvocationContext ctx)
	     throws Exception
	 {
	     HttpServletRequest req = ctx.getRequest();
	     HttpServletResponse rsp = ctx.getResponse();
	     
	     String fbcode = req.getParameter("code");
		 if (StringUtil.isBlank(fbcode))
		 {
			 throw new FriendlyException("error.fbauth_failure");
		 }
		 
		 URL authUrl = new URL(CardBoxFacebookConfig.getAuthURL(fbcode));
		 Tuple<String,Integer> accessCreds = getAuthenticated(authUrl);
		 if (accessCreds == null) {
			 throw new FriendlyException("error.fbauth_failure");
		 }
		 
		 String accessToken = accessCreds.left;
		 int expires = accessCreds.right;
		 
		 FacebookClient fbClient = new DefaultFacebookClient(accessToken);
		 User fbUser = fbClient.fetchObject("me", User.class);
		 
		 boolean playerExists = false;
		 boolean authSuccess = false;
		 FBUserRecord authUser = null;
		 FBUserManager userman = ((CardbookApp)app).getUserManager();
		 
		 // Attempt to log the user in, assuming they've played the game before
		 // If they're new, we need to create an account for them.
		 try {
			 authUser = userman.login(fbUser.getId(), accessToken, expires, req, rsp);
			 playerExists = true;
			 authSuccess = true;
		 } catch (Exception e) {
			 log.info("New user: [user=" + fbUser.getId() + ",token=" + accessToken + "] Successful FB Authentication");
		 }
		 if (!playerExists) {
			 Calendar now = Calendar.getInstance();
			 FBUserRepository repo = userman.getRepository();
			 try {
				 authUser = new FBUserRecord();
				 authUser.init(fbUser, now.getTimeInMillis());
				 repo.insertUser(authUser);
				 authUser = userman.login(fbUser.getId(), accessToken, expires, req, rsp);
				 authSuccess = true;
			 } catch (Exception e) {
				 log.warning("Creating new user: [user=" + fbUser.getId() + ",token=" + accessToken + "] failed due to exception", e);
				 throw new FriendlyException("error.database_error");
			 }
		 }
		 
		 ctx.put("fbauthed", authSuccess);
		 if (authUser != null) {
			 ctx.put("user", authUser);
		 }
		 
		 // Handle redirection flow somewhat nicely
	     String pgState = req.getParameter("state");
	     // By default, take 'em to the play page.
	     String target = "/cardbook/play.wm#play";
	     
    	 if (StringUtil.isBlank(pgState) || pgState.equals(_play)) {
    		 target = "/cardbook/play.wm#play";
    	 } else if (pgState.equals(_sett)) {
    		 target = "/cardbook/settings.wm";
    	 }
	     
    	 // Only redirect if the authentication was successful.
    	 if (authSuccess)
    		 rsp.sendRedirect(target);
	 }
	 
	 /** Performs the raw act of Facebook authentication, getting the token/expiry
	  * from the response to the provided well-formed authentication URL.
	  * @param authUrl The well-formed FB authentication URL with all the details
	  * @return A String,Integer tuple representing the access token and its expiry (s)
	  */
	 private Tuple<String,Integer> getAuthenticated(URL authUrl)
	 {
		 String accToken = null;
		 Integer expires = null;
		 
		 try {
			 // The access token for a successful authentication is returned in the content
			 // of an HTTP response. We can buffer this directly into a string to devour its contents.  
			 String OAuthResponse = readUrl(authUrl);
			 
			 // Split up and parse the returned name-value pairs
			 String[] pairs = OAuthResponse.split("&");
             for (String pair : pairs) {
                 String[] keyval = pair.split("=");
                 if (keyval[0].equals("access_token")) {
                     accToken = keyval[1];
                 } else if (keyval[0].equals("expires")) {
                     expires = Integer.valueOf(keyval[1]);
                 }
             }
		 } catch (Exception e) {
		 }
		 
		 if (accToken == null) {
			 return null;
		 } else {
			 return new Tuple<String,Integer>(accToken, expires);
		 }
	 }
	 
	 private String readUrl(URL url) 
			 throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = url.openStream();
        int r;
        while ((r = is.read()) != -1) {
            baos.write(r);
        }
        return new String(baos.toByteArray());
    }
	
	public static String getDefaultUsername() {
			return CardBoxConfig.config.getValue("default_username", "Anonymous");
	}
	
	protected static final String _play = "play";
	protected static final String _sett = "settings";
	
	protected static final String CLIENT_PATH = "/client";
}

