package com.hextilla.cardbox.lobby.friendlist;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import static com.hextilla.cardbox.Log.log;

public class FriendList extends JList
	implements ListSelectionListener
{
	public FriendList (FriendListModel model)
	{
		super(model);
		_friendModel = model;
		_selectModel = getSelectionModel();
		_selectModel.addListSelectionListener(this);
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
        }
		//log.info(message);
	}
	
	protected FriendListModel _friendModel;
	protected ListSelectionModel _selectModel;
}
