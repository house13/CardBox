package com.hextilla.cardbox.client.chat;
import com.hextilla.cardbox.facebook.CardBoxName;

public class FriendNameTransformer implements NameTransformer {
		
	public String transform(CardBoxName username) {
		// TODO Auto-generated method stub
		return username.getFriendlyName().toString();
	}
}
