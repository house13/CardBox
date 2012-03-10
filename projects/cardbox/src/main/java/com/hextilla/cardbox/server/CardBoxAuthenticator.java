package com.hextilla.cardbox.server;


import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.samskivert.io.PersistenceException;
import com.samskivert.jdbc.ConnectionProvider;

import com.threerings.util.Name;

import com.threerings.presents.net.AuthRequest;
import com.threerings.presents.net.AuthResponse;
import com.threerings.presents.net.AuthResponseData;
import com.threerings.presents.net.UsernamePasswordCreds;

import com.threerings.presents.server.Authenticator;
import com.threerings.presents.server.net.AuthingConnection;

import com.threerings.crowd.data.TokenRing;

import com.hextilla.cardbox.facebook.CardBoxCredentials;
import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.server.CardBoxConfig;
import com.hextilla.cardbox.server.persist.FBUserRecord;
import com.hextilla.cardbox.server.persist.SessionRecord;

import static com.threerings.presents.data.AuthCodes.*;
import static com.hextilla.cardbox.Log.log;

/**
 * Delegates authentication to the CardBox user manager.
 */
@Singleton
public class CardBoxAuthenticator extends Authenticator
{
    @Inject public CardBoxAuthenticator (ConnectionProvider conprov)
        throws PersistenceException
    {
    }

    // from abstract Authenticator
    protected void processAuthentication (AuthingConnection conn, AuthResponse rsp)
        throws Exception
    {
        // make sure we were properly initialized
        if (_usermgr == null) {
            throw new AuthException(SERVER_ERROR);
        }
            
        // make sure they've sent valid credentials
        AuthRequest req = conn.getAuthRequest();
        if (!(req.getCredentials() instanceof CardBoxCredentials)) {
            log.warning("Invalid credentials: " + req);
            throw new AuthException(SERVER_ERROR);
        }
        CardBoxCredentials creds = (CardBoxCredentials)req.getCredentials();
        String session_id = creds.getSession();

        // load up their user account record
        FBUserRecord user = _usermgr.loadUser(session_id);
        if (user == null) {
            throw new AuthException(NO_SUCH_USER);
        }

        // configure their auth name using their facebook record data
        conn.setAuthName(new CardBoxName(user.fbId, user.firstname, user.lastname, user.username, user.anonymous));

        SessionRecord session = _usermgr.loadSession(user.userId);
        if (session == null) {
        	throw new AuthException(INVALID_PASSWORD);
        }
        
        rsp.authdata = session.authtoken;

        log.info("User logged on", "user", user.fbId);
        rsp.getData().code = AuthResponseData.SUCCESS;
    }

    @Inject protected CardBoxUserManager _usermgr;
}
