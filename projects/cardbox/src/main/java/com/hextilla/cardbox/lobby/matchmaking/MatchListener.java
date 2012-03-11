package com.hextilla.cardbox.lobby.matchmaking;

import com.hextilla.cardbox.lobby.matchmaking.MatchMaker.MatchStatus;

// Listener class for the MatchMaker and MatchMakingPanel
public interface MatchListener {
	// Return true if the listener should be deleted
	boolean update (MatchStatus status, int tableId);
}
