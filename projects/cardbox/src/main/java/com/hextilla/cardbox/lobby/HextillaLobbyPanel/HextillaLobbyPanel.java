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

import com.hextilla.cardbox.client.CardBoxButton;
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
import com.hextilla.cardbox.lobby.matchmaking.MatchMakerDirector;
import com.hextilla.cardbox.lobby.matchmaking.MatchMakingButton;
import com.hextilla.cardbox.lobby.matchmaking.StrangerTableFilter;
import com.hextilla.cardbox.lobby.matchmaking.FriendTableFilter;
import com.hextilla.cardbox.lobby.matchmaking.MatchMaker.MatchStatus;
import com.hextilla.cardbox.util.CardBoxContext;
import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.PlaceObject;
import com.threerings.crowd.server.PlaceManager;
import com.threerings.parlor.client.TableDirector;

public class HextillaLobbyPanel extends JPanel implements PlaceView 
{
	
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
        
        // We need to know whether we're running in development mode
        // If so, then don't bother initializing our social services
        _devmode = (config.getGameId() == -1);
        
        // Add the friendPanel (same size as button panel)
        _friendList = new FriendListPanel(ctx, friendlyConfig);
        _friendList.setBorder(BorderFactory.createLineBorder(Color.BLACK));     
        
        // Setup he chat panel, use a tabbed pane
        JTabbedPane chatPane = new JTabbedPane();
        _friendChat = new FriendChatPanel(ctx, true, _ctx.getSocialDirector().getFriends());      
        _globalChat = new ChatPanel(ctx, true);             
        chatPane.addTab("All", null, _globalChat, "Global Chat");
        chatPane.addTab("Friends", null, _friendChat, "Friend Only Chat");                       			             
        
        // Classes to handle match making
        _mdtr = new MatchMakerDirector(ctx);
		_strangerMatchMaker = new MatchMaker(ctx, strangerConfig, _mdtr, 
				new StrangerTableFilter());
		_friendlyMatchMaker = new MatchMaker(ctx, friendlyConfig, _mdtr, 
				new FriendTableFilter(ctx, _ctx.getSocialDirector().getFriends()));		         
		       
		// Stranger Play button
		_strangerPlay = new MatchMakingButton(STRANGER_BUTTON_TEXT, _strangerMatchMaker);
		_strangerPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// Stop the other match making if it is running
				switch (_friendPlay.getState()){
				case MATCHING:
					_friendPlay.stopMatchMaking();
				case STOPPING:
					// Disable the button if stopping (or we just stopped),
					// we will fire the button again matchmaking has stopped
					_strangerPlay.setEnabled(false);
					// Create the listener to fire the button once the other
					// matchmaker has cleared up.
					_friendlyMatchMaker.AddMatchListener(new MatchListener() {
						public boolean update(MatchStatus status, int tableId) {
							// Only care about the stopped status
							if (status != MatchStatus.STOPPED) return false;
							
							log.info("Friendly MM shutdown complete.");
							
							// Re-enable the button, start match making!
							_strangerPlay.setEnabled(true);
							_strangerPlay.startMatchMaking();
							
							// Remove the listener once we get the STOP
							return true;							
						}
					});
					return;
				default:
					break;
				}
				
				switch (_strangerPlay.getState()){
				case MATCHING:
					// Stop if currently running
					_strangerPlay.stopMatchMaking();
					break;
				case STOPPED:
					// Start if stopped
					_strangerPlay.startMatchMaking();
					break;
				default:
					// Should never get here
					log.info("Unhandled state: ", _strangerPlay.getState());
					break;
				}
			}
		});	
		
        // Friend Play button   
		_friendPlay = new MatchMakingButton(FRIENDPLAY_BUTTON_TEXT, _friendlyMatchMaker);
		_friendPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				
				// Stop the other match making if it is running
				switch (_strangerPlay.getState()){
				case MATCHING:
					_strangerPlay.stopMatchMaking();
				case STOPPING:
					// Disable the button if stopping (or we just stopped),
					// we will fire the button again matchmaking has stopped
					_friendPlay.setEnabled(false);
					
					// Create the listener to fire the button once the other
					// matchmaker has cleared up.
					_strangerMatchMaker.AddMatchListener(new MatchListener() {
						public boolean update(MatchStatus status, int tableId) {							
							// Only care about the stopped status
							if (status != MatchStatus.STOPPED) return false;						
							
							log.info("Stranger MM shutdown complete.");
							
							// Re-enable the button, start match making!
							_friendPlay.setEnabled(true);
							_friendPlay.startMatchMaking();
							
							// Remove the listener once we get the STOP
							return true;
						}
					});					
					return;
				default:
					break;
				}
				
				switch (_friendPlay.getState()){
				case MATCHING:
					// Stop if currently running
					_friendPlay.stopMatchMaking();
					break;
				case STOPPED:
					// Start if stopped
					_friendPlay.startMatchMaking();
					break;
				default:
					// Should never get here
					log.info("Unhandled state: ", _friendPlay.getState());
					break;
				}
			}
		});		        
		
        // SoloPlay   	
        _soloPlay = new ComputerOpponentView(_ctx, aiConfig);
        _soloPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// disable the other buttons so they don't get pressed
				_friendPlay.setEnabled(false);
				_strangerPlay.setEnabled(false);
				
				// Friend play is running
				switch (_friendPlay.getState()){
				case MATCHING:
					// Stop if currently running
					_friendPlay.stopMatchMaking();					
					break;
				case STOPPING:
					// Wait for the matchmaking to end
					_friendlyMatchMaker.AddMatchListener(new MatchListener() {
						public boolean update(MatchStatus status, int tableId) {							
							if (status != MatchStatus.STOPPED) return false;													
							_soloPlay.startAIMatch();
							return true;
						}
					});	
					return;
				default:
					break;
				}
				
				// Stranger play is running
				switch (_strangerPlay.getState()){
				case MATCHING:
					// Stop if currently running
					_strangerPlay.stopMatchMaking();					
				case STOPPING:
					// Wait for the matchmaking to end
					_strangerMatchMaker.AddMatchListener(new MatchListener() {
						public boolean update(MatchStatus status, int tableId) {							
							if (status != MatchStatus.STOPPED) return false;													
							_soloPlay.startAIMatch();
							return true;
						}
					});	
					return;
				default:
					break;
				}
				
				_soloPlay.startAIMatch();
			}
		});
        
        // Label to keep track of online players
		_onlinePlayerLabel = new PlayerCountPanel(0);
		JPanel spaceHolder = new JPanel();
        
        // Create the page layout
		// Set the max/min/preferred sizes
		_onlinePlayerLabel.setMaximumSize(PC_MAX_SIZE);
		_onlinePlayerLabel.setPreferredSize(PC_MAX_SIZE);
		_onlinePlayerLabel.setMinimumSize(PC_MIN_SIZE);
		spaceHolder.setMaximumSize(SPACE_MAX_SIZE);
		spaceHolder.setPreferredSize(SPACE_MAX_SIZE);
		spaceHolder.setMinimumSize(SPACE_MAX_SIZE);		
		
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
        
        _friendList.setPreferredSize(LIST_MAX_SIZE);
        _friendList.setMinimumSize(LIST_MIN_SIZE);          
		
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
	    		    				  .addGroup(layout.createSequentialGroup()
	    		    				  			.addComponent(_onlinePlayerLabel)
	    		    				  			.addComponent(spaceHolder))
	    		    				  .addComponent(_friendPlay)
	    		    				  .addComponent(_strangerPlay)
	    		    				  .addComponent(_soloPlay))
		    				  .addComponent(_friendList))
					  .addComponent(chatPane)
	    		);    
        
        // Horizontal Grouping
        // sequential{ parallel{ sequential{optsButton, _matchMaker}, friendPanel}, chatPane}
        layout.setVerticalGroup(
	    		   layout.createSequentialGroup()
	    		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER, true)
	    		    		  .addGroup(layout.createSequentialGroup()
	    		    				  .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, true)
	    		    						  	.addComponent(_onlinePlayerLabel)
	    		    						  	.addComponent(spaceHolder))
	    		    				  .addComponent(_friendPlay)
	    		    				  .addComponent(_strangerPlay)
	    		    				  .addComponent(_soloPlay))
		    				  .addComponent(_friendList))
					  .addComponent(chatPane)
	    		);
	}
	
	public void init(PlaceObject place) 
	{
		//TODO: We need to read in the players who are in games as well
		// Number of player in the lobby
		_onlinePlayerLabel.setOnlinePlayerCount(place.occupantInfo.size());		
		
		willEnterPlace(place);
		_friendList.willEnterPlace(place);
		_friendChat.willEnterPlace(place);
		_globalChat.willEnterPlace(place);
	}
	
	// Entering and leaving the Hextilla panel
	public void willEnterPlace(PlaceObject place) {
		_lobj = (LobbyObject)place;
		_soloPlay.setPlace(place);
		_strangerMatchMaker.setPlace(place);
		_friendlyMatchMaker.setPlace(place);
		_mdtr.setPlace(place);
	}

	public void didLeavePlace(PlaceObject place) {
		_soloPlay.leavePlace(place);
		_strangerMatchMaker.leavePlace(place);
		_friendlyMatchMaker.leavePlace(place);
		_mdtr.leavePlace(place);
	}		
	
	/** ChatPanel objects **/
	protected ChatPanel _friendChat;
	protected ChatPanel _globalChat;
	
    /** Giver of life and services. */
    protected CardBoxContext _ctx;
    
    /** Our lobby distributed object. */
    protected LobbyObject _lobj;
    
    /** The Almighty Friend List */
    protected FriendListPanel _friendList;
    
    // Counts the number of online players
    protected PlayerCountPanel _onlinePlayerLabel;
        
	// Matchmaking classes
	public static MatchMaker _strangerMatchMaker;
	public static MatchMaker _friendlyMatchMaker;  
	protected MatchMakerDirector _mdtr;
    
    // Buttons
    protected ComputerOpponentView _soloPlay;
    protected MatchMakingButton _friendPlay;
    protected MatchMakingButton _strangerPlay;   
    	
    // Button text
    protected static String STRANGER_BUTTON_TEXT = "Play with a Stranger";
    protected static String FRIENDPLAY_BUTTON_TEXT = "Play with a Friend";
	
	// Whether we're running in development mode (i.e. gameId = -1)
	protected boolean _devmode = false;

	// PlayerCount label sizes (and the whitespace beside it)
    protected static Dimension PC_MAX_SIZE = new Dimension(150, 50);
    protected static Dimension PC_MIN_SIZE = new Dimension(75, 25);
    protected static Dimension SPACE_MAX_SIZE = new Dimension(250, 50);
    protected static Dimension SPACE_MIN_SIZE = new Dimension(125, 25);    
	
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
