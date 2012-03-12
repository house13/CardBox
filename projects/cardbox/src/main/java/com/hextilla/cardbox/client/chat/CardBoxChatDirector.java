package com.hextilla.cardbox.client.chat;

import java.util.Map;
import java.util.StringTokenizer;

import com.google.common.collect.Sets;
import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.facebook.client.SocialDirector;
import com.hextilla.cardbox.facebook.client.SocialDirector.FriendIterator;
import com.hextilla.cardbox.util.CardBoxContext;
import com.samskivert.util.ResultListener;
import com.samskivert.util.StringUtil;
import com.threerings.crowd.chat.client.ChatDirector;
import com.threerings.crowd.chat.client.ChatService;
import com.threerings.crowd.chat.client.SpeakService;
import com.threerings.crowd.chat.client.ChatDirector.CommandHandler;
import com.threerings.crowd.chat.data.ChatCodes;
import com.threerings.crowd.chat.data.TellFeedbackMessage;
import com.threerings.crowd.chat.data.UserMessage;
import com.threerings.crowd.util.CrowdContext;
import com.threerings.util.MessageBundle;
import com.threerings.util.Name;
import com.threerings.util.TimeUtil;

public class CardBoxChatDirector extends ChatDirector {

	public CardBoxChatDirector(CardBoxContext ctx, String bundle) {
		super(ctx, bundle);
	}

	//Override to disable / commands till we can get it to work properly
	@Override
    public String requestChat (SpeakService speakSvc, String text, boolean record)
    {
        if (text.startsWith("/")) {
            // split the text up into a command and arguments
            String command = text.substring(1).toLowerCase();
            String[] hist = new String[1];
            String args = "";
            int sidx = text.indexOf(" ");
            if (sidx != -1) {
                command = text.substring(1, sidx).toLowerCase();
                args = text.substring(sidx + 1).trim();
            }

            Map<String, CommandHandler> possibleCommands = getCommandHandlers(command);
            switch (possibleCommands.size()) {
            case 0:
                StringTokenizer tok = new StringTokenizer(text);
                return MessageBundle.tcompose("m.unknown_command", tok.nextToken());

            case 1:
                Map.Entry<String, CommandHandler> entry =
                    possibleCommands.entrySet().iterator().next();
                String cmdName = entry.getKey();
                CommandHandler cmd = entry.getValue();

                // Only allow help and clear for now.
                if(!(cmdName.equals("help") || cmdName.equals("clear")))
                {
                    StringTokenizer tok1 = new StringTokenizer(text);
                    return MessageBundle.tcompose("m.unknown_command", tok1.nextToken());
                }                	
                	
                String result = cmd.handleCommand(speakSvc, cmdName, args, hist);
                if (!result.equals(ChatCodes.SUCCESS)) {
                    return result;
                }

                if (record) {
                    // get the final history-ready command string
                    hist[0] = "/" + ((hist[0] == null) ? command : hist[0]);

                    // remove from history if it was present and add it to the end
                    addToHistory(hist[0]);
                }

                return result;

            default:
                StringBuilder buf = new StringBuilder();
                for (String pcmd : Sets.newTreeSet(possibleCommands.keySet())) {
                    buf.append(" /").append(pcmd);
                }
                return MessageBundle.tcompose("m.unspecific_command", buf.toString());
            }
        }

        // if not a command then just speak
        String message = text.trim();
        if (StringUtil.isBlank(message)) {
            // report silent failure for now
            return ChatCodes.SUCCESS;
        }

        return deliverChat(speakSvc, message, ChatCodes.DEFAULT_MODE);
    }
    
	// Method for handling friend messaging
    /**
     * Requests that a tell message be delivered to all online friends
     *
     * @param msg the contents of the friend message.
     */
    public void requestFriendChat (Name speaker, String text)
    {
        String msg = text.trim();
        
    	// Do nothing if msg is empty        
        if (StringUtil.isBlank(msg)) {
            return;
        }
    	
        // make sure they can say what they want to say
        final String message = filter(msg, null, true);
        
        // If we can't tell to this friend (because they are ignoring
        // us or something via a filter, then skip them
        if (message == null) {
            return;
        }   
        
        // Send a message to all the online friends
        FriendIterator friends = ((CardBoxContext)_ctx).getSocialDirector().getOnlineFriendIterator();
        while (friends.hasNext()){
        	CardBoxName friend = friends.next();        	         

            // Send a tell to that user
            _cservice.tell(friend, message, null);            
        }
        
        // Echo the output
        dispatchMessage(UserMessage.create(speaker, message),
        		ChatCodes.USER_CHAT_TYPE);
    }	
}
