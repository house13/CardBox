package com.hextilla.cardbox.server;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.samskivert.depot.PersistenceContext;
import com.samskivert.io.PersistenceException;

import com.hextilla.cardbox.data.HexCard;
import com.hextilla.cardbox.server.persist.CardRecord;
import com.hextilla.cardbox.server.persist.GameSupplyRepository;

@Singleton
public class CardBoxCardManager 
{
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
    
    public List<HexCard> getCards()
    {
    	// If we have an empty set of cards, try to pull the set down from the repo
    	if (_collection.isEmpty())
    	{
    		List<CardRecord> records = _cardmgr.supply();
    		for (CardRecord record : records)
    		{
    			_collection.add(transmuteCard(record));
    		}
    	}
    	
    	return _collection;
    }
    
    protected HexCard transmuteCard (CardRecord record)
    {
    	HexCard card = new HexCard();
    	if (record == null) return card;
    	
    	card.index = record.cardID;
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
    
    protected List<HexCard> _collection;
}
