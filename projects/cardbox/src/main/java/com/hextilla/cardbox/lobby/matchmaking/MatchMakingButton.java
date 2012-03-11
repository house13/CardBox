package com.hextilla.cardbox.lobby.matchmaking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import com.hextilla.cardbox.client.HextillaButton;

@SuppressWarnings("serial")
public class MatchMakingButton extends HextillaButton {
	
	public MatchMakingButton(String text, MatchMaker matchMaker)
	{
		// superclass constructor
		super(text);
		MATCHMAKING_BUTTON_TEXT = text;
		_matchMaker = matchMaker;
		
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
	}
	
	public void startMatchMaking()
	{
		setText(SEARCHING_TEXT[_elipses]);
		_elipseTimer.start();					
		_matchMaker.startMatchMaking();  
	}
	
	public void stopMatchMaking()
	{
		_elipseTimer.stop();
		_elipses = 0;
		setText(MATCHMAKING_BUTTON_TEXT);					
		_matchMaker.stopMatchMaking();   		
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
}

