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

import java.security.MessageDigest;
import java.sql.Timestamp;

import com.samskivert.depot.Key;
import com.samskivert.depot.PersistentRecord;
import com.samskivert.depot.annotation.Column;
import com.samskivert.depot.annotation.Entity;
import com.samskivert.depot.annotation.Id;
import com.samskivert.depot.expression.ColumnExp;
import com.samskivert.util.StringUtil;

/**
 * Contains information about authenticated user Facebook sessions
 */
@Entity(name="SESSION_MAP")
public class SessionMapRecord extends PersistentRecord
{
    // AUTO-GENERATED: FIELDS START
    public static final Class<SessionMapRecord> _R = SessionMapRecord.class;
    public static final ColumnExp SESSION_TOKEN = colexp(_R, "sessionToken");
    public static final ColumnExp USER_ID = colexp(_R, "userId");
    public static final ColumnExp EXPIRES = colexp(_R, "expires");
    // AUTO-GENERATED: FIELDS END

    public static final int SCHEMA_VERSION = 1;

    @Id @Column(name="SESSION_TOKEN")
    public String sessionToken;
    
    @Column(name="USER_ID")
    public int userId;
    
    @Column(name="EXPIRES")
    public Timestamp expires;
    
    public void init (final SessionRecord sesh, String hash_session)
    {
    	this.sessionToken = hash_session;
    	this.userId = sesh.userId;
    	this.expires = sesh.expires;
    }
    
    /**
     * Provides a string representation of this instance.
     */
    @Override
    public String toString ()
    {
        return StringUtil.fieldsToString(this);
    }

    // AUTO-GENERATED: METHODS START
    /**
     * Create and return a primary {@link Key} to identify a {@link SessionRecord}
     * with the supplied key values.
     */
    public static Key<SessionMapRecord> getKey (String sessionToken)
    {
        return newKey(_R, sessionToken);
    }

    /** Register the key fields in an order matching the getKey() factory. */
    static { registerKeyFields(SESSION_TOKEN); }
    // AUTO-GENERATED: METHODS END
}
