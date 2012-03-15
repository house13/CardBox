package com.hextilla.cardbox.data;

import java.util.List;
import java.util.Random;

import com.samskivert.util.StringUtil;
import com.threerings.io.Streamable;

public class HexDeck implements Streamable
{
	/** The number of cards in this deck. */
	public int size = 0;
	
	/** Keep a HEAD pointer to maintain a stack-like appearance */
	public int top = 0;
	
	/** The cards in this deck. */
	public HexCard[] cards = null;
	
	public void setCards (HexCard[] array)
	{
		size = array.length;
		cards = array;
	}
	
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
    
    /** Return the current card on top of the deck and increment our index pointer */
    public HexCard draw ()
    {
    	// You can't draw past the end of the deck
    	if (top >= size) {
    		return null;
    	} else {
    		HexCard drawn = cards[top];
    		++top;
    		return drawn;
    	}
    }
    
    /** Reset the top of the deck back to 0 */
    public void reset ()
    {
    	top = 0;
    }

    /** Use Fisher-Yates to shuffle a given source deck into a new HexDeck */
    public static HexDeck shuffle(final HexDeck source, Random rand)
    {
    	// We'll be building a shuffled deck from our source deck
    	HexDeck shuffled = new HexDeck();
    	
    	// If we're passed an empty deck, then that's what you get.
    	if (source.size == 0)
    		return shuffled;
    	
    	// Start with a copy of the source card set
    	HexCard[] shuffling = new HexCard[source.size];
    	System.arraycopy(source.cards, 0, shuffling, 0, source.size);
    	
    	// Employ the Fisher-Yates shuffling algorithm to sort our copy of the source
    	for (int i = shuffling.length - 1; i > 0 ; --i)
    	{
    		int j = rand.nextInt(i);
    		HexCard swap = shuffling[i];
    		shuffling[i] = shuffling[j];
    		shuffling[j] = swap;
    	}
    	
    	// At this point, our array should be shuffled, so shimmy it into a deck
    	shuffled.setCards(shuffling);
    	
    	return shuffled;
    }
}
