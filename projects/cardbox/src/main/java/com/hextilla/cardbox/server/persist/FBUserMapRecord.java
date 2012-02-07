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

import com.samskivert.depot.Key;
import com.samskivert.depot.PersistentRecord;
import com.samskivert.depot.annotation.Column;
import com.samskivert.depot.annotation.Entity;
import com.samskivert.depot.annotation.Id;
import com.samskivert.depot.expression.ColumnExp;
import com.samskivert.util.StringUtil;

/**
 * Contains information about a game registration.
 */
@Entity(name="FBID_USER_MAP")
public class FBUserMapRecord extends PersistentRecord
{
    // AUTO-GENERATED: FIELDS START
    public static final Class<FBUserMapRecord> _R = FBUserMapRecord.class;
    public static final ColumnExp USER_ID = colexp(_R, "userId");
    public static final ColumnExp FB_ID = colexp(_R, "fbId");
    // AUTO-GENERATED: FIELDS END

    public static final int SCHEMA_VERSION = 1;
    
    /** Defines the possible values for {@link #status}. */
    public enum Status { NEW, ONLINE, OFFLINE, DEAD }

    /** The user's Facebook ID (unique) */
    @Id @Column(name="FB_ID")
    public String fbId;
    
    /** The user's unique integer identifier. */
    @Column(name="USER_ID")
    public int userId;
    
    /**
     * Returns a brief description of this user.
     */
    public String which ()
    {
        return fbId + " (" + userId + ")";
    }

    /**
     * Provides a string representation of this instance.
     */
    @Override
    public String toString ()
    {
        return StringUtil.fieldsToString(this);
    }
    
    public void init (final FBUserRecord fbuser)
    {
    	this.fbId = fbuser.fbId;
    	this.userId = fbuser.userId;
    }

    // AUTO-GENERATED: METHODS START
    /**
     * Create and return a primary {@link Key} to identify a {@link FBUserRecord}
     * with the supplied key values.
     */
    public static Key<FBUserMapRecord> getKey (String fbId)
    {
        return newKey(_R, fbId);
    }

    /** Register the key fields in an order matching the getKey() factory. */
    static { registerKeyFields(FB_ID); }
    // AUTO-GENERATED: METHODS END
}

