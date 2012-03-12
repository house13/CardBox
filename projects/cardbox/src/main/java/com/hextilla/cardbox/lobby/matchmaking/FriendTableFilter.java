package com.hextilla.cardbox.lobby.matchmaking;

import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.facebook.client.FriendSet;
import com.hextilla.cardbox.util.CardBoxContext;
import com.samskivert.util.Folds.F;
import com.samskivert.util.Log;
import com.threerings.parlor.data.Table;
import static com.hextilla.cardbox.lobby.Log.log;

public class FriendTableFilter implements TableFilter {

	public FriendTableFilter(CardBoxContext ctx, FriendSet friends)
	{
		super();
		_ctx = ctx;
		_clientName = ((CardBoxName)ctx.getClient().getClientObject().username);
	}
	
	@Override
	public boolean filter(Table table) {
		if (table.config instanceof CardBoxGameConfig){
			// Friendly game, but was it made by our friend?
			if (((CardBoxGameConfig)table.config).getGameMode().equals("friendly"))
			{
				if (table.getPlayers().length > 0){
					CardBoxName currentPlayer = ((CardBoxName)table.getPlayers()[0]);
					if (currentPlayer.equals(_clientName) || 
							_ctx.getSocialDirector().isOnlineFriend(currentPlayer)) 
					{
						// Friend made the game						
						return true;
					}
					else if (table.getPlayers().length > 1)
					{
						// See if our friend is joined an existing game with
						// one of their friends
						currentPlayer = (CardBoxName) table.getPlayers()[1];
						return (currentPlayer.equals(_clientName) || 
								_ctx.getSocialDirector().isOnlineFriend(currentPlayer));						
					}
				}
			}
		}
		// Not a friend game
		return false;
	}
	
	// The CardBoxContext
	protected CardBoxContext _ctx;
	
	// The current users face book id
	CardBoxName _clientName;
}
