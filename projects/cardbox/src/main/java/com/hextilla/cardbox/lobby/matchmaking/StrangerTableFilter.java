package com.hextilla.cardbox.lobby.matchmaking;

import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.threerings.parlor.data.Table;

public class StrangerTableFilter implements TableFilter {

	@Override
	public boolean filter(Table table) {
		if (table.config instanceof CardBoxGameConfig){		
			return !((CardBoxGameConfig)table.config).getGameMode().equals("friendly");
		}
		return false;
	}

}
