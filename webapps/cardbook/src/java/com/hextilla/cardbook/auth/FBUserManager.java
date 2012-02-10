package com.hextilla.cardbook.auth;

import java.util.Calendar;
import java.util.Properties;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.samskivert.depot.PersistenceContext;
import com.samskivert.io.PersistenceException;
import com.samskivert.jdbc.ConnectionProvider;
import com.samskivert.servlet.RedirectException;
import com.samskivert.servlet.user.AuthenticationFailedException;
import com.samskivert.servlet.user.Authenticator;
import com.samskivert.servlet.user.InvalidPasswordException;
import com.samskivert.servlet.user.NoSuchUserException;
import com.samskivert.servlet.util.CookieUtil;
import com.samskivert.servlet.util.RequestUtils;
import com.samskivert.util.Interval;
import com.samskivert.util.RunQueue;
import com.samskivert.util.StringUtil;
import com.samskivert.util.Tuple;

import com.hextilla.cardbox.server.persist.FBUserRecord;
import com.hextilla.cardbox.server.persist.FBUserRepository;

import com.hextilla.cardbox.facebook.CardBoxFacebookConfig;

import static com.hextilla.cardbook.Log.log;

/**
 * The user manager provides easy access to user objects for servlets. It takes care of cookie
 * management involved in login, logout and loading a user record during an authenticated session.
 */
public class FBUserManager
{
    /**
     * Prepares this user manager for operation. See {@link #init(Properties,ConnectionProvider)}.
     *
     * @param pruneQueue an optional run queue on which to run our periodic session pruning task.
     */
    public FBUserManager (PersistenceContext pctx)
        throws PersistenceException
    {
        // create the user repository
        _repository = createRepository(pctx);

        if (USERMGR_DEBUG) {
            log.info("FBUserManager initialized", "acook", _userAuthCookie, "login", _loginURL);
        }
    }

    public void shutdown ()
    {
    }

    /**
     * Returns a reference to the repository in use by this user manager.
     */
    public FBUserRepository getRepository ()
    {
        return _repository;
    }

    /**
     * Fetches the necessary authentication information from the http request and loads the user
     * identified by that information.
     *
     * @return the user associated with the request or null if no user was associated with the
     * request or if the authentication information is bogus.
     */
    public FBUserRecord loadUser (HttpServletRequest req)
        throws PersistenceException
    {
        String authcook = CookieUtil.getCookieValue(req, _userAuthCookie);
        if (USERMGR_DEBUG) {
            log.info("Loading user by cookie", _userAuthCookie, authcook);
        }
        return loadUser(authcook);
    }

    /**
     * Loads up a user based on the supplied session hash.
     */
    public FBUserRecord loadUser (String sessionId)
        throws PersistenceException
    {
        FBUserRecord user = (sessionId == null) ? null : _repository.loadUser(sessionId);
        if (USERMGR_DEBUG) {
            log.info("Loaded user by authcode", "code", sessionId, "user", user);
        }
        return user;
    }
    
    /**
     * Loads up a user based on the supplied user ID.
     */
    public FBUserRecord loadUser (int userId)
        throws PersistenceException
    {
        FBUserRecord user = (userId < 0) ? null : _repository.loadUser(userId);
        if (USERMGR_DEBUG) {
            log.info("Loaded user by userId", "userId", userId, "user", user);
        }
        return user;
    }

    /**
     * Fetches the necessary authentication information from the http request and loads the user
     * identified by that information. If no user could be loaded (because the requester is not
     * authenticated), a redirect exception will be thrown to redirect the user to the login page
     * specified in the user manager configuration.
     *
     * @return the user associated with the request.
     */
    public FBUserRecord requireUser (HttpServletRequest req)
        throws PersistenceException, RedirectException
    {
        FBUserRecord user = loadUser(req);
        // if no user was loaded, we need to redirect these fine people to the login page
        if (user == null) {
            // first construct the redirect URL
            String target = CardBoxFacebookConfig.getLoginRedirectURL();
            if (USERMGR_DEBUG) {
                log.info("No user found in require, redirecting", "to", target);
            }
            throw new RedirectException(target);
        }
        return user;
    }
    
    public String getSession (HttpServletRequest req)
    {
    	return CookieUtil.getCookieValue(req, _userAuthCookie);
    }

    /**
     * Attempts to authenticate the requester and initiate an authenticated session for them. An
     * authenticated session involves their receiving a cookie that proves them to be authenticated
     * and an entry in the session database being created that maps their information to their
     * userid. If this call completes, the session was established and the proper cookies were set
     * in the supplied response object. If invalid authentication information is provided or some
     * other error occurs, an exception will be thrown.
     *
     * @param fbId The user's unique Facebook ID.
     * @param token The OAuth token returned from Facebook API.
     * @param expires Number of seconds until the access token expires.
     * @param req The request via which the login page was loaded.
     * @param rsp The response in which the cookie is to be set.
     * @param auth The authenticator used to check whether the user should be authenticated.
     *
     * @return the user object of the authenticated user.
     */
    public FBUserRecord login (String fbId, String token, int expires, HttpServletRequest req, HttpServletResponse rsp)
        throws PersistenceException, AuthenticationFailedException
    {
        // load up the requested user
        FBUserRecord user = _repository.loadUser(Long.valueOf(fbId));
        if (user == null) {
        	log.warning("Couldn't load user by facebook ID", "fbId", fbId);
            throw new NoSuchUserException("error.no_such_user");
        }

        // give them the necessary cookies and business
        effectLogin(user, token, expires, req, rsp);

        return user;
    }

    /**
     * If a user is already known to be authenticated for one reason or other, this method can be
     * used to give them the appropriate authentication cookies to effect their login.
     *
     * @param expires Date in milliseconds when the access token expires.
     */
    public void effectLogin (
        FBUserRecord user, String token, int expires, HttpServletRequest req, HttpServletResponse rsp)
        throws PersistenceException
    {
    	Calendar now = Calendar.getInstance();
    	now.add(Calendar.SECOND, expires);
		long expiration = now.getTimeInMillis();
        String sessionId = _repository.registerSession(user, token, expiration);
        Cookie acookie = new Cookie(_userAuthCookie, sessionId);
        acookie.setPath("/");
        acookie.setMaxAge((expires > 0) ? expires : 60*60);
        if (USERMGR_DEBUG) {
            log.info("Setting cookie " + acookie + ".");
        }
        rsp.addCookie(acookie);
    }

    /**
     * Logs the user out.
     */
    public void logout (HttpServletRequest req, HttpServletResponse rsp)
    {
        // nothing to do if they don't already have an auth cookie
        String authcode = CookieUtil.getCookieValue(req, _userAuthCookie);
        if (authcode == null) {
            return;
        }

        // set them up the bomb
        Cookie c = new Cookie(_userAuthCookie, "x");
        c.setPath("/");
        c.setMaxAge(0);
        CookieUtil.widenDomain(req, c);
        if (USERMGR_DEBUG) {
            log.info("Clearing cookie " + c + ".");
        }
        rsp.addCookie(c);

        // we need an unwidened one to ensure that old-style cookies are wiped as well
        c = new Cookie(_userAuthCookie, "x");
        c.setPath("/");
        c.setMaxAge(0);
        rsp.addCookie(c);
    }

    /**
     * Called by the user manager to create the user repository. Derived classes can override this
     * and create a specialized repository if they so desire.
     */
    protected FBUserRepository createRepository (PersistenceContext pctx)
        throws PersistenceException
    {
        return new FBUserRepository(pctx);
    }

    /** The user repository. */
    protected FBUserRepository _repository;

    /** The URL for the user login page. */
    protected static String _loginURL;

    /** The name of our user authentication cookie. */
    protected String _userAuthCookie = USERAUTH_COOKIE;

    /** The user authentication cookie name. */
    protected static final String USERAUTH_COOKIE = "id_";

    /** Prune the session table every hour. */
    protected static final long SESSION_PRUNE_INTERVAL = 60L * 60L * 1000L;

    /** Indicates how long (in days) that a "persisting" session token should last. */
    protected static final int PERSIST_EXPIRE_DAYS = 30;

    /** Indicates how long (in days) that a "non-persisting" session token should last. */
    protected static final int NON_PERSIST_EXPIRE_DAYS = 1;

    /** Change this to true and recompile to debug cookie handling. */
    protected static final boolean USERMGR_DEBUG = false;
}
