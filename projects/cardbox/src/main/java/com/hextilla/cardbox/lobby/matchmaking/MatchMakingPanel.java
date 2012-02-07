package com.hextilla.cardbox.lobby.matchmaking;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Timer;

import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.hextilla.cardbox.util.CardBoxContext;

// Displays the match making information
public class MatchMakingPanel extends JPanel{
	// The underlying matchmaking class
	public static MatchMaker matchMaker;
	
	// Animates the "..." in searching
	protected Timer elipseTimer;
	protected int elipses = 0;
	
	// Buttons that need referencing so they can hide and stuff
	protected JLabel _statusBox;
	protected JButton _matchButton;
	protected JButton _acceptButton;
	
	public MatchMakingPanel(CardBoxContext ctx, CardBoxGameConfig config) {
		matchMaker = new MatchMaker(ctx, config);
		
		// TODO: make the Searching label thing its own class instead of programming like a shithead
		SEARCHING_TEXT = new String[4];
		SEARCHING_TEXT[0] = "Searching";
		SEARCHING_TEXT[1] = "Searching.";
		SEARCHING_TEXT[2] = "Searching..";
		SEARCHING_TEXT[3] = "Searching...";		
		
		// Action listener to update _status box elipse while searching for matches
		ActionListener elipseUpdater = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				_statusBox.setText(SEARCHING_TEXT[elipses]);				
				if (elipses == 3) {
					elipses = 0;
				}
				else
				{
					++elipses;
				}
			}
		};		
		elipseTimer = new Timer(1000, elipseUpdater);
		
		// Add a listener for successful matches
		MatchListener matchFound = new MatchListener() {
			public void update(MatchMaker.MatchStatus status) {
				switch (status) {
				case AVAILABLE:
					_acceptButton.setVisible(true);
					elipses = 0;
					elipseTimer.stop();
					_matchButton.setText("MATCH FOUND");					
					break;
				case CANCELED:
					_acceptButton.setVisible(false);
					break;
				default:
					break;
				}
			}
		};	
		matchMaker.AddMatchListener(matchFound);
		
		// Split the panel into two pieces
		setLayout(new GridLayout(2, 1));
		JPanel top = new JPanel(new BorderLayout());
		JPanel bottom = new JPanel();			
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
		top.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));			
		bottom.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));				
		       
		// Matchmaking start/cancel button	
		_matchButton = new JButton(MATCHMAKING_BUTTON_TEXT);
		_matchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// Toggle text on the button
				if (_matchButton.getText() == MATCHMAKING_BUTTON_TEXT){
					_matchButton.setText(MATCHMAKING_CANCEL_TEXT);
					elipses = 0;
					elipseTimer.stop();					
					_statusBox.setText(" ");					
					matchMaker.startMatchMaking();   					
				} else {
					_matchButton.setText(MATCHMAKING_BUTTON_TEXT);
					elipseTimer.start();
					matchMaker.stopMatchMaking();   					
				}
			}
		}); 		
		top.add(_matchButton, BorderLayout.CENTER);
		
		// Group the Accept button with the info text
		// Text box for information showing (Searching..., Match Found! etc)
		_statusBox = new JLabel(" ");
		_statusBox.setAlignmentX(JLabel.CENTER_ALIGNMENT);

		_statusBox.setFont(new Font("Serif", Font.BOLD, 16));
		_statusBox.setForeground(Color.WHITE);
		_statusBox.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));		
		bottom.add(_statusBox);
		
		// Area for buttons when match is found (its an empty pane otherwise)
		JPanel joinPane = new JPanel();
		bottom.add(joinPane);
		
		// Button for accepting a match
		_acceptButton = new JButton("Accept");
		_acceptButton.addActionListener(
			new ActionListener () {
				public void actionPerformed(ActionEvent e) {
					matchMaker.startGame();
				}
			});	
		_acceptButton.setVisible(false);		
		joinPane.add(_acceptButton);
		
		add(top);		
		add(bottom);			
	}		
	
    // Button text
    protected String MATCHMAKING_BUTTON_TEXT = "Random Opponent";
    protected String MATCHMAKING_CANCEL_TEXT = "Cancel Search";
    
    // Searching text
    protected String[] SEARCHING_TEXT;
}
