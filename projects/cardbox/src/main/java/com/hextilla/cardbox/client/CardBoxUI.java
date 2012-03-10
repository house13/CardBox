//
// CardBox library - framework for matchmaking networked games
// Copyright (C) 2005-2011 Three Rings Design, Inc., All Rights Reserved
// http://github.com/threerings/game-gardens
//
// This library is free software; you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published
// by the Free Software Foundation; either version 2.1 of the License, or
// (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package com.hextilla.cardbox.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.hextilla.cardbox.util.CardBoxContext;

import static com.hextilla.cardbox.Log.log;

/**
 * Contains various bits needed for our look and feel.
 */
public class CardBoxUI
{
    /** The fancy cursive font we use to display game names. */
    public static Font HextillaFontLarge;
    public static Font HextillaFontMedium;
    public static Font HextillaFontSmall;
    public static Font HextillaFont;
    
    /** The nice blue background we use for scrolly bits. */
    public static final Color LIGHT_BLUE = new Color(0xC8E1E9);
    
    public static ImageIcon getDefaultDisplayPic(){
        
    	// Load the pic if it hasn't been loaded yet
    	if (defaultDisplayPic == null){
	        // Try to load the default friend display picture
	        BufferedImage image = null;  
	        try {
	        	// Load and scale the picture        	  
	        	//defaultDisplayPic = new ImageIcon(CardBoxUI.class.getClassLoader().getResource(DEFAULT_DISPLAY_PIC_PATH));
	        	image = ImageIO.read(CardBoxUI.class.getClassLoader().getResource(DEFAULT_DISPLAY_PIC_PATH));
	        } catch (Exception e) {
	        	// Just use an empty image
	        	log.info("Error: " + e.getMessage());
	            log.info("Could not located default display picture, defaulting to a black sqare!");
	        	image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
	        }
	        
	        // Scale that image, smooooth style
	        defaultDisplayPic = new ImageIcon(image.getScaledInstance(32, 32, Image.SCALE_SMOOTH));	        
    	}
    	
    	// Return the default pic
        return defaultDisplayPic;
    }

    public static void init (CardBoxContext ctx)
    {
        _ctx = ctx;

        // try to load our fancy font
        try {
            InputStream in =
                CardBoxUI.class.getClassLoader().getResourceAsStream("rsrc/media/League Gothic.ttf");
            HextillaFont = Font.createFont(Font.TRUETYPE_FONT, in);
            in.close();
        } catch (Exception e) {
            log.warning("Failed to load custom font, falling back to default.", e);
            HextillaFont = BORING_DEFAULT;
        }
        HextillaFontLarge = HextillaFont.deriveFont(Font.PLAIN, 52);
        HextillaFontMedium = HextillaFont.deriveFont(Font.PLAIN, 30);
        HextillaFontSmall = HextillaFont.deriveFont(Font.PLAIN, 20);
    }
    
    protected static CardBoxContext _ctx;

    /** The boring default font used if the custom font can't be loaded. */
    protected static final Font BORING_DEFAULT = new Font("Dialog", Font.PLAIN, 12);
    
    // The default image for use in the friend list (used if friend pic is not loaded/available)
    protected static ImageIcon defaultDisplayPic = null;    
    
    protected static String DEFAULT_DISPLAY_PIC_PATH = "rsrc/media/displayPic.png";
}
