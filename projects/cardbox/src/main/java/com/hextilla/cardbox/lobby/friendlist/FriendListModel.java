package com.hextilla.cardbox.lobby.friendlist;

import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

import com.hextilla.cardbox.facebook.CardBoxName;

public class FriendListModel extends AbstractListModel
{
	public FriendListModel()
	{
		_model = new TreeSet<FriendEntry>();
		_directory = new Hashtable<CardBoxName, FriendEntry>();
	}
	
	public synchronized void addElement(FriendEntry friend)
	{
		if (_model.add(friend))
		{
			_directory.put(friend.getName(), friend);
			fireContentsChanged(this, 0, getSize());
		}
	}
	
	public synchronized void addAll(FriendEntry friends[])
	{
		Collection<FriendEntry> c = Arrays.asList(friends);
		_model.addAll(c);
		for (FriendEntry friend : c)
		{
			_directory.put(friend.getName(), friend);
		}
		fireContentsChanged(this, 0, getSize());
	}
	
	public synchronized boolean removeElement(FriendEntry friend)
	{
		boolean removed = _model.remove(friend);
		if (removed)
		{
			_directory.remove(friend.getName());
			fireContentsChanged(this, 0, getSize());
		}
		return removed;
	}
	
	public synchronized boolean updateElement(FriendEntry update)
	{
		CardBoxName name = update.getName();
		boolean exists = _directory.containsKey(name);
		if (exists)
		{
			FriendEntry entry = getElementByName(name);
			if (entry.update(update))
			{
				fireContentsChanged(this, 0, getSize());
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(FriendEntry friend)
	{
		return _model.contains(friend);
	}
	
	public void clear()
	{
		_model.clear();
		_directory.clear();
		fireContentsChanged(this, 0, getSize());
	}
	
	public Iterator<FriendEntry> iterator()
	{
		return _model.iterator();
	}
	
	public FriendEntry getElementByName(CardBoxName name)
	{
		return _directory.get(name);
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
	protected Hashtable<CardBoxName, FriendEntry> _directory;
}
