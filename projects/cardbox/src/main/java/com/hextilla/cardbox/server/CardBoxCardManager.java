package com.hextilla.cardbox.server;

import static com.hextilla.cardbox.data.CardBoxCodes.TOYBOX_GROUP;
import static com.threerings.presents.data.InvocationCodes.INTERNAL_ERROR;
import static com.hextilla.cardbox.Log.log;

import java.util.List;
import java.util.ArrayList;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.samskivert.depot.PersistenceContext;
import com.samskivert.io.PersistenceException;
import com.samskivert.util.Invoker;
import com.samskivert.util.ResultListenerList;
import com.threerings.presents.annotation.MainInvoker;
import com.threerings.presents.client.InvocationService.ResultListener;
import com.threerings.presents.data.ClientObject;
import com.threerings.presents.server.InvocationException;
import com.threerings.presents.server.InvocationManager;
import com.threerings.presents.util.PersistingUnit;
import com.threerings.presents.util.ResultAdapter;

import com.hextilla.cardbox.data.HexCard;
import com.hextilla.cardbox.data.HexDeck;
import com.hextilla.cardbox.server.persist.CardRecord;
import com.hextilla.cardbox.server.persist.GameRecord;
import com.hextilla.cardbox.server.persist.GameSupplyRepository;

@Singleton
public class CardBoxCardManager 
	implements CardBoxCardProvider
{
	@Inject public CardBoxCardManager (InvocationManager invmgr)
    {
        // register ourselves as providing the cardbox service
        invmgr.registerDispatcher(new CardBoxCardDispatcher(this), TOYBOX_GROUP);
        _collection = new ArrayList<HexCard>();
        _deck = new HexDeck();
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
    
 // documentation inherited from interface
    public void getCards (ClientObject caller, final int gameId, final ResultListener rl)
        throws InvocationException
    {
        // look to see if we have already resolved a lobby for this game
        HexDeck cards = getCards();
        if (cards != null) {
            rl.requestProcessed(cards);
            return;
        }
    }
    
    public HexDeck getCards()
    {
    	// If we have an empty set of cards, try to pull the set down from the repo
    	if (_collection == null || _collection.isEmpty())
    	{
    		List<CardRecord> records = _cardmgr.supply();
    		log.info("Records supplied: ", "Records", records.size());
    		for (CardRecord record : records)
    		{
    			_collection.add(transmuteCard(record));
    			log.info("Getting card ", record);
    		}
    		_deck.setCards(_collection);
    	}
    	
    	return _deck;
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
    protected HexDeck _deck;
    
    /** Handles database business. */
    @Inject protected @MainInvoker Invoker _invoker;
}
