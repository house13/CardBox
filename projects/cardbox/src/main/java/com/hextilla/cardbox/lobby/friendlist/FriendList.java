package com.hextilla.cardbox.lobby.friendlist;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import com.hextilla.cardbox.client.CardBoxUI;
import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.hextilla.cardbox.facebook.client.FriendSet;
import com.hextilla.cardbox.util.CardBoxContext;
import com.samskivert.swing.util.SwingUtil;

// Class to show the list of Facebook friends
public class FriendList extends JPanel 
	implements ActionListener
{
	// Main panel containing the list of friends
	JList _friendList;
	DefaultListModel _listModel;
	
	// TODO: reference to this "friend" object taken in constructor
	public FriendList (CardBoxContext ctx, CardBoxGameConfig config)
	{
        _ctx = ctx;
        
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(Color.BLACK);
                      
		// Title the entry
		JLabel friendTitle = new JLabel("Friends", JLabel.CENTER);
		friendTitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		friendTitle.setFont(new Font("Serif", Font.BOLD, 24));
		friendTitle.setForeground(Color.DARK_GRAY);
		friendTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		add(friendTitle);
		
		// Setup the friend list objects and custom renderers
		_listModel  = new DefaultListModel();
		_friendList = new JList(_listModel);
		_friendList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		_friendList.setLayoutOrientation(JList.VERTICAL);		
		ListCellRenderer renderer = new FriendListRenderer();
		_friendList.setCellRenderer(renderer);
		
		// Create the scroll Bar	
		JScrollPane scrollin = new JScrollPane(_friendList);
		scrollin.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		add(scrollin);	
		
		// Cheat and add some friends for testing
		for (int i = 0; i < 50; ++i)
		{
			addFriend("Friend " + i);
		}	
	}
	
	// Add a friend to the friend list
	//TODO: take reference to a friend object, pass it to constructor
	public void addFriend(String name)
	{
		_listModel.addElement(new FriendEntry(name));		
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	/** Giver of life and services. */
	protected CardBoxContext _ctx;

	// Returns the friendSet used by the friendPanel (used to initialize the friend chat)
	public FriendSet getFriends() {
		// TODO: Fill this out once friends are working properly
		return null;
	}
}
