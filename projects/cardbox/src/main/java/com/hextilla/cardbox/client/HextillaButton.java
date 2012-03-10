package com.hextilla.cardbox.client;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class HextillaButton extends JButton implements MouseListener {
	
	private boolean m_mouseInside = false;
	private boolean m_pressed = false;
	
	public HextillaButton(String text) {
		super(text);
		addMouseListener(this);
	}

	public void paint(Graphics g) {
		Color highlight;
		if (m_mouseInside) {
			highlight = Color.LIGHT_GRAY;
		} else {
			highlight = Color.DARK_GRAY;
		}
		g.setColor(Color.BLACK);
		g.fillRoundRect(0, 0, this.getWidth()-1, this.getHeight()-1, 16, 16);
		g.setColor(highlight);
		g.drawRoundRect(0, 0, this.getWidth()-1, this.getHeight()-1, 16, 16);
		if (m_pressed) {
			g.setColor(new Color(190, 205, 225));
		} else {
			g.setColor(new Color(225, 235, 250));
		}
		g.fillRoundRect(2, 2, this.getWidth()-5, this.getHeight()-5, 12, 12);
		g.setColor(highlight);
		g.drawRoundRect(2, 2, this.getWidth()-5, this.getHeight()-5, 12, 12);

		g.setColor(Color.DARK_GRAY);
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
