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

package com.hextilla.cardbox.server.persist;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.samskivert.depot.DepotRepository;
import com.samskivert.depot.Exps;
import com.samskivert.depot.PersistenceContext;
import com.samskivert.depot.PersistentRecord;
import com.samskivert.depot.expression.FluentExp;
import com.samskivert.depot.clause.Limit;
import com.samskivert.depot.clause.OrderBy;
import com.samskivert.depot.clause.Where;

import com.samskivert.io.PersistenceException;

import com.hextilla.cardbox.server.CardBoxUserManager;

import static com.hextilla.cardbox.Log.log;

/**
* Provides an interface to our persistent repository of user metadata.
*/
public class FBUserRepository extends DepotRepository
 implements CardBoxUserManager.FacebookUserRepository
{
 /**
  * The database identifier used when establishing a database connection. This value being
  * <code>default</code>.
  */
 public static final String USER_DB_IDENT = "default";

 /**
  * Constructs a new repository with the specified persistence context.
  */
 public FBUserRepository (PersistenceContext ctx)
 {
     super(ctx);
 }
 
 /**
  * Loads information on a single user from the repository. Returns null if no user exists with
  * the specified user id.
  */
 public FBUserRecord loadUser (int userId)
     throws PersistenceException
 {
     return load(FBUserRecord.getKey(userId));
 }
 
 /**
  * Loads information on a single user from the repository. Returns null if no user exists for
  * the specified Facebook id.
  */
 public FBUserRecord loadUser (long fbId)
     throws PersistenceException
 {
	 FBUserMapRecord maptoUser = load(FBUserMapRecord.getKey(fbId));
	 if (maptoUser == null)
	 {
		 log.warning("Query for user by fbId returned 0 rows.", "fbId", fbId);
		 return null;
	 }
     return load(FBUserRecord.getKey(maptoUser.userId));
 }
 
 /**
  * Loads information on a single user from the repository. Returns null if no user exists for
  * the provided session hash.
  */
 public FBUserRecord loadUser (String sessionHash)
     throws PersistenceException
 {
	 SessionMapRecord maptoSession = load(SessionMapRecord.getKey(sessionHash));
	 if (maptoSession == null)
	 {
		 log.warning("Query for user by session returned 0 rows.", "session", sessionHash);
		 return null;
	 }
	 return load(FBUserRecord.getKey(maptoSession.userId));
 }

 /**
  * Inserts the supplied user into the repository. {@link GameRecord#gameId} will be filled in
  * by this method with the user's newly assigned unique identifier.
  */
 public void insertUser (final FBUserRecord user)
     throws PersistenceException
 {
     insert(user);
     FBUserMapRecord maptoUser = new FBUserMapRecord();
     maptoUser.init(user);
     insert(maptoUser);
 }
 
 /**
  * Loads information on a single user from the repository. Returns null if no session exists for
  * the specified user.
  */
 public SessionRecord loadSession (int userId)
     throws PersistenceException
 {
     return load(SessionRecord.getKey(userId));
 }

 /**
  * Inserts the supplied user into the repository. {@link GameRecord#gameId} will be filled in
  * by this method with the user's newly assigned unique identifier.
  */
 public String registerSession (final FBUserRecord user, String authtoken, long expires)
     throws PersistenceException
 {
	 // Since the user's registering a session, I'd say that counts as activity
	 activateUser(user);
	 
	 // If a session record already exists for this user, update with the new token/expires pair
	 SessionRecord sesh = load(SessionRecord.getKey(user.userId));
	 if (sesh == null) {
		 // If there's no session record already, make 'em a new one
    	 return newSession(user.userId, authtoken, expires);
	 } else {
		 return refreshSession(sesh, authtoken, expires);
	 }
 }
 
 /** 
  * Create a new session record, and session map record (hashed).
  * 
  * @param userId Unique CardBox userId
  * @param authtoken Facebook authentication token
  * @param expires Milliseconds when the authtoken will expire
  * @return Hashed authentication token (session ID)
  * @throws PersistenceException
  */
 protected String newSession (int userId, String authtoken, long expires)
 	throws PersistenceException
 {
	 SessionRecord sesh = new SessionRecord();
	 sesh.init(userId, authtoken, expires);
	 
	 String hash_session = hash(authtoken);
	 SessionMapRecord seshmap = new SessionMapRecord();
	 seshmap.init(sesh, hash_session);
	 
	 insert(sesh);
	 insert(seshmap);
	 
	 return hash_session;
 }
 
 /**
  * Update an existing SessionRecord with new FB token, and create a new session map record (hashed)
  * 
  * @param oldsesh Existing session to be updated
  * @param authtoken Facebook authentication token
  * @param expires Milliseconds when the authtoken will expire
  * @return Hashed authentication token (session ID)
  * @throws PersistenceException
  */
 protected String refreshSession (final SessionRecord oldsesh, String authtoken, long expires)
	throws PersistenceException
 {
	 String hash_session = hash(authtoken);
	 SessionMapRecord seshmap = load(SessionMapRecord.getKey(hash(oldsesh.authtoken)));
	 if (seshmap != null)
	 {
		 delete(seshmap);
	 }
	 oldsesh.refresh(authtoken, expires);
	 seshmap = new SessionMapRecord();
	 seshmap.init(oldsesh, hash_session);
	 
	 update(oldsesh);
	 insert(seshmap);
	 
	 return hash_session;
 }

 /**
  * Loads up all of the users in the repository.
  */
 public List<FBUserRecord> loadUsers ()
 {
     return findAll(FBUserRecord.class, new Where(alive()));
 }

 /**
  * Loads the specified number of the most recently created users in the system.
  */
 public List<FBUserRecord> loadRecentlyAdded (final int count)
 {
     return findAll(FBUserRecord.class, new Where(alive()),
                    OrderBy.descending(FBUserRecord.CREATED), new Limit(0, count));
 }

 /**
  * Loads the specified number of the most recently updated users in the system.
  */
 public List<FBUserRecord> loadRecentlyActive (final int count)
 {
     return findAll(FBUserRecord.class, new Where(alive()),
                    OrderBy.descending(FBUserRecord.LAST_ACTIVE), new Limit(0, count));
 }

 /**
  * Updates the supplied user in the repository. Returns true if a matching row was found and
  * updated, false if no rows matched.
  */
 public boolean updateUser (final FBUserRecord user)
     throws PersistenceException
 {
     int mod = update(user);
     switch (mod) {
     case 0: return false;
     case 1:
    	 return true;
     default:
         log.warning("updateUser() modified more than one row?!", "user", user, "modified", mod);
         return true; // something was updated!
     }
 }
 
 /**
  * Update the given user record to reflect recent activity.
  * 
  * @param user User to be updated
  * @return Whether a row was matched/updated
  * @throws PersistenceException
  */
 public boolean activateUser (final FBUserRecord user)
 	 throws PersistenceException
 {
	 Calendar now = Calendar.getInstance();
	 user.activate(now.getTimeInMillis());
	 return updateUser(user);
 }
 
 public int purgeSessions ()
	 throws PersistenceException
 {
	 Calendar now = Calendar.getInstance();
	 Timestamp nowstamp = new Timestamp(now.getTimeInMillis());
	 
	 int rows_deleted = 0;
	 rows_deleted += deleteAll(SessionRecord.class, new Where(SessionRecord.EXPIRES.lessEq(nowstamp)));
	 rows_deleted += deleteAll(SessionMapRecord.class, new Where(SessionMapRecord.EXPIRES.lessEq(nowstamp)));
	 
	 log.info("Call to purgeSessions() removed " + rows_deleted + " total expired session records for approx. " + (rows_deleted/2) + " users.");
	 
	 return rows_deleted;
 }
 
 public static String hash (String s)
 {
	 String algo = "SHA-256";
	 String hashed = null;
	 try {
		 MessageDigest md = MessageDigest.getInstance(algo);
		 hashed = bytes2hex(md.digest(s.getBytes()));
	 } catch (NoSuchAlgorithmException e) {
		 log.warning("Apparently we don't know " + algo, e);
	 }
	 return hashed;
 }
 
 private static String bytes2hex (final byte[] bytes)
 {
	 BigInteger bi = new BigInteger(1, bytes);
	 return String.format("%0" + (bytes.length << 1) + "X", bi);
 }

 @Override
 protected void getManagedRecords (Set<Class<? extends PersistentRecord>> classes)
 {
     classes.add(FBUserRecord.class);
     classes.add(FBUserMapRecord.class);
     classes.add(SessionRecord.class);
     classes.add(SessionMapRecord.class);
 }

 protected static FluentExp alive ()
 {
     return FBUserRecord.STATUS.notEq(FBUserRecord.Status.DEAD.toString());
 }
}
