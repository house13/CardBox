package com.hextilla.cardbox.server;

import static com.hextilla.cardbox.Log.log;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.samskivert.depot.PersistentRecord;
import com.samskivert.io.PersistenceException;
import com.samskivert.util.IntMap;
import com.samskivert.util.Interval;
import com.samskivert.util.Invoker;

import com.threerings.presents.annotation.MainInvoker;
import com.threerings.presents.server.InvocationManager;
import com.threerings.presents.server.PresentsDObjectMgr;

public class CardBoxSupplyManager<T extends PersistentRecord>
{
	public interface SupplyRepository<T>
    {
    	/** Loads persistent user data from userId */
    	public List<T> loadSupplies ()
    		throws PersistenceException;
    }
	
	public CardBoxSupplyManager ()
    {
    }
	
	/**
     * Prepares the cardbox supply manager for operation.
     */
    public void init (SupplyRepository<T> supplyrepo)
        throws PersistenceException
    {
    	_supplyrepo = supplyrepo;
    	
    	if (_supplyrepo != null)
    	{
    		_supplies = _supplyrepo.loadSupplies();
    		
    		log.info("CardBoxSupplyManager loaded game supplies successfully.");
    	}
    }
    
    public List<T> supply ()
    {
    	return _supplies;
    }
    
    
    protected SupplyRepository<T> _supplyrepo;
    protected List<T> _supplies;
}
