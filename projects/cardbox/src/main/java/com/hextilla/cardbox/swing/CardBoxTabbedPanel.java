package com.hextilla.cardbox.swing;

import javax.swing.JTabbedPane;

//TODO: make a custom version of the tabbedPane
public class CardBoxTabbedPanel extends JTabbedPane {
	public CardBoxTabbedPanel() {
		super(JTabbedPane.TOP);
	}
	
	public CardBoxTabbedPanel(int tabPlacement) {
		super(tabPlacement);
	}

}
