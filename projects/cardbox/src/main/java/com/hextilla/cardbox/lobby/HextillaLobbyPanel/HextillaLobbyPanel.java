package com.hextilla.cardbox.lobby.HextillaLobbyPanel;

import static com.hextilla.cardbox.lobby.Log.log;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.Timer;

import com.hextilla.cardbox.client.chat.ChatPanel;
import com.hextilla.cardbox.client.chat.FriendChatPanel;
import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.hextilla.cardbox.data.GameDefinition;
import com.hextilla.cardbox.lobby.data.LobbyConfig;
import com.hextilla.cardbox.lobby.data.LobbyObject;
import com.hextilla.cardbox.lobby.friendlist.FriendListPanel;
import com.hextilla.cardbox.lobby.matchmaking.ComputerOpponentView;
import com.hextilla.cardbox.lobby.matchmaking.MatchListener;
import com.hextilla.cardbox.lobby.matchmaking.MatchMaker;
import com.hextilla.cardbox.util.CardBoxContext;
import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.PlaceObject;

public class HextillaLobbyPanel extends JPanel implements PlaceView {
	
	public HextillaLobbyPanel (CardBoxContext ctx, LobbyConfig config)
	{
        _ctx = ctx;
        _lobj = null;
        
        // Get the game definition from the lobby config
        GameDefinition gamedef = config.getGameDefinition();
        
        // We need multiple copies of the game config because they differ slightly for friend games and 
        // stranger/matchmaking games and AI games (how we display info/panels)
        CardBoxGameConfig friendlyConfig = new CardBoxGameConfig(config.getGameId(), gamedef, "friendly");
        CardBoxGameConfig strangerConfig = new CardBoxGameConfig(config.getGameId(), gamedef, "stranger");
        CardBoxGameConfig aiConfig = new CardBoxGameConfig(config.getGameId(), gamedef, "ai");                   
        
        // Add the friendPanel (same size as button panel)
        FriendListPanel friendPanel = new FriendListPanel(ctx, friendlyConfig);
        friendPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));     
        
        // Setup he chat panel, use a tabbed pane
        JTabbedPane chatPane = new JTabbedPane();
        _friendChat = new FriendChatPanel(ctx, true, _ctx.getSocialDirector().getFriends());      
        _globalChat = new ChatPanel(ctx, true);             
        chatPane.addTab("All", null, _globalChat, "Global Chat");
        chatPane.addTab("Friends", null, _friendChat, "Friend Only Chat");                       			
        
        // Class which handles match making, uses stranger config (matchmaking is against randoms)
		_matchMaker = new MatchMaker(ctx, strangerConfig);              
        
        // SoloPlay   	
        _soloPlay = new ComputerOpponentView(_ctx, aiConfig);      
        
        // FriendPlay    
		_friendPlay = new JButton(FRIENDPLAY_BUTTON_TEXT);
        
		// Action listener to update _strangerPlay button elipses while searching for matches
		ActionListener elipseUpdater = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				_strangerPlay.setText(SEARCHING_TEXT[elipses]);				
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
					elipses = 0;
					elipseTimer.stop();				
					break;
				case CANCELED:
					break;
				default:
					break;
				}
			}
		};	
		_matchMaker.AddMatchListener(matchFound);			
		       
		// Stranger Play button, engages matchmaker	
		_strangerPlay = new JButton(MATCHMAKING_BUTTON_TEXT);
		_strangerPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// Toggle text on the button
				if (_strangerPlay.getText() == MATCHMAKING_BUTTON_TEXT){
					elipseTimer.start();					
					_matchMaker.startMatchMaking();   					
				} else {
					elipses = 0;
					elipseTimer.stop();
					_strangerPlay.setText(MATCHMAKING_BUTTON_TEXT);					
					_matchMaker.stopMatchMaking();   					
				}
			}
		});
        
        // Create the page layout
		// Set the max/min/preferred sizes
		_strangerPlay.setMaximumSize(BUTTON_MAX_SIZE);
		_strangerPlay.setPreferredSize(BUTTON_MAX_SIZE);
		_strangerPlay.setMinimumSize(BUTTON_MIN_SIZE);
		
		_friendPlay.setMaximumSize(BUTTON_MAX_SIZE);
		_friendPlay.setPreferredSize(BUTTON_MAX_SIZE);			
		_friendPlay.setMinimumSize(BUTTON_MIN_SIZE);
			
		_soloPlay._playButton.setMaximumSize(BUTTON_MAX_SIZE);	
		_soloPlay._playButton.setPreferredSize(BUTTON_MAX_SIZE);		
		_soloPlay._playButton.setMinimumSize(BUTTON_MIN_SIZE);	
		
        _friendChat.setMaximumSize(CHAT_MAX_SIZE);
        _globalChat.setPreferredSize(CHAT_MAX_SIZE);
        _globalChat.setMinimumSize(CHAT_MIN_SIZE);
        
        _globalChat.setMaximumSize(CHAT_MAX_SIZE);
        _globalChat.setPreferredSize(CHAT_MAX_SIZE);
        _globalChat.setMinimumSize(CHAT_MIN_SIZE);        
        
        friendPanel.setPreferredSize(LIST_MAX_SIZE);
        friendPanel.setMinimumSize(LIST_MIN_SIZE);          
		
		// Layout Manager woop-a-doop
        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);        
        this.setLayout(layout);  
        
        // Horizontal Grouping
        // parallel{ sequential{ parallel{optsButton, _matchMaker}, friendPanel}, chatPane}
        layout.setHorizontalGroup(
	    		   layout.createParallelGroup(GroupLayout.Alignment.CENTER)
	    		      .addGroup(layout.createSequentialGroup()
	    		    		  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER, true)
	    		    				  .addComponent(_friendPlay)
	    		    				  .addComponent(_strangerPlay)
	    		    				  .addComponent(_soloPlay))
		    				  .addComponent(friendPanel))
					  .addComponent(chatPane)
	    		);    
        
        // Horizontal Grouping
        // sequential{ parallel{ sequential{optsButton, _matchMaker}, friendPanel}, chatPane}
        layout.setVerticalGroup(
	    		   layout.createSequentialGroup()
	    		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER, true)
	    		    		  .addGroup(layout.createSequentialGroup()
	    		    				  .addComponent(_friendPlay)
	    		    				  .addComponent(_strangerPlay)
	    		    				  .addComponent(_soloPlay))
		    				  .addComponent(friendPanel))
					  .addComponent(chatPane)
	    		);
	}
	
	public void init(PlaceObject place) {
		willEnterPlace(place);
		_friendChat.willEnterPlace(place);
		_globalChat.willEnterPlace(place);
	}
	
	// Entering and leaving the Hextilla panel
	public void willEnterPlace(PlaceObject place) {
		_lobj = (LobbyObject)place;
		_soloPlay.setPlace(place);
		_matchMaker.setPlace(place);
	}

	public void didLeavePlace(PlaceObject place) {
		_soloPlay.leavePlace(place);
		_matchMaker.leavePlace(place);
	}		
	
	/** ChatPanel objects **/
	protected ChatPanel _friendChat;
	protected ChatPanel _globalChat;
	
    /** Giver of life and services. */
    protected CardBoxContext _ctx;
    
    /** Our lobby distributed object. */
    protected LobbyObject _lobj;
        
	// The underlying matchmaking class
	public static MatchMaker _matchMaker;    
    
    // Buttons
    protected ComputerOpponentView _soloPlay;
    protected JButton _friendPlay;
    protected JButton _strangerPlay;   
    	
    // Button text
    protected static String MATCHMAKING_BUTTON_TEXT = "Play with a Stranger";
    protected static String FRIENDPLAY_BUTTON_TEXT = "Play with a Friend";
    
    // Searching text
    protected static String[] SEARCHING_TEXT = {
			"Searching",
			"Searching.",
			"Searching..",
			"Searching..."};		    
	
	// Animates the "..." in searching
	protected Timer elipseTimer;
	protected int elipses = 0; 

    // Button sizes
    protected static Dimension BUTTON_MAX_SIZE = new Dimension(400, 100);
    protected static Dimension BUTTON_MIN_SIZE = new Dimension(200, 25);
    
	// Max/Min Sizes for the Friend List
    protected static Dimension LIST_MAX_SIZE = new Dimension(400, 300);
    protected static Dimension LIST_MIN_SIZE = new Dimension(200, 75);	
    
	// Max/Min Sizes for the Chat
    protected static Dimension CHAT_MAX_SIZE = new Dimension(800, 200);
    protected static Dimension CHAT_MIN_SIZE = new Dimension(400, 50);	    
}
