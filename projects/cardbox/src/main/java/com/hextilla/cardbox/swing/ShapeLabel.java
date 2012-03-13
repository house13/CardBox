package com.hextilla.cardbox.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;

import com.hextilla.cardbox.client.CardBoxUI;
import com.samskivert.swing.ShapeIcon;

public class ShapeLabel extends ShapeIcon {
	
	public ShapeLabel(Shape shape, Color fillColor, Color outlineColor) {
		super(shape, fillColor, outlineColor);
		_font = CardBoxUI.AppFontSmall;
	}
	
	public ShapeLabel(String text, Shape shape, Color fillColor, Color outlineColor) {
		super(shape, fillColor, outlineColor);
		_text = text;
		_font = CardBoxUI.AppFontSmall;
	}
	
    // get the text shown on the label
    public String getText ()
    {
        return _text;
    }	
    
    // set the text shown on the label
    public void setText (String text)
    {
        _text = text;
    }	
    
    // get the tfont
    public Font getFont ()
    {
        return _font;
    }	
    
    // set the font
    public void setFont (Font font)
    {
    	_font = font;
    }    
	
    // documentation inherited from interface Icon
    public void paintIcon (Component c, Graphics g, int x, int y)
    {
        Graphics2D g2 = (Graphics2D) g;
        // turn on anti-aliasing
        Object oldAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the shape
        Rectangle bounds = _shape.getBounds();
        int dx = x - bounds.x;
        int dy = y - bounds.y;
        g2.translate(dx, dy);
        if (_fillColor != null) {
            g2.setColor(_fillColor);
            g2.fill(_shape);
        }
        if (_outlineColor != null) {
            g2.setColor(_outlineColor);
            g2.draw(_shape);
        }
        g2.translate(-dx, -dy);
        
        // Add the text onto the shape
		g.setColor(CardBoxUI.GRAY);
		FontMetrics metrics = g.getFontMetrics(this.getFont());
		int textHeight = metrics.getHeight();
		int textWidth = metrics.stringWidth(this.getText());
		g.setFont(this.getFont());
		g.drawString(this.getText(), (this.getIconWidth()-textWidth)/2, (this.getIconHeight()+textHeight)/2-metrics.getDescent());
        
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAlias);
    }

    String _text;
    Font _font;
}
