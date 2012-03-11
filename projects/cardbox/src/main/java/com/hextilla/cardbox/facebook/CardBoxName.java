package com.hextilla.cardbox.facebook;

import com.threerings.util.Name;

public class CardBoxName extends Name
{
	public CardBoxName (long fbId, String first, String last, String username, boolean anonymous)
	{
		super(String.valueOf(fbId));
		_fbId = fbId;
		_first = first;
		_last = last;
		_full = first + " " + last;
		_username = new Name(username);
		_anonymous = anonymous;
	}
	
	public Name getFriendlyName ()
	{
		return new Name(_full);
	}
	
	public Name getStrangerName ()
	{
		if (_anonymous) {
			return getUsername();
		} else {
			return new Name(_first + " " + _last.charAt(0));
		}
	}
	
	public Name getUsername ()
	{
		return _username;
	}
	
	public long getFacebookId ()
	{
		return _fbId;
	}
	
    /**
     * Gives this name a chance to override the default comparison in a symmetric fashion.
     *
     * @return the result of the comparison, or null for no override.
     */
	@Override
    protected Integer overrideCompareTo (Name other)
    {
		if (other instanceof CardBoxName)
		{
			CardBoxName cbn = (CardBoxName) other;
			return getFriendlyName().compareTo(cbn.getFriendlyName());
		}
        return null;
    }
	
	/**
     * By default, 
     */
	@Override
	public String toString ()
	{
	    return getStrangerName().toString();
	}

	/**
	 * An easily extensible method via which derived classes can add to {@link #toString}'s output.
	 */
	protected void toString (StringBuilder buf)
	{
		String sep = ", ";
	    buf.append("fbId=").append(_fbId).append(sep);
	    buf.append("first=").append(_first).append(sep);
	    buf.append("last=").append(_last).append(sep);
	    buf.append("username=").append(_username.toString()).append(sep);
	    buf.append("anonymous=").append(_anonymous);
	}
	
    // Numeric Facebook ID is stored as long and string (parent's _name)
	protected long _fbId;
	protected String _first;
	protected String _last;
	protected String _full;
	protected Name _username;
	protected boolean _anonymous;
}
