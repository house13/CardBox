package com.hextilla.cardbox.server;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.samskivert.depot.PersistenceContext;
import com.samskivert.io.PersistenceException;

import com.hextilla.cardbox.server.persist.CardRecord;
import com.hextilla.cardbox.server.persist.GameSupplyRepository;

@Singleton
public class CardBoxCardManager 
{
	/** Most basic representation of our Card data structure.
	 *  Used to abstract away from the Depot representation.
	 */
	public static final class Card
	{
		public int element;
		public int[] sides = {0, 0, 0, 0, 0, 0};
	}
	
	@Inject public CardBoxCardManager ()
    {
    }
	
	/**
     * Prepares the card manager for operation.
     */
    public void init (PersistenceContext ctx)
        throws PersistenceException
    {
    	_cardmgr = new CardBoxSupplyManager<CardRecord>();
    	_cardrepo = new GameSupplyRepository<CardRecord>(ctx, CardRecord.class);
    	
    	ctx.initializeRepositories(true);
    	_cardmgr.init(_cardrepo);
    }
    
    public List<Card> getCards()
    {
    	// If we have an empty set of cards, try to pull the set down from the repo
    	if (_collection.isEmpty())
    	{
    		List<CardRecord> records = _cardmgr.supply();
    		for (CardRecord record : records)
    		{
    			_collection.add(transmute(record));
    		}
    	}
    	
    	return _collection;
    }
    
    protected Card transmute (CardRecord record)
    {
    	Card card = new Card();
    	if (record == null) return card;
    	
    	card.element = record.element;
    	for (int i = 0; i < 6; ++i)
    	{
    		switch (i) 
    		{
	    		case 0: { card.sides[i] = record.northPower;     break; }
	    		case 1: { card.sides[i] = record.northEastPower; break; }
	    		case 2: { card.sides[i] = record.southEastPower; break; }
	    		case 3: { card.sides[i] = record.southPower;     break; }
	    		case 4: { card.sides[i] = record.southWestPower; break; }
	    		case 5: { card.sides[i] = record.northWestPower; break; }
    		}
    	}
    	return card;
    }
    
    protected CardBoxSupplyManager<CardRecord> _cardmgr;
    protected GameSupplyRepository<CardRecord> _cardrepo;
    
    protected List<Card> _collection;
}
