package com.hextilla.cardbox.lobby.matchmaking;

import static com.hextilla.cardbox.lobby.Log.log;

import java.util.List;
import java.util.Vector;

import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.hextilla.cardbox.lobby.data.LobbyObject;
import com.hextilla.cardbox.util.CardBoxContext;
import com.threerings.crowd.data.PlaceObject;
import com.threerings.parlor.client.SeatednessObserver;
import com.threerings.parlor.client.TableDirector;
import com.threerings.parlor.client.TableObserver;
import com.threerings.parlor.data.Table;
import com.threerings.parlor.data.TableLobbyObject;

// Sits between the table director and the matchmaking views, passes information
// to all matchmakers who are listening.
public class MatchMakerDirector implements TableObserver, SeatednessObserver{

	public MatchMakerDirector(CardBoxContext ctx) {
		observers = new Vector<MatchMaker>();
		_tdtr = new TableDirector(ctx, LobbyObject.TABLE_SET, this);
	}
	
	@Override
	public void seatednessDidChange(boolean isSeated) {
		// Notify all the observers
		for (MatchMaker observer : observers)
		{
			observer.seatednessDidChange(isSeated);
		}		
	}

	@Override
	public void tableAdded(Table table) {
		// Notify all the observers
		for (MatchMaker observer : observers)
		{
			observer.tableAdded(table);
		}		
	}

	@Override
	public void tableUpdated(Table table) {
		// Notify all the observers
		for (MatchMaker observer : observers)
		{
			observer.tableUpdated(table);
		}		
	}

	@Override
	public void tableRemoved(int tableId) {
		// Notify all the observers
		for (MatchMaker observer : observers)
		{
			observer.tableRemoved(tableId);
		}		
	}
	
	public TableDirector getTableDirector()
	{
		return _tdtr;
	}
	
	// Add an observer
	public void addObserver(MatchMaker observer) {
		observers.add(observer);
	}
	
	// Entering and leaving the match maker
	public void setPlace(PlaceObject place) {
        // pass the good word on to our table director
        _tdtr.setTableObject(place);	        
	}

	public void leavePlace(PlaceObject place) {
        // pass the good word on to our table director
        _tdtr.clearTableObject();
	}		
	
    /** A reference to our table director. */
    protected TableDirector _tdtr;	
	
	// Vector of observers
	protected List<MatchMaker> observers;		

}
