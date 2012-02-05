package com.hextilla.cardbox.lobby.hextillaPanel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.hextilla.cardbox.client.ChatPanel;
import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.hextilla.cardbox.lobby.data.LobbyConfig;
import com.hextilla.cardbox.lobby.friendlist.FriendList;
import com.hextilla.cardbox.util.CardBoxContext;

public class HextillaPanel extends JPanel {
	
	public HextillaPanel (CardBoxContext ctx, CardBoxGameConfig config)
	{
        _ctx = ctx;
        _config = config;
        
    	setLayout(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.BOTH;         
		
        // Add the buttons, give them their own panel, love me some GridBagLayout
        JPanel buttonPane = new JPanel(new GridLayout(4, 1, 5, 5));
        GridBagConstraints buttonConstraints = new GridBagConstraints();         
        
        // Solo     
        JButton soloButton = new JButton("Computer Opponent");    
        buttonPane.add(soloButton, buttonConstraints);
                
        // Matchmaking
        JButton matchButton = new JButton("Random Opponent");
        buttonPane.add(matchButton, buttonConstraints);       
        
        // Options
        JButton optsButton = new JButton("Options");      
        buttonPane.add(optsButton, buttonConstraints);     
        
        // Some whitespace, intentionally left blank
        JPanel whitespace = new JPanel();       
        buttonPane.add(whitespace, buttonConstraints);
        
        // Add the button panel
        c.weightx = 1.0;
        c.weighty = 10.0;
        c.gridwidth = 1; 
        c.gridheight = 10;       
        add(buttonPane, c);
        
        // Add the friendPanel (same size as button panel)
        FriendList friendPanel = new FriendList(ctx);
        friendPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        c.gridwidth = GridBagConstraints.REMAINDER;        
        add(friendPanel, c);  
        
        // Add the chat panel
        c.weightx = 3.0;
        c.weighty = 3.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = GridBagConstraints.REMAINDER;             
        add(new ChatPanel(ctx, true), c);  	
	}
	
    /** Giver of life and services. */
    protected CardBoxContext _ctx;
    
    // Game config
    protected CardBoxGameConfig _config;
}
