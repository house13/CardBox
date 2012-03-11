package com.hextilla.cardbox.lobby.matchmaking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import com.hextilla.cardbox.client.HextillaButton;
import com.hextilla.cardbox.lobby.matchmaking.MatchMaker.MatchStatus;

@SuppressWarnings("serial")
public class MatchMakingButton extends HextillaButton {
	public enum State {
		MATCHING,	// MatchMaker is running
		STOPPING,	// MatchMaker is stopping
		STOPPED		// MatchMaker has stopped
	}
	
	public MatchMakingButton(String text, MatchMaker matchMaker)
	{
		// superclass constructor
		super(text);
		MATCHMAKING_BUTTON_TEXT = text;
		_matchMaker = matchMaker;
		_state = State.STOPPED;
		
		// Reference to the button (this)
		_button = this;
		
		// Action listener to update elipses while searching for matches
		ActionListener strangerElipseUpdater = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {			
				if (_elipses == 3) {
					_elipses = 0;
				}
				else
				{
					++_elipses;
				}
				_button.setText(SEARCHING_TEXT[_elipses]);					
			}
		};		
		_elipseTimer = new Timer(1000, strangerElipseUpdater);
		
		// Add a listener to the matchMaker
		MatchListener matchListener = new MatchListener() {
			
			@Override
			public boolean update(MatchStatus status, int tableId) {
				if ((status == MatchStatus.STOPPED) && (_state == State.STOPPING))
				{
					_state = State.STOPPED;
					_button.setEnabled(true);					
				}
				return false;
			}
		};
		_matchMaker.AddMatchListener(matchListener);
	}
	
	public void startMatchMaking()
	{
		setText(SEARCHING_TEXT[_elipses]);
		_elipseTimer.start();					
		_matchMaker.startMatchMaking();
		_state = State.MATCHING;
	}
	
	public void stopMatchMaking()
	{
		_elipseTimer.stop();
		_elipses = 0;
		_matchMaker.stopMatchMaking();	
		setText(MATCHMAKING_BUTTON_TEXT);
		_state = State.STOPPING;
		_button.setEnabled(false);
	}	
	
	// Returns the current state of the button
	public State getState()
	{
		return _state;
	}
	
	// Animates the "..." in searching
	protected Timer _elipseTimer;
	protected int _elipses = 0;		
	
	// Reference to the button itself
	MatchMakingButton _button;
	
	// Reference to the matchmaking object
	MatchMaker _matchMaker;
	
    // Searching text
    protected static String[] SEARCHING_TEXT = {
			"Searching",
			"Searching.",
			"Searching..",
			"Searching..."};	
    
    protected String MATCHMAKING_BUTTON_TEXT;
    
    // The current state of the button
    protected State _state;
}

