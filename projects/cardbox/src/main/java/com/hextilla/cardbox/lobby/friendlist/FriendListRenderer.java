package com.hextilla.cardbox.lobby.friendlist;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.hextilla.cardbox.util.CardBoxContext;

// Renders a friend entry object in a list
public class FriendListRenderer extends JLabel implements ListCellRenderer 
{		 
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {	
		
		// Cell container (holds this object)
		JPanel cell = new JPanel();
		cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));		
		cell.add(this);
		
		// Add border, text and icon
		setIcon(((FriendEntry)value).getDisplayPic());
		setText(((FriendEntry)value).getName().getFriendlyName().toString());
		setBorder(BorderFactory.createEmptyBorder(5, 1, 5, 1));
		
		// Change colour when selected
		if (isSelected){
			// Change text from black to white
			setForeground(Color.white);
			
			// Change cell colour to DARK_GRAY
			cell.setBackground(Color.DARK_GRAY);
		} else {
			// Change text from black to white
			setForeground(Color.black);
			
			// Change cell colour to black
			cell.setBackground(Color.GRAY);
		}
		
		// Outline the cell if it has focus
		if (cellHasFocus){
			cell.setBorder(BorderFactory.createLineBorder(Color.white, 1));
		} else {
			cell.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		}
		
		//return this;
		return cell;
	}	
	
	/** Giver of life and services. */
	protected CardBoxContext _ctx;	
}

class DisplayPic extends JPanel {
    Image image;
 
    DisplayPic(Image image2) {
        this.image = image2;
    }
 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
}
