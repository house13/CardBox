package com.hextilla.cardbox.swing;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

import com.hextilla.cardbox.client.CardBoxUI;

@SuppressWarnings("serial")
public class CardBoxButton extends JButton implements MouseListener {
	
	private boolean m_mouseInside = false;
	private boolean m_pressed = false;
	
	public CardBoxButton(String text) {
		super(text);
		addMouseListener(this);
	}

	public void paint(Graphics g) {
		Graphics2D gfx = (Graphics2D)g;
		gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setColor(CardBoxUI.GRAY);
		g.fillRoundRect(0, 0, this.getWidth()-1, this.getHeight()-1, 16, 16);

		if (!this.isEnabled()) {
			g.setColor(CardBoxUI.LIGHT_GRAY);
			g.fillRoundRect(2, 2, this.getWidth()-5, this.getHeight()-5, 12, 12);
		} else {
			if (m_mouseInside) {
			g.setColor(CardBoxUI.DARK_BLUE);
			} else {
				g.setColor(CardBoxUI.LIGHT_BLUE);
			}
			g.fillRoundRect(2, 2, this.getWidth()-5, this.getHeight()-5, 12, 12);
			if (m_pressed) {
				g.setColor(CardBoxUI.DARK_BLUE);
			} else {
				g.setColor(CardBoxUI.LIGHT_BLUE);
			}
			g.fillRoundRect(4, 4, this.getWidth()-9, this.getHeight()-9, 10, 10);
		}
		
		g.setColor(CardBoxUI.GRAY);
		FontMetrics metrics = g.getFontMetrics(this.getFont());
		int textHeight = metrics.getHeight();
		int textWidth = metrics.stringWidth(this.getText());
		g.setFont(this.getFont());
		g.drawString(this.getText(), (this.getWidth()-textWidth)/2, (this.getHeight()+textHeight)/2-metrics.getDescent());
	}

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
		m_mouseInside = true;
		repaint();
	}

	public void mouseExited(MouseEvent arg0) {
		m_mouseInside = false;
		repaint();
	}

	public void mousePressed(MouseEvent arg0) {
		m_pressed = true;
		repaint();
	}

	public void mouseReleased(MouseEvent arg0) {
		m_pressed = false;
		repaint();
	}
}
