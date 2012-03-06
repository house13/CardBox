package com.hextilla.cardbox.lobby.HextillaLobbyPanel;

import static com.hextilla.cardbox.lobby.Log.log;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;

import com.hextilla.cardbox.client.chat.ChatPanel;
import com.hextilla.cardbox.client.chat.DynamicNameTransformer;
import com.hextilla.cardbox.client.chat.FriendChatPanel;
import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.hextilla.cardbox.data.GameDefinition;
import com.hextilla.cardbox.lobby.data.LobbyConfig;
import com.hextilla.cardbox.lobby.data.LobbyObject;
import com.hextilla.cardbox.lobby.friendlist.FriendList;
import com.hextilla.cardbox.lobby.matchmaking.ComputerOpponentView;
import com.hextilla.cardbox.lobby.matchmaking.MatchMakingPanel;
import com.hextilla.cardbox.util.CardBoxContext;
import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.PlaceObject;

public class HextillaLobbyPanel extends JPanel implements PlaceView {
	JButton _matchButton;
	MatchMakingPanel _matchMaker;
	
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
        
    	setLayout(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.BOTH;         
		
        // Add the buttons, give them their own panel, love me some GridBagLayout
        JPanel leftPane = new JPanel(new GridBagLayout());
        GridBagConstraints buttonConstraints = new GridBagConstraints();  
        buttonConstraints.weightx = 1;
        buttonConstraints.weighty = 1;             
        buttonConstraints.gridheight = 1;
        buttonConstraints.gridwidth	= GridBagConstraints.REMAINDER;
        buttonConstraints.fill = GridBagConstraints.BOTH;
        buttonConstraints.insets = new Insets(5, 5, 5, 5);
        
        // Options
        JButton optsButton = new JButton("Options");      
        leftPane.add(optsButton, buttonConstraints);     
        
        // Solo     
        _cov = new ComputerOpponentView(_ctx, aiConfig);     
        leftPane.add(_cov, buttonConstraints);
        
        // Add matchmaking info box here, takes up more space then other buttons
        buttonConstraints.gridheight = 2;
        buttonConstraints.weightx = 2;
        buttonConstraints.weighty = 2;            
        _matchMaker = new MatchMakingPanel(ctx, strangerConfig);      
        leftPane.add(_matchMaker, buttonConstraints);   
        
        // Add the button panel
        c.weightx = 1.0;
        c.weighty = 5.0;
        c.gridwidth = 1; 
        c.gridheight = 5;       
        add(leftPane, c);
        
        // Add the friendPanel (same size as button panel)
        FriendList friendPanel = new FriendList(ctx, friendlyConfig);
        friendPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        c.gridwidth = GridBagConstraints.REMAINDER;        
        add(friendPanel, c);  
        
        // Add the chat panel, use a tabbed pane
        c.weightx = 2.0;
        c.weighty = 5.0;
        c.gridwidth = 2;
        c.gridheight = 5;
        JTabbedPane tabbedPane = new JTabbedPane();
        _friendChat = new FriendChatPanel(ctx, true, friendPanel.getFriends());
        _friendChat.setPreferredSize(new Dimension(200,100));
        _globalChat = new ChatPanel(ctx, true);      
        _globalChat.setPreferredSize(new Dimension(200,100));        
        add(tabbedPane, c);
        tabbedPane.addTab("All", null, _globalChat, "Global Chat");
        tabbedPane.addTab("Friends", null, _friendChat, "Friend Only Chat");
	}
	
	public void init(PlaceObject place) {
		willEnterPlace(place);
		_friendChat.willEnterPlace(place);
		_globalChat.willEnterPlace(place);
	}
	
	// Entering and leaving the Hextilla panel
	public void willEnterPlace(PlaceObject place) {
		_lobj = (LobbyObject)place;
		_cov.setPlace(place);
		MatchMakingPanel.matchMaker.setPlace(place);
	}

	public void didLeavePlace(PlaceObject place) {
		_cov.leavePlace(place);
		MatchMakingPanel.matchMaker.leavePlace(place);
	}		
	
	/** ChatPanel objects **/
	protected ChatPanel _friendChat;
	protected ChatPanel _globalChat;
	
    /** Giver of life and services. */
    protected CardBoxContext _ctx;
    
    /** Our lobby distributed object. */
    protected LobbyObject _lobj;    
    
    protected ComputerOpponentView _cov;
}
