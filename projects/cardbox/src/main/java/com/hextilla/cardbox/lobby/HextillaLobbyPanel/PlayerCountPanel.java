package com.hextilla.cardbox.lobby.HextillaLobbyPanel;
import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hextilla.cardbox.client.CardBoxUI;
import com.threerings.presents.client.Client;
import com.threerings.presents.client.SessionObserver;

public class PlayerCountPanel extends JPanel implements SessionObserver
{
	PlayerCountPanel(int currentPlayerCount)
	{
		super(new BorderLayout());
		_playerCount = currentPlayerCount;
		
		_onlineCountTextLabel = new JLabel(_onlineCountText, JLabel.LEFT);
		_onlineCountTextLabel.setFont(CardBoxUI.AppFontSmall);
		add(_onlineCountTextLabel, BorderLayout.WEST);
		
		_onlineCountNumberLabel = new JLabel("" + _playerCount, JLabel.RIGHT);
		_onlineCountNumberLabel.setFont(CardBoxUI.AppFontSmall);
		add(_onlineCountNumberLabel, BorderLayout.CENTER);
	}

	public void clientWillLogon(Client client) {
	}

	public void clientDidLogon(Client client) {
		++_playerCount;
		_onlineCountNumberLabel.setText("" + _playerCount);
	}

	public void clientObjectDidChange(Client client) {
	}

	public void clientDidLogoff(Client client) {
		_playerCount--;
		_onlineCountNumberLabel.setText("" + _playerCount);
	}
	
	// The number of players currently connected
	long _playerCount;	
	
	// The labels for showing the online players
	JLabel _onlineCountTextLabel;
	JLabel _onlineCountNumberLabel;
	
	// Label text
	protected static String _onlineCountText = "Players Online: ";

	public void setOnlinePlayerCount(long count) {
		_playerCount = count;
		_onlineCountNumberLabel.setText("" + _playerCount);
	}

}
