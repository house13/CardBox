package com.hextilla.cardbox.lobby.HextillaLobbyPanel;
import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hextilla.cardbox.client.CardBoxUI;
import com.hextilla.cardbox.util.CardBoxContext;
import com.threerings.crowd.client.OccupantObserver;
import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.OccupantInfo;
import com.threerings.crowd.data.PlaceObject;

public class PlayerCountPanel extends JPanel implements OccupantObserver, PlaceView
{
	PlayerCountPanel(CardBoxContext ctx, int currentPlayerCount)
	{
		super(new BorderLayout());
		_playerCount = currentPlayerCount;
		
		_onlineCountTextLabel = new JLabel(_onlineCountText, JLabel.LEFT);
		_onlineCountTextLabel.setFont(CardBoxUI.AppFontSmall);
		add(_onlineCountTextLabel, BorderLayout.WEST);
		
		_onlineCountNumberLabel = new JLabel("" + _playerCount, JLabel.RIGHT);
		_onlineCountNumberLabel.setFont(CardBoxUI.AppFontSmall);
		add(_onlineCountNumberLabel, BorderLayout.CENTER);
		
		ctx.getOccupantDirector().addOccupantObserver(this);
	}

	// Label text
	protected static String _onlineCountText = "Players Online: ";

	public void setOnlinePlayerCount(long count) {
		_playerCount = count;
		_onlineCountNumberLabel.setText("" + _playerCount);
	}

	public void occupantEntered(OccupantInfo info) {
		++_playerCount;
		_onlineCountNumberLabel.setText("" + _playerCount);		
	}

	public void occupantLeft(OccupantInfo info) {
		_playerCount--;
		_onlineCountNumberLabel.setText("" + _playerCount);
	}

	public void occupantUpdated(OccupantInfo oldinfo, OccupantInfo newinfo) {
	}

	public void willEnterPlace(PlaceObject place) {
		//TODO: We need to read in the players who are in games as well
		// Number of player in the lobby
		setOnlinePlayerCount(place.occupantInfo.size());				
	}

	public void didLeavePlace(PlaceObject place) {
	
	}
	
	
	// The number of players currently connected
	long _playerCount;	
	
	// The labels for showing the online players
	JLabel _onlineCountTextLabel;
	JLabel _onlineCountNumberLabel;
		

}
