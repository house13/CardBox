package com.hextilla.cardbox.server;

import static com.hextilla.cardbox.Log.log;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.hextilla.cardbox.server.persist.FBUserRecord;
import com.hextilla.cardbox.server.persist.SessionRecord;

import com.samskivert.io.PersistenceException;
import com.samskivert.util.IntIntMap;
import com.samskivert.util.Interval;
import com.samskivert.util.Invoker;

import com.threerings.presents.annotation.MainInvoker;
import com.threerings.presents.server.InvocationManager;
import com.threerings.presents.server.PresentsDObjectMgr;

@Singleton
public class CardBoxUserManager 
{
    public interface FacebookUserRepository
    {
    	/** Loads persistent user data from userId */
    	public FBUserRecord loadUser (int userId)
    		     throws PersistenceException;
    	
    	/** Loads persistent user data from Facebook ID */
    	public FBUserRecord loadUser (long fbId)
    		     throws PersistenceException;
    	
    	/** Loads persistent user data from Session Hash */
    	public FBUserRecord loadUser (String sessionHash)
    		     throws PersistenceException;
    	
    	/** Loads Facebook authentication data for a given user */
    	public SessionRecord loadSession (int userId)
    		     throws PersistenceException;
    	
    	public int purgeSessions ()
    			 throws PersistenceException;
    }
    
    @Inject public CardBoxUserManager (InvocationManager invmgr)
    {
    }
    
	/**
     * Prepares the cardbox user manager for operation.
     */
    public void init (FacebookUserRepository userrepo)
        throws PersistenceException
    {
        // make a note of our server services
    	_userrepo = userrepo;

        if (_userrepo != null) {
            // periodically delete expired sessions from the database
        	_purgesesh = new Interval(_omgr) {
                @Override
                public void expired () {
                    purgeSessions();
                }
            };
            _purgesesh.schedule(60 * 1000L, true);
        }

        log.info("CardBoxManager ready [rsrcdir=" + CardBoxConfig.getResourceDir() + "].");
    }
    
    /**
     * Publishes our lobby and game occupancy figures to the database.
     */
    protected void purgeSessions ()
    {
        // then update the database
        _invoker.postUnit(new Invoker.Unit() {
            @Override
            public boolean invoke () {
                try {
                	int deleted = _userrepo.purgeSessions();
                	log.info("Call to purgeSessions() removed " + deleted + " total expired session records for approx. " + (deleted/2) + " users.");
                } catch (Exception e) {
                    log.warning("Failed to purge expired sessions", e);
                }
                return false;
            }
        });
    }
	
	protected FacebookUserRepository _userrepo;

    /** Handles distributed object business. */
    @Inject protected PresentsDObjectMgr _omgr;

    /** Handles database business. */
    @Inject protected @MainInvoker Invoker _invoker;
    
    /** Periodically deletes expired sessions from the database. */
    protected Interval _purgesesh;
}
