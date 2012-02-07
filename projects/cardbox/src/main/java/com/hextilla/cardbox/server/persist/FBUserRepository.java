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
import com.samskivert.util.Tuple;

import com.hextilla.cardbox.server.CardBoxManager;

import static com.hextilla.cardbox.Log.log;

/**
* Provides an interface to our persistent repository of user metadata.
*/
public class FBUserRepository extends DepotRepository
 implements CardBoxManager.FacebookUserRepository
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
  * the specifed id.
  */
 public FBUserRecord loadUser (int userId)
     throws PersistenceException
 {
     return load(FBUserRecord.getKey(userId));
 }
 
 /**
  * Loads information on a single user from the repository. Returns null if no user exists with
  * the specifed id.
  */
 public FBUserRecord loadUserByFbId (String fbId)
     throws PersistenceException
 {
     List<FBUserRecord> result = findAll(FBUserRecord.class, new Where(fbIdMatch(fbId)));
     
     if (result == null || result.isEmpty())
     { 
    	 return null; 
     }
     else 
     {
    	 return result.get(0);
     }
 }
 
 /**
  * Loads information on a single user from the repository. Returns null if no user exists with
  * the specifed id.
  */
 public FBUserRecord loadUserBySession (String authtoken)
     throws PersistenceException
 {
     List<SessionRecord> result = findAll(SessionRecord.class, new Where(sessionMatch(authtoken)));
     
     if (result == null || result.isEmpty())
     { 
    	 return null; 
     }
     else 
     {
    	 if (result.size() > 1)
    	 {
    		 StringBuilder warn = new StringBuilder(
    				 "loadUserBySession returned multiple rows for [ session=" + authtoken + " ]\n");
    		 for (SessionRecord sesh : result)
    		 {
    			 warn.append(sesh.toString());
    		 }
    		 log.warning(warn.toString());
    	 }
    	 return load(FBUserRecord.getKey(result.get(0).userId));
     }
 }

 /**
  * Inserts the supplied user into the repository. {@link GameRecord#gameId} will be filled in
  * by this method with the user's newly assigned unique identifier.
  */
 public void insertUser (final FBUserRecord user)
     throws PersistenceException
 {
     insert(user);
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
 public void registerSession (final FBUserRecord user, String authtoken, long expires)
     throws PersistenceException
 {
	 SessionRecord sesh;
	 // If a session record already exists for this user, update with the new token/expires pair
	 sesh = loadSession(user.userId);
	 if (sesh == null) {
		// If there's no session record already, make 'em a new one
    	 sesh = new SessionRecord();
    	 sesh.init(user.userId, authtoken, expires);
    	 insert(sesh);
	 } else {
		 sesh.refresh(authtoken, expires);
    	 update(sesh);
	 }
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
     case 2:
    	 return true;
     default:
         log.warning("updateUser() modified more than two rows?!", "user", user, "modified", mod);
         return true; // something was updated!
     }
 }
 
 public int pruneSessions ()
	 throws PersistenceException
 {
	 return deleteAll(SessionRecord.class, new Where(sessionExpired()));
 }

 @Override
 protected void getManagedRecords (Set<Class<? extends PersistentRecord>> classes)
 {
     classes.add(FBUserRecord.class);
     classes.add(SessionRecord.class);
 }

 protected static FluentExp alive ()
 {
     return FBUserRecord.STATUS.notEq(FBUserRecord.Status.DEAD.toString());
 }
 
 protected static FluentExp fbIdMatch (String fbId)
 {
	 return FBUserRecord.FB_ID.eq(fbId);
 }
 
 protected static FluentExp sessionMatch (String authtoken)
 {
	 return SessionRecord.AUTHTOKEN.eq(authtoken);
 }
 
 protected static FluentExp sessionExpired ()
 {
	 Calendar now = Calendar.getInstance();
	 return SessionRecord.EXPIRES.lessEq(new Timestamp(now.getTimeInMillis()));
 }
}
