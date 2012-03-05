package com.hextilla.cardbox.client.chat;
import com.hextilla.cardbox.facebook.CardBoxName;

public class StrangerNameTransformer implements NameTransformer {
		
	public String transform(CardBoxName username) {
		return username.getStrangerName().toString();
	}
}
