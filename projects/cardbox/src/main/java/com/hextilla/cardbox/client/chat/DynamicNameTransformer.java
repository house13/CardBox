package com.hextilla.cardbox.client.chat;
import com.hextilla.cardbox.facebook.CardBoxName;

// Used by chat panel to transform the name, the dynamic name transformer shows
// friendly name for friends and stranger otherwise.
public class DynamicNameTransformer implements NameTransformer {
	
	public String transform(CardBoxName username) {
		// TODO Auto-generated method stub
		return username.toString();
	}
}
