package com.hextilla.cardbox.lobby.friendlist;

import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.hextilla.cardbox.lobby.invite.ButtonContext;
import com.hextilla.cardbox.util.CardBoxContext;

import static com.hextilla.cardbox.Log.log;

public class FriendList extends JList
	implements ListSelectionListener
{
	public FriendList (CardBoxContext ctx, FriendListModel model)
	{
		super(model);
		_ctx = ctx;
		_friendModel = model;
		_selectModel = getSelectionModel();
		_selectModel.addListSelectionListener(this);
	}
	
	public ButtonContext getContext()
	{
		FriendSelectionContext fsc = new FriendSelectionContext(_ctx);
		_contexts.add(fsc);
		return fsc;
	}

	/**
	 * Assume our friend list only supports single selection.
	 */
	@Override
	public void valueChanged(ListSelectionEvent e)
	{
        boolean isAdjusting = e.getValueIsAdjusting(); 
        
        if (_selectModel.isSelectionEmpty()) {
        	// no-op
        } else if (!isAdjusting) {
            // Since we only support single selection, this will get us the info we need
            int index = _selectModel.getMinSelectionIndex();
            FriendEntry fe = (FriendEntry)_friendModel.getElementAt(index);
            log.info("You've selected your good friend " + fe.getName().getFriendlyName());
            updateContexts(fe);
        }
		//log.info(message);
	}
	
	public void updateContexts(FriendEntry fe)
	{
		for (ButtonContext context : _contexts)
		{
			context.updateContext(fe);
		}
	}
	
	public void clear()
	{
		_contexts.clear();
	}
	
	protected CardBoxContext _ctx;
	
	protected Vector<ButtonContext> _contexts = new Vector<ButtonContext>();
	
	protected FriendListModel _friendModel;
	protected ListSelectionModel _selectModel;
}
