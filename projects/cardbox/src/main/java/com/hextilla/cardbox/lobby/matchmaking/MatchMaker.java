package com.hextilla.cardbox.lobby.matchmaking;

import java.util.ArrayList;

import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.hextilla.cardbox.util.CardBoxContext;

public class MatchMaker {
	// Vector of listeners
	ArrayList<MatchListener> listeners;

	// Use an enum to indicate status, we may need to change this if we need more info
	public enum MatchStatus {
		AVAILABLE,	// Match found! Press Accept!
		CANCELED	// Match was canceled (you or someone else did not accept)
	}

	public MatchMaker(CardBoxContext ctx, CardBoxGameConfig config) {
		listeners = new ArrayList<MatchListener>();
	}

	// Accept the currently matched game
	public void startGame() {
		// TODO Auto-generated method stub
		
	}

	// Start searching for a game
	public void startMatchMaking() {
		// TODO Auto-generated method stub
		
	}

	// Stop Searching for a game
	public void stopMatchMaking() {
		// TODO Auto-generated method stub
		
	}

	// Add listener
	public void AddMatchListener(MatchListener listener) {
		//listeners.add(listener);		
	}

	// Remove listener
	public void RemoveMatchListener(MatchListener listener) {
		//listeners.remove(listener);		
	}	
	
	// Update all the listeners of the change
	public void NotifyMatchListeners(MatchStatus status) {
	    //for (MatchListener listener : listeners) {
	    	//listener.update(status);
	    //}
	}

}
