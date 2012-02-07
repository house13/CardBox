package com.hextilla.cardbox.lobby.matchmaking;

import static com.hextilla.cardbox.lobby.Log.log;

import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;

import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.hextilla.cardbox.data.TableMatchConfig;
import com.hextilla.cardbox.lobby.data.LobbyObject;
import com.hextilla.cardbox.lobby.table.TableItem;
import com.hextilla.cardbox.util.CardBoxContext;
import com.samskivert.swing.util.SwingUtil;
import com.threerings.crowd.client.PlaceView;
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
		AVAILABLE,	// Match found! Press Accept!
		CANCELED	// Match was cancelled (you or someone else did not accept)
	}

	public MatchMaker(CardBoxContext ctx, CardBoxGameConfig config) {
		listeners = new Vector<MatchListener>();
        _config = config;
        _ctx = ctx;
        
        // Initiate Lists
        _openList = new Vector<Table>();
        _playList = new Vector<Table>();
        
        // Engage table director
        _tdtr = new TableDirector(ctx, LobbyObject.TABLE_SET, this);

        // Set addSeatednessObserver to stun
        _tdtr.addSeatednessObserver(this);      
        
	    // Setup the table configuration
        _tconfig = new TableConfig();
        _tconfig.minimumPlayerCount = ((TableMatchConfig)_config.getGameDefinition().match).minSeats;
        _tconfig.desiredPlayerCount = 2;
	}

	// Accept and start the currently matched game
	public void startGame() {	
		Table table = _tdtr.getSeatedTable();		
		if (table != null){
			log.info("Starting game: " + table.tableId);	
        	_ctx.getLocationDirector().moveTo(table.gameOid);
		} else {
			//TODO: game canceled or something?
		}
	}

	// Start searching for a game
	public void startMatchMaking() {
		log.info("Start Matchmaking...");		

		// Load-up the table lists		
        for (Table table : _tlobj.getTables()) {
            tableAdded(table);
        }	
        
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
		log.info("Leaving Matchmaking...");
		Table table = _tdtr.getSeatedTable();
		
		// Leave the table if sitting at one
		if (table != null){
			_tdtr.leaveTable(table.tableId);
		}
		
        // clear out our table lists
        _openList.clear();
        _playList.clear();			
	}

	// Add listener
	public void AddMatchListener(MatchListener listener) {
		listeners.add(listener);		
	}

	// Remove listener
	public void RemoveMatchListener(MatchListener listener) {
		listeners.remove(listener);		
	}	
	
	// Update all the listeners of the change
	public void NotifyMatchListeners(MatchStatus status) {
	    for (MatchListener listener : listeners) {
	    	listener.update(status);
	    }
	}

	public void seatednessDidChange(boolean arg0) {
		// TODO Auto-generated method stub		
	}

	// Table added to table manager
	public void tableAdded(Table table) {
		log.info("Table added: " + table.tableId);
		if (table.inPlay()){
			_playList.add(table);
			return;
		}
		_openList.add(table);
	}

	// Clean up references to tables when one is removed
	public void tableRemoved(int id) {
		log.info("Table removed: " + id);
		// Search inplay list
	    for (Table table : _playList) {
	    	if (table.tableId == id) {
	    		_playList.remove(table);
	    		return;
	    	}
	    }
	    
	    // Search open list
	    for (Table table : _openList) {
	    	if (table.tableId == id) {
	    		_openList.remove(table);
	    		return;
	    	}
	    }	    
	}

	// Update tables
	public void tableUpdated(Table updatedTable) {
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
	                // Move table to playin tables
	                _openList.remove(table);
	                _playList.add(updatedTable);
	                
	                // Notify the user that they have been matched
	                NotifyMatchListeners(MatchStatus.AVAILABLE);
	    			
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
        
        // pass the good word on to our table director
        _tdtr.setTableObject(place);	
	}

	public void leavePlace(PlaceObject place) {
        log.info("Leaving MatchMaker");
        
        // pass the good word on to our table director
        _tdtr.clearTableObject();
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
    
    /** The interface used to configure a table before creating it. */
    protected GameConfigurator _figger;    
    
    // Table configuration object
    protected TableConfig _tconfig;   
    
    // Reference to the lobby
    TableLobbyObject _tlobj;    
}
