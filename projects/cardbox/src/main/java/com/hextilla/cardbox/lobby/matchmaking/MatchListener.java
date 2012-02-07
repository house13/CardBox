package com.hextilla.cardbox.lobby.matchmaking;

import com.hextilla.cardbox.lobby.matchmaking.MatchMaker.MatchStatus;

// Listener class for the MatchMaker and MatchMakingPanel
public interface MatchListener {
	void update (MatchStatus status);
}
