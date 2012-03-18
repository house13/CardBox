package com.hextilla.cardbox.swing;

import java.awt.event.ActionEvent;

import com.hextilla.cardbox.lobby.invite.ButtonContext;
import com.hextilla.cardbox.lobby.invite.ButtonContextObserver;
import com.hextilla.cardbox.util.CardBoxContext;

import static com.hextilla.cardbox.lobby.Log.log;

public class CardBoxContextualButton extends CardBoxButton
	implements ButtonContextObserver
{
	public CardBoxContextualButton (CardBoxContext ctx)
	{
		super("");
		setContext(new DefaultContext(ctx));
	}
	
	public void setContext(ButtonContext context)
	{
		clearContext();
		_context = context;
		if (_context != null) {
			addActionListener(_context);
			_context.addObserver(this);
			contextUpdated();
		}
	}
	
	// Do any cleanup necessary to disconnect the context
	public void clearContext()
	{
		if (_context != null) {
			removeActionListener(_context);
			_context.clear();
		}
		_context = null;
	}
	
	public void contextUpdated()
	{
		this.setText(_context.getText());
		this.setEnabled(_context.getEnabled());
	}
	
	protected class DefaultContext implements ButtonContext
	{
		public DefaultContext (CardBoxContext ctx)
		{
			_ctx = ctx;
			_buttonText = _ctx.xlate(BUTTON_MSGS, DEFAULT_MSG);
		}
		
		@Override
		public String getName() {
			return "DefaultContext";
		}

		@Override
		public String getText() {
			return _buttonText;
		}

		@Override
		public boolean getEnabled() {
			return false;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
		}

		@Override
		public void addObserver(ButtonContextObserver ob) {
		}
		@Override
		public void updateContext(Object ob) {
		}
		@Override
		public void clear() {
		}
		
		protected CardBoxContext _ctx;
		protected String _buttonText;
		protected static final String BUTTON_MSGS = "client.friend";
		protected static final String DEFAULT_MSG = "m.button_default";
		
	}
	
	protected ButtonContext _context;
}

