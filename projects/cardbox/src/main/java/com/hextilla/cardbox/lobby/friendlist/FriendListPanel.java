package com.hextilla.cardbox.lobby.friendlist;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
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
import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.facebook.client.FriendSet;
import com.hextilla.cardbox.facebook.client.SocialDirector;
import com.hextilla.cardbox.facebook.client.SocialDirector.FriendIterator;
import com.hextilla.cardbox.util.CardBoxContext;
import com.threerings.crowd.client.OccupantObserver;
import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.OccupantInfo;
import com.threerings.crowd.data.PlaceObject;

import static com.hextilla.cardbox.Log.log;

// Class to show the list of Facebook friends
public class FriendListPanel extends JPanel 
	implements PlaceView, OccupantObserver, SocialDirector.FriendTracker
{
	// TODO: reference to this "friend" object taken in constructor
	public FriendListPanel (CardBoxContext ctx, CardBoxGameConfig config)
	{
        _ctx = ctx;
        
        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);        
        this.setLayout(layout); 
                      
		// Title the entry
		JLabel friendTitle = new JLabel("friends", CardBoxUI.getFacebookIcon(), JLabel.CENTER);
		friendTitle.setFont(CardBoxUI.FbFontBoldLarge);
		friendTitle.setIconTextGap(10);
		
		friendTitle.setMinimumSize(TITLE_MIN_SIZE);
		friendTitle.setMaximumSize(TITLE_MAX_SIZE);
		friendTitle.setPreferredSize(TITLE_MAX_SIZE);
		
		// Setup the friend list objects and custom renderers
		_listModel  = new FriendListModel();
		_friendList = new JList(_listModel);
		_friendList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		_friendList.setLayoutOrientation(JList.VERTICAL);		
		ListCellRenderer renderer = new FriendListRenderer();
		_friendList.setCellRenderer(renderer);
		
		// Create the scroll Bar	
		JScrollPane scrollin = new JScrollPane(_friendList);
		scrollin.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		
		scrollin.setMinimumSize(LIST_MIN_SIZE);
		scrollin.setMaximumSize(LIST_MAX_SIZE);
		scrollin.setPreferredSize(LIST_MAX_SIZE);
		
		// Horizontal Grouping
        // parallel{ friendTitle, scrollin }
		layout.setHorizontalGroup(
	    		   layout.createParallelGroup(GroupLayout.Alignment.CENTER)
	    		       .addComponent(friendTitle)
	    		       .addComponent(scrollin)
	    );
		
		// Vertical Grouping
        // sequential{ friendTitle, scrollin }
		layout.setVerticalGroup(
	    		   layout.createSequentialGroup()
	    		       .addComponent(friendTitle)
	    		       .addComponent(scrollin)
		);
		
		_friends = _ctx.getSocialDirector().getFriends();
		_ctx.getOccupantDirector().addOccupantObserver(this);
		_ctx.getSocialDirector().setFriendTracker(this);
	}
	
	public void addFriend(OccupantInfo info)
	{
		if (info.username instanceof CardBoxName)
		{
			CardBoxName name = (CardBoxName) info.username;
			if (isFriend(name.getFacebookId()))
				addFriend(name);
		}
	}
	
	// Add a friend to the friend list
	public void addFriend(CardBoxName name)
	{
		_listModel.addElement(new FriendEntry(name, _friends.getPic(name)));		
	}
	
	public void removeFriend(OccupantInfo info)
	{
		if (info.username instanceof CardBoxName)
		{
			CardBoxName name = (CardBoxName) info.username;
			if (isFriend(name.getFacebookId()))
				removeFriend(name);
		}
	}
	
	// Remove a friend from the friend list
	public void removeFriend(CardBoxName name)
	{
		_listModel.removeElement(new FriendEntry(name));
	}

	@Override
	public boolean isOnlineFriend(CardBoxName friend)
	{
		return _listModel.contains(new FriendEntry(friend));
	}

	@Override
	public FriendIterator getOnlineFriendIterator()
	{
		return new FriendListIterator(_listModel.iterator());
	}
	
	@Override
	public void imageUpdated(CardBoxName name) 
	{
		log.info("A display picture has finished downloading!", "Friend", name.getFriendlyName().toString());
		_listModel.updateElement(new FriendEntry(name, _friends.getPic(name)));
		invalidate();
	}
	
	@Override
	public void occupantEntered(OccupantInfo info)
	{
		addFriend(info);
	}

	@Override
	public void occupantLeft(OccupantInfo info)
	{
		removeFriend(info);
	}

	@Override
	public void occupantUpdated(OccupantInfo oldinfo, OccupantInfo newinfo)
	{
		// no-op for now
	}

	@Override
	public void willEnterPlace(PlaceObject plobj)
	{
		// add all of the occupants of the place to our list
        for (OccupantInfo info : plobj.occupantInfo) {
        	log.info("Determining whether user ", info.username, " is a friend of ours");
        	addFriend(info);
        }
	}

	@Override
	public void didLeavePlace(PlaceObject plobj)
	{
		// clear out our occupant entries
        _listModel.clear();
	}
	
	public boolean isFriend(long fbId)
	{
		if (_devmode || _friends == null) {
			return true;
		} else if (_friends.isFriend(fbId)) {
			return true;
		} else {
			return false;
		}
	}
	
	/** The whole world is your friend in dev mode! */
	protected boolean _devmode;
	
	/** Giver of life and services. */
	protected CardBoxContext _ctx;
	
	/** The sorted model that represents our list. */
	protected FriendListModel _listModel;
	
	/** JComponent representation of the friend list */
	protected JList _friendList;
	
	/** Set of raw friend data from Facebook */
	protected FriendSet _friends;
	
	// Max/Min Sizes for the Title
	protected static Dimension TITLE_MAX_SIZE = new Dimension(400, 30);
    protected static Dimension TITLE_MIN_SIZE = new Dimension(200, 30);
    // Max/Min Sizes for the Friend List
    protected static Dimension LIST_MAX_SIZE = new Dimension(400, 300);
    protected static Dimension LIST_MIN_SIZE = new Dimension(200, 50);
}
