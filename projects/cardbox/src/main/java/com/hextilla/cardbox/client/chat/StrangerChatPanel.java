package com.hextilla.cardbox.client.chat;

import com.hextilla.cardbox.util.CardBoxContext;
import com.threerings.crowd.chat.client.ChatDirector;
import com.threerings.crowd.chat.data.ChatCodes;
import com.threerings.crowd.chat.data.ChatMessage;
import com.threerings.crowd.chat.data.UserMessage;

public class StrangerChatPanel extends ChatPanel {

	public StrangerChatPanel(CardBoxContext ctx, ChatDirector chatdtr)
    {
        this(ctx, chatdtr, false);        
    }
	
	public StrangerChatPanel(CardBoxContext ctx, ChatDirector chatdtr, boolean horizontal) {
		super(ctx, chatdtr, horizontal);
	}
	
	// Don't show stuff from the friend chat
	@Override 
	public boolean displayMessage (ChatMessage message, boolean alreadyShown)
	{
		// If it is not a friend message ignore it, its meant for regular chat
		if (message.localtype == ChatCodes.USER_CHAT_TYPE)
		{
			return false;
		}
		
		return super.displayMessage(message, alreadyShown);		
	}

}
