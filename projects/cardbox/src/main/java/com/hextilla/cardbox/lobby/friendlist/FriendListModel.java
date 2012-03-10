package com.hextilla.cardbox.lobby.friendlist;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

public class FriendListModel extends AbstractListModel
{
	public FriendListModel()
	{
		_model = new TreeSet<FriendEntry>();
	}
	
	public void addElement(FriendEntry friend)
	{
		if (_model.add(friend))
		{
			fireContentsChanged(this, 0, getSize());
		}
	}
	
	public void addAll(FriendEntry friends[])
	{
		Collection<FriendEntry> c = Arrays.asList(friends);
		_model.addAll(c);
		fireContentsChanged(this, 0, getSize());
	}
	
	public boolean removeElement(FriendEntry friend)
	{
		boolean removed = _model.remove(friend);
		if (removed)
		{
			fireContentsChanged(this, 0, getSize());
		}
		return removed;
	}
	
	public boolean contains(FriendEntry friend)
	{
		return _model.contains(friend);
	}
	
	public void clear()
	{
		_model.clear();
		fireContentsChanged(this, 0, getSize());
	}
	
	public Iterator<FriendEntry> iterator()
	{
		return _model.iterator();
	}
	
	@Override
	public Object getElementAt(int index)
	{
		return _model.toArray()[index];
	}

	@Override
	public int getSize() 
	{
		return _model.size();
	}

	protected SortedSet<FriendEntry> _model;
}
