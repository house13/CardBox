package com.hextilla.cardbox.data;

/** Most basic representation of our 6-sided Card data structure.
 *  Used to abstract away from the Depot or distObj representation.
 */
public class HexCard
{
	/** The card's unique integer identifier. */
	public int index;
	
	/** The card's elemental type. */
	public int element;
	
	/** Array representing the card's powers, starting clockwise from North side. */
	public int[] sides = {0, 0, 0, 0, 0, 0};
}
