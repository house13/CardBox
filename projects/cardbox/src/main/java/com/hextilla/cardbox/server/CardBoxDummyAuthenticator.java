package com.hextilla.cardbox.server;

import java.util.Random;

import com.hextilla.cardbox.facebook.CardBoxCredentials;
import com.hextilla.cardbox.facebook.CardBoxName;
import com.threerings.util.Name;

import com.threerings.presents.net.AuthResponse;
import com.threerings.presents.net.AuthResponseData;
import com.threerings.presents.net.Credentials;
import com.threerings.presents.server.Authenticator;
import com.threerings.presents.server.net.AuthingConnection;

import static com.threerings.presents.Log.log;

/**
* A simple authenticator implementation that simply accepts all authentication requests.
*/
public class CardBoxDummyAuthenticator extends Authenticator
{
	
 @Override
 protected void processAuthentication (AuthingConnection conn, AuthResponse rsp)
     throws Exception
 {
     log.info("Accepting request: " + conn.getAuthRequest());

     // In dev mode, the session ID in our credentials is actually the raw API token from facebook!
     Credentials creds = conn.getAuthRequest().getCredentials();
     if (creds instanceof CardBoxCredentials) {
    	 CardBoxCredentials cbcreds = (CardBoxCredentials)creds;
         conn.setAuthName(new CardBoxName(genRandomId(), "Errol", "Hornsby", "Airhornsman", true));
         rsp.authdata = cbcreds.getSession();
     } else {
         conn.setAuthName(new Name(conn.getInetAddress().getHostAddress()));
     }   

     rsp.getData().code = AuthResponseData.SUCCESS;
 }
 
 protected static long genRandomId ()
 {
	 long start = 1000;
	 long end = 10000000000007L;
	 long range = end - start + 1;
	 long frac = (long)(range * _rand.nextDouble());
	 return frac + start;
 }
 
 protected static Random _rand = new Random();
}
