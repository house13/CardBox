package com.hextilla.cardbox.client.chat;

import javax.swing.text.Style;

import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.facebook.client.FriendSet;
import com.hextilla.cardbox.util.CardBoxContext;
import com.samskivert.util.StringUtil;
import com.threerings.crowd.chat.data.ChatCodes;
import com.threerings.crowd.chat.data.ChatMessage;
import com.threerings.crowd.chat.data.UserMessage;
import com.threerings.crowd.data.OccupantInfo;
import com.threerings.util.MessageBundle;

public class FriendChatPanel extends ChatPanel {

	public FriendChatPanel(CardBoxContext ctx, FriendSet friends)
	{
		this(ctx, false, friends);
	}
	
	public FriendChatPanel(CardBoxContext ctx, boolean horizontal, FriendSet friends) 
	{
		super(ctx, horizontal);

		// FriendSet
		_friendSet = friends;
		
		// All friends here!
        _nameTransformer = new FriendNameTransformer();
	}
	
	@Override public boolean displayMessage (ChatMessage message, boolean alreadyShown)
	{
        if (message instanceof UserMessage) {
        	//TODO: filter messages from non-friends
            UserMessage msg = (UserMessage) message;
            if (msg.mode == CardBoxChatCodes.FRIEND_MODE) {
            	// Filter messages send from the friend chat to those that match the users friends
            	return filter(msg, alreadyShown);
            } else if (msg.mode == CardBoxChatCodes.DEFAULT_MODE) {
            	// Don't show regular messages
            	return false;
            }     	
        }        
		return super.displayMessage(message, alreadyShown);		
	}
	
	private boolean filter(UserMessage message, boolean alreadyShown) 
	{
		// If the speaker is in the friend list then display it normally
		if (_friendSet != null && _friendSet.isFriend(((CardBoxName)message.speaker).getFacebookId()))
		{
			return super.displayMessage(message, alreadyShown);
		}
		return false;
	}

	// Send text in FRIEND_MODE
	@Override protected void sendText ()
    {
        String text = _entry.getText().trim();
        if (!StringUtil.isBlank(text)) {
        	// TODO: this doesn't properly handle slash commands and such, we need to override the
        	// chat director and redo the requestChat function
            _chatdtr.requestSpeak(_room.speakService, text, CardBoxChatCodes.FRIEND_MODE);
            _entry.setText("");
        }
    }	
	
    // documentation inherited
	@Override public void occupantEntered (OccupantInfo info)
    {
		// Only display information about friends
		if (_friendSet != null && _friendSet.isFriend(((CardBoxName)info.username).getFacebookId()))
		{
	        displayOccupantMessage("*** " + _nameTransformer.transform((CardBoxName)info.username) + " entered.");			
		}
    }

    // documentation inherited
	@Override public void occupantLeft (OccupantInfo info)
    {
		// Only display information about friends		
		if (_friendSet != null && _friendSet.isFriend(((CardBoxName)info.username).getFacebookId()))
		{
			displayOccupantMessage("*** " + _nameTransformer.transform((CardBoxName)info.username) + " left.");
		}
    }	
	
	// The set of friends to compare messages against
	protected FriendSet _friendSet;

}
