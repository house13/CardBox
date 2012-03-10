//
// CardBox library - framework for matchmaking networked games
// Copyright (C) 2005-2011 Three Rings Design, Inc., All Rights Reserved
// http://github.com/threerings/game-gardens
//
// This library is free software; you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published
// by the Free Software Foundation; either version 2.1 of the License, or
// (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package com.hextilla.cardbox.server.persist;

import java.io.StringReader;
import java.sql.Date;

import com.samskivert.depot.Key;
import com.samskivert.depot.PersistentRecord;
import com.samskivert.depot.annotation.Column;
import com.samskivert.depot.annotation.Entity;
import com.samskivert.depot.annotation.GeneratedValue;
import com.samskivert.depot.annotation.GenerationType;
import com.samskivert.depot.annotation.Id;
import com.samskivert.depot.expression.ColumnExp;
import com.samskivert.util.StringUtil;

import com.hextilla.cardbox.server.CardBoxConfig;

import com.restfb.types.User;

/**
 * Contains information about a game registration.
 */
@Entity(name="USERS")
public class FBUserRecord extends PersistentRecord
{
    // AUTO-GENERATED: FIELDS START
    public static final Class<FBUserRecord> _R = FBUserRecord.class;
    public static final ColumnExp USER_ID = colexp(_R, "userId");
    public static final ColumnExp FB_ID = colexp(_R, "fbId");
    public static final ColumnExp STATUS = colexp(_R, "status");
    public static final ColumnExp USERNAME = colexp(_R, "username");
    public static final ColumnExp FIRSTNAME = colexp(_R, "firstname");
    public static final ColumnExp LASTNAME = colexp(_R, "lastname");
    public static final ColumnExp EMAIL = colexp(_R, "email");
    public static final ColumnExp ANONYMOUS = colexp(_R, "anonymous");
    public static final ColumnExp CREATED = colexp(_R, "created");
    public static final ColumnExp LAST_ACTIVE = colexp(_R, "lastActive");
    // AUTO-GENERATED: FIELDS END

    public static final int SCHEMA_VERSION = 2;
    
    /** Defines the possible values for {@link #status}. */
    public enum Status { NEW, ONLINE, OFFLINE, DEAD }
    
    /** The user's unique integer identifier. */
    @Id @Column(name="USER_ID") @GeneratedValue(strategy=GenerationType.IDENTITY)
    public int userId;
    
    /** The user's Facebook ID (unique) */
    @Column(name="FB_ID")
    public long fbId;
    
    /** The status of the user. We can't use the enumeration directly here as this class is
     * persisted and JORA doesn't (and can't be made to) automagically handle enums. */
    @Column(name="STATUS")
    public String status;

    /** The user's chosen username (shown to strangers) */
    @Column(name="USERNAME")
    public String username;

    /** The user's real first name (shown to friends) */
    @Column(name="FIRSTNAME")
    public String firstname;
    
    /** The user's real last name (shown to you) */
    @Column(name="LASTNAME")
    public String lastname;

    /** The user's email address. */
    @Column(name="EMAIL")
    public String email;
    
    /** How this user appears to strangers */
    @Column(name="ANONYMOUS")
    public boolean anonymous;
    
    /** The date this user record was created. */
    @Column(name="CREATED")
    public Date created;
    
    /** The last date this user record was active. */
    @Column(name="LAST_ACTIVE")
    public Date lastActive;
    
    /** Returns the status of this user. */
    public Status getStatus ()
    {
        return Status.valueOf(status);
    }

    /** Updates the status of this user. */
    public void setStatus (Status status)
    {
        this.status = status.toString();
    }
    
    /**
     * Returns a brief description of this user.
     */
    public String which ()
    {
        return username + " (" + userId + ")";
    }

    /**
     * Provides a string representation of this instance.
     */
    @Override
    public String toString ()
    {
        return StringUtil.fieldsToString(this);
    }
    
    public void init (final User fbuser, final long created)
    {
    	this.fbId = Long.valueOf(fbuser.getId());
    	this.username = _default_username;
    	this.firstname = fbuser.getFirstName();
    	this.lastname = fbuser.getLastName();
    	this.email = fbuser.getEmail();
    	this.anonymous = false;
    	this.created = new Date(created);
    	this.lastActive = new Date(created);
    	setStatus(Status.NEW);
    }
    
    public void activate (final long now)
    {
    	this.lastActive = new Date(now);
    }
    
    public static final String _default_username = CardBoxConfig.config.getValue("default_username", "Anonymous");

    // AUTO-GENERATED: METHODS START
    /**
     * Create and return a primary {@link Key} to identify a {@link FBUserRecord}
     * with the supplied key values.
     */
    public static Key<FBUserRecord> getKey (int userId)
    {
        return newKey(_R, userId);
    }

    /** Register the key fields in an order matching the getKey() factory. */
    static { registerKeyFields(USER_ID); }
    // AUTO-GENERATED: METHODS END
}
