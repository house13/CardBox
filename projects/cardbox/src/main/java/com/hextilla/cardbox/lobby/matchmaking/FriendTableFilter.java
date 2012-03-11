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
		_friends = friends;
		_userID = ((CardBoxName)ctx.getClient().getClientObject().username).getFacebookId();
	}
	
	@Override
	public boolean filter(Table table) {
		if (table.config instanceof CardBoxGameConfig){
			// Friendly game, but was it made by our friend?
			if (((CardBoxGameConfig)table.config).getGameMode().equals("friendly"))
			{
				if (table.getPlayers().length > 0){
					long id = ((CardBoxName)table.getPlayers()[0]).getFacebookId();
					if (id == _userID || _friends.isFriend(id)) 
					{
						// Friend made the game						
						return true;
					}
					else if (table.getPlayers().length > 1)
					{
						// See if our friend is joined an existing game with
						// one of their friends
						id = ((CardBoxName)table.getPlayers()[1]).getFacebookId();
						return (id == _userID || _friends.isFriend(id));
						
					}
					// Neither of the players are our friend :(
					return false;
				}
			}
		}
		// Not a friend game
		return false;
	}
	
	// The user's set of friends
	protected FriendSet _friends;
	
	// The current users face book id
	long _userID;
}
