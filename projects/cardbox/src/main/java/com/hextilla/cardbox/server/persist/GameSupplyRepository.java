package com.hextilla.cardbox.server.persist;

import java.util.List;
import java.util.Set;

import com.samskivert.depot.DepotRepository;
import com.samskivert.depot.Exps;
import com.samskivert.depot.PersistenceContext;
import com.samskivert.depot.PersistentRecord;
import com.samskivert.depot.clause.Where;
import com.samskivert.io.PersistenceException;

import com.hextilla.cardbox.server.CardBoxSupplyManager;

public class GameSupplyRepository<T extends PersistentRecord> extends DepotRepository
	implements CardBoxSupplyManager.SupplyRepository<T>
{
	/**
	  * The database identifier used when establishing a database connection. This value being
	  * <code>default</code>.
	  */
	 public static final String SUPPLY_DB_IDENT = "supply";

	 /**
	  * Constructs a new repository with the specified persistence context.
	  */
	 public GameSupplyRepository (PersistenceContext ctx, Class<T> type)
	 {
	     super(ctx);
	     _type = type;
	 }
	 
	 /** Idea: Gameplay-related data is stored in the database, loaded once on initialization */
	 public List<T> loadSupplies ()
	    	throws PersistenceException
	 { 
		 return findAll(_type, new Where(Exps.value(true)));
	 }
	 
	 @Override
	 protected void getManagedRecords (Set<Class<? extends PersistentRecord>> classes)
	 {
		 classes.add(_type);
	 }
	 
	 protected Class<T> _type;
}
