package com.hextilla.cardbook.auth;

import java.sql.Date;

import com.samskivert.jdbc.jora.FieldMask;
import com.samskivert.servlet.user.Username;
import com.samskivert.util.StringUtil;

/**
 * A user object contains information about a registered user in our web application environment.
 * Users are stored in the user repository (a database table) and loaded during a request based on
 * authentication information provided with the request headers.
 *
 * <p><b>Note:</b> Do not modify any of the fields of this object directly. Use the
 * <code>set</code> methods to make updates. If no set methods exist, you shouldn't be modifying
 * that field.
 */
public class FBUser 
{
    /** The user's assigned integer userid. */
    public int userId;
    
    /** The user's Facebook ID (unique) */
    public String fbId;
    
    /** The date this record was created. */
    public Date created;

    /** The user's chosen username (shown to strangers) */
    public String username;

    /** The user's real name (shown to friends) */
    public String realname;

    /** The user's email address. */
    public String email;

    /** The site identifier of the site through which the user created their account. (Their
     * affiliation, if you will.) */
    public int siteId;

    /**
     * Updates the user's username.
     */
    public void setFacebookId (String id)
    {
    	//TODO: Apply some kind of suitability check to chosen usernames
        this.fbId = id;
        setModified("fbId");
    }
    
    /**
     * Updates the user's username.
     */
    public void setUsername (Username username)
    {
    	//TODO: Apply some kind of suitability check to chosen usernames
        this.username = username.getUsername();
        setModified("username");
    }

    /**
     * Updates the user's real name.
     */
    public void setRealName (String realname)
    {
        this.realname = realname;
        setModified("realname");
    }

    /**
     * Updates the user's email address.
     */
    public void setEmail (String email)
    {
        this.email = email;
        setModified("email");
    }

    /**
     * Updates the user's site id.
     */
    public void setSiteId (int siteId)
    {
        this.siteId = siteId;
        setModified("siteId");
    }
    
    public void setCreated (Date date)
    {
    	this.created = date;
    	setModified("created");
    }

    /**
     * @return true if this User has been "deleted".
     */
    public boolean isDeleted ()
    {
        // a deleted account has an "=" in the username
        return (-1 != username.indexOf('='));
    }

    /**
     * Returns true if this user is an admin, false otherwise. The default implementation does not
     * track admin status and always returns false.
     */
    public boolean isAdmin ()
    {
        return false;
    }

    @Override // from Object
    public String toString ()
    {
        return StringUtil.fieldsToString(this);
    }

    /**
     * Called by the repository to find out which fields have been modified since the object was
     * loaded.
     */
    protected FieldMask getDirtyMask ()
    {
        return _dirty;
    }

    /**
     * Called by the repository to configure this user record with a field mask that it can use to
     * track modified fields.
     */
    protected void setDirtyMask (FieldMask dirty)
    {
        _dirty = dirty;
    }

    /**
     * Marks the supplied field as dirty.
     */
    protected void setModified (String field)
    {
        _dirty.setModified(field);
    }

    /** Our dirty field mask. */
    protected transient FieldMask _dirty;
}
