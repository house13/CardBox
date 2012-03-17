package com.hextilla.cardbox.lobby.friendlist;

import java.awt.Dimension;
import java.util.Arrays;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import com.hextilla.cardbox.client.CardBoxUI;
import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.facebook.client.FriendSet;
import com.hextilla.cardbox.facebook.client.SocialDirector;
import com.hextilla.cardbox.facebook.client.SocialDirector.FriendIterator;
import com.hextilla.cardbox.lobby.data.LobbyObject;
import com.hextilla.cardbox.util.CardBoxContext;

import com.threerings.crowd.client.OccupantObserver;
import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.OccupantInfo;
import com.threerings.crowd.data.PlaceObject;
import com.threerings.parlor.client.SeatednessObserver;
import com.threerings.parlor.client.TableDirector;
import com.threerings.parlor.client.TableObserver;
import com.threerings.parlor.data.Table;

import static com.hextilla.cardbox.lobby.Log.log;

// Class to show the list of Facebook friends
public class FriendListPanel extends JPanel 
	implements PlaceView, OccupantObserver, SocialDirector.FriendTracker
			   
{
	// TODO: reference to this "friend" object taken in constructor
	public FriendListPanel (CardBoxContext ctx, CardBoxGameConfig config)
	{
        _ctx = ctx;
        
        _devmode = !_ctx.isFacebookEnabled();
        
        _tabler = new FriendTableTracker(_ctx);
        _tdtr = new TableDirector(_ctx, LobbyObject.TABLE_SET, _tabler);
        
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
		_friendList = new FriendList(_listModel);
		_friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
		
		log.info("Friends List Panel has been created!!");
		_friends = _ctx.getSocialDirector().getFriends();
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
		log.info("Adding friend " + name);
		_listModel.addElement(new FriendEntry(_ctx, name, _friends.getPic(name)));		
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
		_listModel.removeElement(new FriendEntry(_ctx, name));
	}

	@Override
	public boolean isOnlineFriend(CardBoxName friend)
	{
		return _listModel.contains(friend);
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
		_listModel.updateElement(new FriendEntry(_ctx, name, _friends.getPic(name)));
	}
	
	public void updateStatus(OccupantInfo info, byte status)
	{
		if (info.username instanceof CardBoxName)
		{
			CardBoxName name = (CardBoxName) info.username;
			if (isFriend(name.getFacebookId()))
				updateStatus(name, status);
		}
	}
	
	public void updateStatus(CardBoxName friend, byte status)
	{
		String message = friend.getFriendlyName().toString() + " now has status ";
		switch(status)
		{
		case OnlineStatus.ONLINE:
			message = message + "ONLINE";
			break;
		case OnlineStatus.WAITING:
			message = message + "WAITING";
			break;
		case OnlineStatus.INGAME:
			message = message + "INGAME";
			break;
		case OnlineStatus.LEAVING:
			message = message + "LEAVING";
			break;
		}
		
		if (_listModel.updateElement(new FriendEntry(_ctx, friend, _friends.getPic(friend), status)))
			log.info(message);
	}
	
	@Override
	public void occupantEntered(OccupantInfo info)
	{
		CardBoxName user = (CardBoxName)info.username;
		// Base Case: Friend enters the lobby
		if (!_listModel.contains(user))
		{
			addFriend(info);
		} else {
			updateStatus(info, OnlineStatus.LEAVING);
		}
		log.info("Occupant Entered", "user", user, "status", occupantStatus(info));
	}

	@Override
	public void occupantLeft(OccupantInfo info)
	{
		CardBoxName user = (CardBoxName)info.username;
		// Ensure it's a friend who left
		if (_listModel.contains(user))
		{
			// Only remove a friend from the list if they left from the lobby
			// without any running or pending games.
			switch(_tabler.getUserStatus(user))
			{
			case OnlineStatus.ONLINE:
			case OnlineStatus.LEAVING:
				removeFriend(info);
				break;
			}
		}
		log.info("Occupant Left", "user", user, "status", occupantStatus(info));
	}

	@Override
	public void occupantUpdated(OccupantInfo oldinfo, OccupantInfo newinfo)
	{
		CardBoxName olduser = (CardBoxName)oldinfo.username;
		CardBoxName newuser = (CardBoxName)newinfo.username;
		log.info("Occupant Updated", "olduser", olduser, "oldstatus", occupantStatus(oldinfo),
									 "newuser", newuser, "newstatus", occupantStatus(newinfo));
	}

	@Override
	public void willEnterPlace(PlaceObject plobj)
	{
		// add all of the occupants of the place to our list
        for (OccupantInfo info : plobj.occupantInfo) {
        	addFriend(info);
        }
        _ctx.getOccupantDirector().addOccupantObserver(this);
        // TODO: Add friends currently in-game
        _tdtr.setTableObject(plobj);
	}

	@Override
	public void didLeavePlace(PlaceObject plobj)
	{
		// clear out our occupant entries
		_ctx.getOccupantDirector().removeOccupantObserver(this);
        _listModel.clear();
        _tdtr.clearTableObject();
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
	
	protected static String occupantStatus(OccupantInfo info)
	{
		String status = "Unknown";
		switch(info.status)
		{
		case OccupantInfo.ACTIVE:
			status = "Active";
			break;
		case OccupantInfo.IDLE:
			status = "Idle";
			break;
		case OccupantInfo.DISCONNECTED:
			status = "Disconnected";
			break;
		}
		
		return status;
	}
	
	/** The whole world is your friend in dev mode! */
	protected boolean _devmode = false;
	
	/** Giver of life and services. */
	protected CardBoxContext _ctx;
	
	/** The sorted model that represents our list. */
	protected FriendListModel _listModel;
	
	/** JComponent representation of the friend list */
	protected FriendList _friendList;
	
	/** Set of raw friend data from Facebook */
	protected FriendSet _friends;
	
	protected TableDirector _tdtr;
	
	protected FriendTableTracker _tabler;
	
	// Max/Min Sizes for the Title
	protected static Dimension TITLE_MAX_SIZE = new Dimension(390, 30);
    protected static Dimension TITLE_MIN_SIZE = new Dimension(200, 30);
    // Max/Min Sizes for the Friend List
    protected static Dimension LIST_MAX_SIZE = new Dimension(390, 370);
    protected static Dimension LIST_MIN_SIZE = new Dimension(200, 50);
}
