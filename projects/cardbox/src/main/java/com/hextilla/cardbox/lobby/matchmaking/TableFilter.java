package com.hextilla.cardbox.lobby.matchmaking;

import com.threerings.parlor.data.Table;

public interface TableFilter {
	// Returns true if the table passes the filter, false otherwise
	boolean filter(Table table);
}
