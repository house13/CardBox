package com.hextilla.cardbox.lobby.matchmaking;

import static com.hextilla.cardbox.lobby.Log.log;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.hextilla.cardbox.data.TableMatchConfig;
import com.hextilla.cardbox.util.CardBoxContext;
import com.threerings.crowd.data.PlaceObject;
import com.threerings.parlor.client.SeatednessObserver;
import com.threerings.parlor.client.TableDirector;
import com.threerings.parlor.client.TableObserver;
import com.threerings.parlor.data.Table;
import com.threerings.parlor.data.TableConfig;
import com.threerings.parlor.data.TableLobbyObject;
import com.threerings.parlor.game.client.GameConfigurator;

public class MatchMaker implements TableObserver, SeatednessObserver 
{

	// Use an enum to indicate status, we may need to change this if we need more info
	public enum MatchStatus {
		STOPPED,		// Matchmaking stopped and tables cleaned up
		GAME_STARTED
	}

	public MatchMaker(CardBoxContext ctx, CardBoxGameConfig config, MatchMakerDirector mdtr, TableFilter filter) {
		listeners = new Vector<MatchListener>();
        _config = config;
        _ctx = ctx;
        _filter = filter;
        
        // Initiate Lists
        _openList = new Vector<Table>();
        _playList = new Vector<Table>();
                
        // Engage table director
        mdtr.addObserver(this);
        _tdtr = mdtr.getTableDirector();

        // Set addSeatednessObserver to stun
        _tdtr.addSeatednessObserver(this);      
        
	    // Setup the table configuration
        _tconfig = new TableConfig();
        _tconfig.minimumPlayerCount = ((TableMatchConfig)_config.getGameDefinition().match).minSeats;
        _tconfig.desiredPlayerCount = 2;
	}

	// Start searching for a game
	public void startMatchMaking() {
		log.info("Start Matchmaking...");		
		
		// Search for open games
	    for (Table table : _openList) {
	    	// Try to join at the next open position (Join at position 2, host sits in 1)
	    	//TODO: may need to keep track of open seats using seatedness observer
	    	//TODO: We need to try again if join fails (how to detect this...?)
	    	
            _tdtr.joinTable(table.tableId, 1);
            return;
	    }
	    
	    // No open tables, make a new table
        _tdtr.createTable(_tconfig, _config);
	}

	// Stop Searching for a game
	public void stopMatchMaking() {
		log.info("Stop Matchmaking...");
		Table table = _tdtr.getSeatedTable();
		
		// Leave the table if sitting at one
		if (table != null){
			// Save the table id so we can check for when its finally removed
			_currentTableID = table.tableId;

			// Leave the table
			_tdtr.leaveTable(table.tableId);
		}
		else
		{
            // Notify the user cleanup is complete.
            NotifyMatchListeners(MatchStatus.STOPPED, -1);
		}		
	}

	// Add listener
	public void AddMatchListener(MatchListener listener) {
		listeners.add(listener);		
	}

	// Remove listener
	public void RemoveMatchListener(MatchListener listener) {
		listeners.remove(listener);		
	}	
	
	// Update all the listeners of the change, remove those which return true
	public void NotifyMatchListeners(MatchStatus status, int tableId) {
		Iterator<MatchListener> it = listeners.iterator();
		while(it.hasNext()) 
		{
			MatchListener currentListener = it.next();
	    	if (currentListener.update(status, tableId))
	    	{
	    		it.remove();
	    	}
	    }
	}

	public void seatednessDidChange(boolean arg0) {
		// TODO Auto-generated method stub		
	}

	// Table added to table manager
	public void tableAdded(Table table) {
		if (!_filter.filter(table)) return;
		
		//TODO Add listener for updates here (for friend list stuff)		
		
		log.info("Table added: " + table.tableId);
		if (table.inPlay()){
			_playList.add(table);
			return;
		}
		_openList.add(table);
	}

	// Clean up references to tables when one is removed
	public void tableRemoved(int id) {
		// Search inplay list
	    for (Table table : _playList) {
	    	if (table.tableId == id) {
	    		log.info("Table removed: " + id);
	    		_playList.remove(table);
	    		
	    		// Notify people who are waiting when the current table is deleted
	    		if (_currentTableID == id)
	    		{
	    			_currentTableID = -1;
	    			
	                // Notify the user that the game they were in was removed and cleanup is complete.
	                NotifyMatchListeners(MatchStatus.STOPPED, id);
	    		}
	    		return;
	    	}
	    }
	    
	    // Search open list
	    for (Table table : _openList) {
	    	if (table.tableId == id) {
	    		_openList.remove(table);
	    		log.info("Table removed: " + id);
	    		
	    		// Notify people who are waiting when the current table is deleted
	    		if (_currentTableID == id)
	    		{
	    			_currentTableID = -1;
	    			
	                // Notify the user that the game they were in was removed and cleanup is complete.
	                NotifyMatchListeners(MatchStatus.STOPPED, id);
	    		}
	    		
	    		return;
	    	}
	    }	    
	}

	// Update tables
	public void tableUpdated(Table updatedTable) {
		if (!_filter.filter(updatedTable)) return;
		
		//TODO Add listener for updates here (for friend list stuff)
		
		log.info("Table updated: " + updatedTable.tableId);
		// Search inplay list
	    for (Table table : _playList) {
	    	if (table.tableId == updatedTable.tableId) {
	    		table = updatedTable;	    		
	    		return;
	    	}
	    }
	    
	    // Search open list
	    for (Table table : _openList) {
	    	if (table.tableId == updatedTable.tableId) {
	    		
	    		// Move the table to the other list if the game is transitioning to in play
	    		if (table.gameOid != -1){     
	    			log.info("Possible match found...");
	                // Move table to playing tables
	                _openList.remove(table);
	                _playList.add(updatedTable);
	                
	                // Notify listeners that a game has moved from open, to started
	                NotifyMatchListeners(MatchStatus.GAME_STARTED, table.tableId);
	    			
    			// Otherwise just update the current entry	    			
	    		} else {
		    		table = updatedTable;
	    		}
	    		
	    		return;
	    	}
	    }			
	}		

	// Entering and leaving the match maker
	public void setPlace(PlaceObject place) {
        log.info("Entering MatchMaker");
        _tlobj = (TableLobbyObject)place;          
        
		// Load-up the table lists		
        for (Table table : _tlobj.getTables()) 
        {        	
        	// Only add the ones that pass the filter
        	if (!_filter.filter(table)) continue;
        	
            tableAdded(table);
        }	        
	}

	public void leavePlace(PlaceObject place) {
        log.info("Leaving MatchMaker");

        _openList.clear();
        _playList.clear();
	}	
	
	// Vector of listeners
	protected List<MatchListener> listeners;	
	
    /** A reference to the client context. */
    protected CardBoxContext _ctx;

    /** The configuration for the game that we're match-making. */
    protected CardBoxGameConfig _config;
    
    /** A reference to our table director. */
    protected TableDirector _tdtr;	
    
    /** The list of tables that are in play. */
    protected List<Table> _playList;
    
    /** The list of tables with open spaces */
    protected List<Table> _openList;
    
    /** Filter which filters out tables from updates, such as no friend tables
     * or stranger only tables
     */
    protected TableFilter _filter;
    
    /** The interface used to configure a table before creating it. */
    protected GameConfigurator _figger;    
    
    // Table configuration object
    protected TableConfig _tconfig;   

    // The id of the current table we are seated at. Used to make sure the table was
    // deleted before starting matchmaking again.
    protected int _currentTableID = -1;
    
    // Reference to the lobby
    TableLobbyObject _tlobj;    
}
