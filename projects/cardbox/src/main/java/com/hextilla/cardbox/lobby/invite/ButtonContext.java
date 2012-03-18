package com.hextilla.cardbox.lobby.invite;

import java.awt.event.ActionListener;

import com.hextilla.cardbox.lobby.invite.ButtonContextObserver;

public interface ButtonContext extends ActionListener
{
	/** Return the name of this context */
	public String getName();
	
	/** Return the button label text associated with this context */
	public String getText();
	
	/** Returns whether the button should be enabled according to the given context */
	public boolean getEnabled();
	
	/** Provide a means to notify our button if the context changes */
	public void addObserver(ButtonContextObserver ob);
	
	// TODO: Add some constraints to the context-updating infrastructure
	/** Update this context using a plain object (implementation-specific) */
	public void updateContext(Object ob);
	
	/** Do some cleanup when the context is being discarded */
	public void clear();
}
