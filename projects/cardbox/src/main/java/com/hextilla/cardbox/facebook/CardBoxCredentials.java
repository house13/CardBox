package com.hextilla.cardbox.facebook;

import com.threerings.presents.net.Credentials;

public class CardBoxCredentials extends Credentials
{
	public CardBoxCredentials (String session_id)
	{
		_session = session_id;
	}
	
	public String getSession ()
	{
		return _session;
	}
	
	@Override
    public String getDatagramSecret ()
    {
        return _session;
    }

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
        buf.append("session=").append(_session);
    }

	protected String _session;
}
