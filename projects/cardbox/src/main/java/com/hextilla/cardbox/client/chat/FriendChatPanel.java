package com.hextilla.cardbox.client.chat;

import javax.swing.text.Style;

import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.facebook.client.FriendSet;
import com.hextilla.cardbox.facebook.client.SocialDirector;
import com.hextilla.cardbox.facebook.client.SocialDirector.FriendIterator;
import com.hextilla.cardbox.util.CardBoxContext;
import com.samskivert.util.StringUtil;
import com.threerings.crowd.chat.client.ChatDirector;
import com.threerings.crowd.chat.data.ChatCodes;
import com.threerings.crowd.chat.data.ChatMessage;
import com.threerings.crowd.chat.data.UserMessage;
import com.threerings.crowd.data.OccupantInfo;
import com.threerings.util.MessageBundle;

public class FriendChatPanel extends ChatPanel {

    /** The message bundle identifier for chat translations. */
    public static final String CHAT_MSGS = "friend.chat";
    
	public FriendChatPanel(CardBoxContext ctx, ChatDirector chatdtr)
    {
        this(ctx, chatdtr, false);        
    }
	
	public FriendChatPanel(CardBoxContext ctx,  ChatDirector chatdtr, boolean horizontal) 
	{
		super(ctx, chatdtr, horizontal);

		// Social director
		_sdtr = ctx.getSocialDirector();
		
		// All friends here!
        _nameTransformer = new FriendNameTransformer();
	}
	
	@Override public boolean displayMessage (ChatMessage message, boolean alreadyShown)
	{
		// If it is not a friend message ignore it, its meant for regular chat
		if (message.localtype != ChatCodes.USER_CHAT_TYPE)
		{
			return false;
		}
		
		return filter((UserMessage)message, alreadyShown);		
	}
	
	// Might not need this depending on how send works
	private boolean filter(UserMessage message, boolean alreadyShown) 
	{	
		// If the speaker is in the friend list then display it normally
		if ((message.speaker.equals(_ctx.getClient().getClientObject().username)) || 
				(_sdtr.isOnlineFriend((CardBoxName)message.speaker)))
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
        	// TODO: this doesn't properly handle slash commands and such
        	        	
            // Just tell all the online friends
        	((CardBoxChatDirector)_chatdtr).requestFriendChat(_ctx.getUsername(), text);
            
            _entry.setText("");
        }
    }	
	
    // documentation inherited
	@Override public void occupantEntered (OccupantInfo info)
    {
		// Only display information about friends
		if (_sdtr.isOnlineFriend((CardBoxName)info.username))
		{
	        displayOccupantMessage("*** " + _nameTransformer.transform((CardBoxName)info.username) + " entered.");			
		}
    }

    // documentation inherited
	@Override public void occupantLeft (OccupantInfo info)
    {
		// Only display information about friends		
		if (_sdtr.isOnlineFriend((CardBoxName)info.username))
		{
			displayOccupantMessage("*** " + _nameTransformer.transform((CardBoxName)info.username) + " left.");
		}
    }	
	
	// The set of friends to compare messages against
	protected SocialDirector _sdtr;

}
