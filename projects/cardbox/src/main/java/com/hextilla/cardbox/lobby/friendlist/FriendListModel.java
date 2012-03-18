package com.hextilla.cardbox.lobby.friendlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.AbstractListModel;

import com.hextilla.cardbox.facebook.CardBoxName;

import static com.hextilla.cardbox.Log.log;

public class FriendListModel extends AbstractListModel
{
	public FriendListModel()
	{
		_model = new ArrayList<FriendEntry>();
		_directory = new Hashtable<CardBoxName, FriendEntry>();
	}
	
	public synchronized void addElement(FriendEntry friend)
	{
		// Don't add duplicate entries
		if (!contains(friend))
		{
			_model.add(friend);
			_directory.put(friend.getName(), friend);
			Collections.sort(_model);
			fireContentsChanged(this, 0, getSize());
		} else {
			// If you try to add a duplicate, we'll at least 
			// give you the benefit of the doubt.
			updateElement(friend);
		}
	}
	
	public synchronized void addAll(FriendEntry friends[])
	{
		// Might add duplicate entries, only use this if you're sure.
		Collection<FriendEntry> c = Arrays.asList(friends);
		_model.addAll(c);
		for (FriendEntry friend : c)
		{
			_directory.put(friend.getName(), friend);
		}
		Collections.sort(_model);
		fireContentsChanged(this, 0, getSize());
	}
	
	public synchronized boolean removeElement(int index)
	{
		FriendEntry fe = _model.remove(index);
		if (fe != null)
		{
			_directory.remove(fe.getName());
			fireContentsChanged(this, index, getSize());
		}
		return (fe != null);
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
		if (contains(name))
		{
			FriendEntry entry = getElementByName(name);
			if (entry.update(update))
			{
				int index = Collections.binarySearch(_model, entry);
				fireContentsChanged(this, index, index);
				return true;
			}
		} else {
			addElement(update);
		}
		return false;
	}
	
	public boolean contains(CardBoxName friend)
	{
		return _directory.containsKey(friend);
	}
	
	public boolean contains(FriendEntry friend)
	{
		return _directory.containsKey(friend.getName());
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
		return _model.get(index);
	}

	@Override
	public int getSize() 
	{
		return _model.size();
	}

	protected ArrayList<FriendEntry> _model;
	protected Hashtable<CardBoxName, FriendEntry> _directory;
}
