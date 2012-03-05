package com.hextilla.cardbox.client.chat;
import com.hextilla.cardbox.facebook.CardBoxName;

public class defaultNameTransformer implements NameTransformer {

	// This version does nothing to the name, just returns it as is
	public String transform(CardBoxName username) {
		return username.toString();
	}

}
