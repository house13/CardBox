package com.hextilla.cardbox.lobby.friendlist;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.hextilla.cardbox.client.CardBoxUI;
import com.hextilla.cardbox.facebook.CardBoxName;

import com.samskivert.swing.MultiLineLabel;

public class FriendListCell extends JPanel 
{
	public FriendListCell(FriendEntry fe)
	{
		GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);        
        this.setLayout(layout); 
        
        _entry = fe;
        _icon = new JLabel(_entry.getDisplayPic());
        
        _name = new NamePanel(_entry.getName().getFriendlyName().toString(), _entry.getName().getStrangerName().toString());
        
        _status = new StatusPanel(fe);
        
        /*
        _icon.setMinimumSize(CardBoxUI.DISPLAY_PIC);
        _icon.setMaximumSize(CardBoxUI.DISPLAY_PIC);
        _icon.setPreferredSize(CardBoxUI.DISPLAY_PIC);
        */
        
        _name.setMinimumSize(szNameMin);
        _name.setMaximumSize(szNameMax);
        _name.setPreferredSize(szNamePref);
        
        _status.setMinimumSize(szStatusMin);
        _status.setMaximumSize(szStatusMax);
        
        // Horizontal Grouping
        // sequential{ _icon, parallel{ sequential{ _name, _username }, _status }, _game }
        layout.setHorizontalGroup(
        		layout.createSequentialGroup()
        			.addComponent(_icon)
        			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(_name)
        				.addComponent(_status))
        );
        
        // Vertical Grouping
        // parallel{ _icon, sequential{ parallel{ _name, _username }, _status }, _game }
        layout.setVerticalGroup(
        		layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        			.addComponent(_icon)
        			.addGroup(layout.createSequentialGroup()
        				.addComponent(_name)
        				.addComponent(_status))
        );
	}
	
	/**
	 *  We want to make sure colour changes are consistent for all rendered elements,
	 *  so ensure we also make all colour change calls to our "child" panels as well. 
	 */
	@Override
	public void setBackground(Color bg)
	{
		super.setBackground(bg);
		if (_name != null)
			_name.setBackground(bg);
		if (_status != null)
			_status.setBackground(bg);
	}
	@Override
	public void setForeground(Color fg)
	{
		super.setForeground(fg);
		if (_name != null)
			_name.setForeground(fg);
		if (_status != null)
			_status.setForeground(fg);
	}
	
	protected class NamePanel extends JPanel
	{
		public NamePanel (String name, String username)
		{
			_name = new MultiLineLabel(name, SwingConstants.LEFT, SwingConstants.VERTICAL, 0);
	        _username = new MultiLineLabel("(" + username + ")", SwingConstants.LEFT, SwingConstants.VERTICAL, 0);
	        
	        _name.setFont(CardBoxUI.AppFontSmall);
	        _username.setFont(CardBoxUI.AppFontItalicExtraSmall);
	        
	        _name.setMinimumSize(szFriendlyMin);
	        _name.setMaximumSize(szFriendlyMax);
	        
	        _username.setMinimumSize(szStrangerMin);
	        _username.setMaximumSize(szStrangerMax);
	        
	        FlowLayout flow = new FlowLayout(SwingConstants.LEADING, 5, 1);
	        this.setLayout(flow);
	        this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	        
	        this.add(_name);
	        this.add(_username);
		}
		
		protected MultiLineLabel _name;
		protected MultiLineLabel _username;
	}
	
	protected class StatusPanel extends JPanel
		implements OnlineStatus.StatusObserver
	{
		public StatusPanel (FriendEntry fe)
		{
			_status = fe.printStatus();
			
			_status.setMinimumSize(szStatusMsgMin);
			_status.setMaximumSize(szStatusMsgMax);
			
			FlowLayout flow = new FlowLayout(SwingConstants.LEADING, 5, 0);
	        this.setLayout(flow);
	        this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	        
	        this.add(_status);
		}
		
		protected MultiLineLabel _status;

		@Override
		public void statusUpdated(CardBoxName user, OnlineStatus status)
		{
			_status.setText(status.toString());
		}
	}
	
	protected FriendEntry _entry = null;
	
	protected JLabel _icon = null;
	protected NamePanel _name = null;
	protected StatusPanel _status = null;
	
	protected static final Dimension szNameMin = new Dimension(60, 16);
	protected static final Dimension szNameMax = new Dimension(270, 18);
	protected static final Dimension szNamePref = new Dimension(270, 18);
	
	protected static final Dimension szFriendlyMin = new Dimension(30, 14);
	protected static final Dimension szFriendlyMax = new Dimension(155, 16);
	protected static final Dimension szStrangerMin = new Dimension(20, 14);
	protected static final Dimension szStrangerMax = new Dimension(95, 16);
	
	protected static final Dimension szStatusMin = new Dimension(50, 14);
	protected static final Dimension szStatusMax = new Dimension(270, 14);
	protected static final Dimension szStatusPref = new Dimension(270, 14);
	
	protected static final Dimension szStatusMsgMin = new Dimension(40, 12);
	protected static final Dimension szStatusMsgMax = new Dimension(260, 12);
}
