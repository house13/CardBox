package com.hextilla.cardbox.client.chat;
import com.hextilla.cardbox.facebook.CardBoxName;

// Class used to transform the username to a user friendly form
public interface NameTransformer {

	// Transforms the user name to a user friendly form
	String transform(CardBoxName username);

}
