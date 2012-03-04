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

package com.hextilla.cardbox.data;

import javax.annotation.Generated;

import com.hextilla.cardbox.facebook.CardBoxName;
import com.threerings.crowd.data.BodyObject;
import com.threerings.crowd.data.TokenRing;
import com.threerings.util.Name;

/**
 * Extends the {@link BodyObject} with some custom bits needed for CardBox.
 */
public class CardBoxUserObject extends BodyObject
{
    // AUTO-GENERATED: FIELDS START
    /** The field name of the <code>tokens</code> field. */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public static final String TOKENS = "tokens";

    /** The field name of the <code>session</code> field. */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public static final String SESSION = "session";
    // AUTO-GENERATED: FIELDS END

    /** Indicates which access control tokens are held by this user. */
    public TokenRing tokens;
    
    /** Authenticated session token held by this user. */
    public String session;

    @Override // from BodyObject
    public TokenRing getTokens ()
    {
        return tokens;
    }
    
    @Override //from BodyObject
    public Name getVisibleName ()
    {
    	return username;
    }
    
    public String getSession ()
    {
    	return session;
    }
    
    public long getFacebookId ()
    {
    	if (username instanceof CardBoxName) 
    	{
    		return ((CardBoxName)username).getFacebookId();
    	}
    	
    	return 0;
    }
    
    public Name getFriendlyName()
    {
    	if (username instanceof CardBoxName)
    	{
    		return ((CardBoxName)username).getFriendlyName();
    	}
    	
    	return username;
    }
    
    public Name getStrangerName()
    {
    	if (username instanceof CardBoxName)
    	{
    		return ((CardBoxName)username).getStrangerName();
    	}
    	
    	return username;
    }

    // AUTO-GENERATED: METHODS START
    /**
     * Requests that the <code>tokens</code> field be set to the
     * specified value. The local value will be updated immediately and an
     * event will be propagated through the system to notify all listeners
     * that the attribute did change. Proxied copies of this object (on
     * clients) will apply the value change when they received the
     * attribute changed notification.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void setTokens (TokenRing value)
    {
        TokenRing ovalue = this.tokens;
        requestAttributeChange(
            TOKENS, value, ovalue);
        this.tokens = value;
    }

    /**
     * Requests that the <code>session</code> field be set to the
     * specified value. The local value will be updated immediately and an
     * event will be propagated through the system to notify all listeners
     * that the attribute did change. Proxied copies of this object (on
     * clients) will apply the value change when they received the
     * attribute changed notification.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void setSession (String value)
    {
        String ovalue = this.session;
        requestAttributeChange(
            SESSION, value, ovalue);
        this.session = value;
    }
    // AUTO-GENERATED: METHODS END
}
