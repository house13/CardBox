package com.hextilla.cardbox.facebook;

import com.threerings.util.Name;

public class CardBoxName extends Name
{
	public CardBoxName (long fbId, String username, String first, String last)
	{
		super(String.valueOf(fbId));
		_fbId = fbId;
		_username = username;
		_first = first;
		_last = last;
	}
	
	public Name getFriendlyName ()
	{
		return new Name(_first + " " + _last);
	}
	
	public Name getStrangerName ()
	{
		return new Name(_first + " " + _last.charAt(0));
	}
	
	public long getFacebookId ()
	{
		return _fbId;
	}
	
	/**
     * Returns the unprocessed name as a string.
     */
	@Override
	public String toString ()
	{
	    StringBuilder buf = new StringBuilder("[");
	    toString(buf);
	    return buf.append("]").toString();
	}

	/**
	 * An easily extensible method via which derived classes can add to {@link #toString}'s output.
	 */
	protected void toString (StringBuilder buf)
	{
	    buf.append("fbId=").append(_fbId);
	    buf.append("username=").append(_username);
	    buf.append("first=").append(_first);
	    buf.append("last=").append(_last);
	}
	
    // Numeric Facebook ID is stored as long and string (parent's _name)
	protected long _fbId;
	
	protected String _username;
	protected String _first;
	protected String _last;
}
