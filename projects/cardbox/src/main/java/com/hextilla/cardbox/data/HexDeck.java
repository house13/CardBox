package com.hextilla.cardbox.data;

import java.util.List;

import com.samskivert.util.StringUtil;
import com.threerings.io.Streamable;

public class HexDeck implements Streamable
{
	/** The number of cards in this deck. */
	public int size;
	
	/** The cards in this deck. */
	public HexCard[] cards;
	
	public void setCards (List<HexCard> list)
	{
		size = list.size();
		cards = list.toArray(new HexCard[size]);
	}
	
	/** Generates a string representation of this instance. */
    @Override
    public String toString ()
    {
        return StringUtil.fieldsToString(this);
    }

}
