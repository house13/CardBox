package com.hextilla.cardbox.data;

import com.samskivert.util.StringUtil;
import com.threerings.io.Streamable;

/** Most basic representation of our 6-sided Card data structure.
 *  Used to abstract away from the Depot or distObj representation.
 */
public class HexCard implements Streamable
{
	/** The card's unique integer identifier. */
	public int index;
	
	/** The card's elemental type. */
	public int element;
	
	/** Array representing the card's powers, starting clockwise from North side. */
	public int[] sides = {0, 0, 0, 0, 0, 0};
	
	/** Generates a string representation of this instance. */
    @Override
    public String toString ()
    {
        return StringUtil.fieldsToString(this);
    }
}
