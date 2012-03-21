package com.hextilla.cardbox.lobby.client;

import static com.hextilla.cardbox.lobby.Log.log;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import com.hextilla.cardbox.client.CardBoxUI;
import com.hextilla.cardbox.client.chat.ChatPanel;
import com.hextilla.cardbox.client.chat.FriendChatPanel;
import com.hextilla.cardbox.client.chat.StrangerChatPanel;
import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.hextilla.cardbox.data.GameDefinition;
import com.hextilla.cardbox.lobby.data.LobbyCodes;
import com.hextilla.cardbox.lobby.data.LobbyConfig;
import com.hextilla.cardbox.lobby.data.LobbyObject;
import com.hextilla.cardbox.lobby.friendlist.FriendListPanel;
import com.hextilla.cardbox.lobby.invite.InvitationPanel;
import com.hextilla.cardbox.lobby.matchmaking.ComputerOpponentView;
import com.hextilla.cardbox.lobby.matchmaking.MatchListener;
import com.hextilla.cardbox.lobby.matchmaking.MatchMaker;
import com.hextilla.cardbox.lobby.matchmaking.MatchMakerDirector;
import com.hextilla.cardbox.lobby.matchmaking.MatchMakingButton;
import com.hextilla.cardbox.lobby.matchmaking.StrangerTableFilter;
import com.hextilla.cardbox.lobby.matchmaking.FriendTableFilter;
import com.hextilla.cardbox.lobby.matchmaking.MatchMaker.MatchStatus;
import com.hextilla.cardbox.swing.CardBoxContextualButton;
import com.hextilla.cardbox.swing.CardBoxTabbedPanel;
import com.hextilla.cardbox.swing.PlayerCountPanel;
import com.hextilla.cardbox.util.CardBoxContext;
import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.PlaceObject;
import com.threerings.util.MessageBundle;


public class CardBoxLobbyPanel extends JPanel implements PlaceView 
{
	
	public CardBoxLobbyPanel (CardBoxContext ctx, LobbyConfig config)
	{
        _ctx = ctx;
        _lobj = null;
        _msgs = _ctx.getMessageManager().getBundle(LobbyCodes.LOBBY_MSGS);
        
        // Get the game definition from the lobby config
        GameDefinition gamedef = config.getGameDefinition();
        
        // We need multiple copies of the game config because they differ slightly for friend games and 
        // stranger/matchmaking games and AI games (how we display info/panels)
        CardBoxGameConfig friendlyConfig = new CardBoxGameConfig(config.getGameId(), gamedef, "friendly");
        CardBoxGameConfig strangerConfig = new CardBoxGameConfig(config.getGameId(), gamedef, "stranger");
        CardBoxGameConfig aiConfig = new CardBoxGameConfig(config.getGameId(), gamedef, "ai");    
        
        // We need to know whether we're running in development mode
        // If so, then don't bother initialising our social services
        _devmode = (config.getGameId() == -1);
        
        // Add the friendPanel (same size as button panel)
        _friendList = new FriendListPanel(ctx, friendlyConfig);  
        
        // In case you have some kind of icky state kicking around I guess
        _ctx.getInvitationDirector().clearInvitations();
        
        // Use our friendly game configuration in our invitations
        _ctx.getInvitationDirector().init(friendlyConfig);
                
        // Modify the look/feel of the tabbedPane
        UIDefaults def = UIManager.getLookAndFeelDefaults();
        def.put("TabbedPane.unselectedBackground", CardBoxUI.DARK_BLUE);
        def.put("TabbedPane.selected", CardBoxUI.CHAT_BACKGROUND);      
        System.out.println("Size: " + (CHAT_MIN_SIZE.height - CardBoxUI.getGlobalChatIcon().getIconHeight())/2);
        // Setup he chat panel, use a tabbed pane
        CardBoxTabbedPanel chatPane = new CardBoxTabbedPanel(JTabbedPane.LEFT);
        chatPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);        
        _friendChat = new FriendChatPanel(ctx, ctx.getChatDirector(), true);
        _globalChat = new StrangerChatPanel(ctx, ctx.getFriendChatDirector(), true);
        chatPane.addTab("", CardBoxUI.getGlobalChatIcon(), _globalChat, "Global Chat");
        chatPane.addTab("", CardBoxUI.getFriendChatIcon(), _friendChat, "Friend Only Chat");
        chatPane.setBackground(_globalChat.getBackground());
        
        // Classes to handle match making
        _mdtr = new MatchMakerDirector(ctx);
		_strangerMatchMaker = new MatchMaker(ctx, strangerConfig, _mdtr, 
				new StrangerTableFilter());       
		       
		// Stranger Play button
		_strangerPlay = new MatchMakingButton(_msgs.xlate("m.stranger"), _strangerMatchMaker);
		_strangerPlay.setFont(CardBoxUI.AppFontMedium);
		_strangerPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
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
		
		log.info("Making the new Contextual Button");
		_friendPlay = new CardBoxContextualButton(_ctx);
		_friendPlay.setFont(CardBoxUI.AppFontMedium);
		_friendPlay.setContext(_friendList.getContext());
		
        // SoloPlay   	
        _soloPlay = new ComputerOpponentView(_ctx, aiConfig);
        _soloPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// disable the other buttons so they don't get pressed
				_friendPlay.setEnabled(false);
				_strangerPlay.setEnabled(false);
				_soloPlay.setEnabled(false);
				
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
        
        log.info("Continuing lobby panel config");
        
        // Label to keep track of online players
        _onlinePlayerLabel = new PlayerCountPanel(_ctx, 0);	
        
        _invitePanel = new InvitationPanel(_ctx, _onlinePlayerLabel);
        _invitePanel.setBackground(CardBoxUI.ORANGE);
        _invitePanel.setMaximumSize(STATUS_MAX_SIZE);
        _invitePanel.setPreferredSize(STATUS_MAX_SIZE);
        _invitePanel.setMinimumSize(STATUS_MIN_SIZE);
        
        _invitePanel.setAcceptListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
				// disable the other buttons so they don't get pressed
        		_soloPlay.setEnabled(false);
				_strangerPlay.setEnabled(false);
				
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
							_invitePanel.accept();
							return true;
						}
					});	
					return;
				default:
					break;
				}
				
				_invitePanel.accept();
			}
        });
		
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
        
        chatPane.setMaximumSize(CHAT_MAX_SIZE);
        chatPane.setPreferredSize(CHAT_MAX_SIZE);
        chatPane.setMinimumSize(CHAT_MIN_SIZE);          
        
        _friendList.setMaximumSize(LIST_MAX_SIZE);
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
	    		    				  .addComponent(_invitePanel)
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
	    		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, true)
	    		    		  .addGroup(layout.createSequentialGroup()
	    		    				  .addComponent(_invitePanel)
	    		    				  .addComponent(_friendPlay)
	    		    				  .addComponent(_strangerPlay)
	    		    				  .addComponent(_soloPlay))
		    				  .addComponent(_friendList))
					  .addComponent(chatPane)
	    		);
        
        log.info("Finished lobby panel config");
	}
	
	public void init(PlaceObject place) 
	{		
		willEnterPlace(place);
		_friendList.willEnterPlace(place);
		_friendChat.willEnterPlace(place);
		_globalChat.willEnterPlace(place);
		_onlinePlayerLabel.willEnterPlace(place);
	}
	
	// Entering and leaving the Hextilla panel
	public void willEnterPlace(PlaceObject place) {
		_lobj = (LobbyObject)place;
		_soloPlay.setPlace(place);
		_strangerMatchMaker.setPlace(place);
		_mdtr.setPlace(place);
	}

	public void didLeavePlace(PlaceObject place) {
		_ctx.getInvitationDirector().clearInvitations();
		_soloPlay.leavePlace(place);
		_strangerMatchMaker.leavePlace(place);
		_mdtr.leavePlace(place);
	}		
	
	/** ChatPanel objects **/
	protected ChatPanel _friendChat;
	protected ChatPanel _globalChat;
	
    /** Giver of life and services. */
    protected CardBoxContext _ctx;
    
    /** Our translation messages. */
    protected MessageBundle _msgs;
    
    /** Our lobby distributed object. */
    protected LobbyObject _lobj;
    
    /** The Almighty Friend List */
    protected FriendListPanel _friendList;
    
    // Counts the number of online players
    protected PlayerCountPanel _onlinePlayerLabel;
    
    //protected InvitationPanel _invitePanel;
    protected InvitationPanel _invitePanel;
        
	// Matchmaking classes
	public static MatchMaker _strangerMatchMaker; 
	protected MatchMakerDirector _mdtr;
    
    // Buttons
    protected ComputerOpponentView _soloPlay;
    protected CardBoxContextualButton _friendPlay;
    protected MatchMakingButton _strangerPlay;   
	
	// Whether we're running in development mode (i.e. gameId = -1)
	protected boolean _devmode = false;

	// PlayerCount label sizes (and the whitespace beside it)
	protected static Dimension STATUS_MAX_SIZE = new Dimension(400, 50);
    protected static Dimension STATUS_MIN_SIZE = new Dimension(200, 25);   
	
    // Button sizes
    protected static Dimension BUTTON_MAX_SIZE = new Dimension(400, 100);
    protected static Dimension BUTTON_MIN_SIZE = new Dimension(200, 35);
    
	// Max/Min Sizes for the Friend List
    protected static Dimension LIST_MAX_SIZE = new Dimension(400, 400);
    protected static Dimension LIST_MIN_SIZE = new Dimension(200, 75);	
    
	// Max/Min Sizes for the Chat
    protected static Dimension CHAT_MAX_SIZE = new Dimension(1920, 200);
    protected static Dimension CHAT_MIN_SIZE = new Dimension(400, 100);	    
}
