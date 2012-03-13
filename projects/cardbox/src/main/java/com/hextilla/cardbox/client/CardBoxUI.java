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
    /** The fonts we use to display game names. */
	public static Font TitleFontLarge;
	public static Font TitleFontMedium;
	public static Font TitleFontSmall;
	public static Font TitleFont;
    public static Font AppFontLarge;
    public static Font AppFontMedium;
    public static Font AppFontSmall;
    public static Font AppFont;
    
    /** The nice blue background we use for scrolly bits. */
    public static final Color LIGHT_BLUE = new Color(0xC8E1E9);
    public static final Color ORANGE = new Color(250, 153, 0);
    public static final Color DARK_BLUE = new Color(145, 185, 215);
    public static final Color GRAY = Color.DARK_GRAY;
    
    public static ImageIcon getDefaultDisplayPic(){
    	
    	return getImageIcon(defaultDisplayPic, DEFAULT_DISPLAY_PIC_PATH, 64, 64, 32, 32);
    }
    
    public static ImageIcon getGlobalChatIcon(){
    	
    	return getImageIcon(globalChatPic, GLOBAL_CHAT_PIC_PATH, 32, 32, 16, 16);
    }
    
    public static ImageIcon getFriendChatIcon(){
    	
    	return getImageIcon(friendChatPic, FRIEND_CHAT_PIC_PATH, 32, 32, 16, 16);
    }    

    private static ImageIcon getImageIcon(ImageIcon icon, String icon_path, 
    		int width, int height, int scaleW, int scaleH) {
        
    	// Load the pic if it hasn't been loaded yet
    	if (icon == null){
	        // Try to load the default friend display picture
	        BufferedImage image = null;  
	        try {
	        	// Load and scale the picture        	  
	        	//defaultDisplayPic = new ImageIcon(CardBoxUI.class.getClassLoader().getResource(DEFAULT_DISPLAY_PIC_PATH));
	        	image = ImageIO.read(CardBoxUI.class.getClassLoader().getResource(icon_path));
	        } catch (Exception e) {
	        	// Just use an empty image
	        	log.info("Error: " + e.getMessage());
	            log.info("Could not load " + DEFAULT_DISPLAY_PIC_PATH + ", defaulting to a black sqare!");
	        	image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        }
	        
	        // Scale that image, smooooth style
	        icon = new ImageIcon(image.getScaledInstance(scaleW, scaleH, Image.SCALE_SMOOTH));	        
    	}
    	
    	// Return the default pic
        return icon;
	}

	public static void init (CardBoxContext ctx)
    {
        _ctx = ctx;

        // try to load our fancy font
        try {
            InputStream in =
                CardBoxUI.class.getClassLoader().getResourceAsStream("rsrc/media/jargon.ttf");
            AppFont = Font.createFont(Font.TRUETYPE_FONT, in);
            in.close();
        } catch (Exception e) {
            log.warning("Failed to load custom font, falling back to default.", e);
            AppFont = BORING_DEFAULT;
        }
        AppFontLarge = AppFont.deriveFont(Font.PLAIN, 52);
        AppFontMedium = AppFont.deriveFont(Font.PLAIN, 30);
        AppFontSmall = AppFont.deriveFont(Font.PLAIN, 20);
        try {
        	InputStream in =
                    CardBoxUI.class.getClassLoader().getResourceAsStream("rsrc/media/League Gothic.ttf");
                TitleFont = Font.createFont(Font.TRUETYPE_FONT, in);
                in.close();  
        } catch (Exception e) {
            log.warning("Failed to load custom font, falling back to default.", e);
            TitleFont = BORING_DEFAULT;
        }
        TitleFontLarge = TitleFont.deriveFont(Font.PLAIN, 52);
        TitleFontMedium = TitleFont.deriveFont(Font.PLAIN, 30);
        TitleFontSmall = TitleFont.deriveFont(Font.PLAIN, 20);
    }
    
    protected static CardBoxContext _ctx;

    /** The boring default font used if the custom font can't be loaded. */
    protected static final Font BORING_DEFAULT = new Font("Dialog", Font.PLAIN, 12);
    
    // The default image for use in the friend list (used if friend pic is not loaded/available)
    protected static ImageIcon defaultDisplayPic = null;        
    protected static String DEFAULT_DISPLAY_PIC_PATH = "rsrc/media/displayPic.png";
    
    // The image/icon used for the friend Chat tab icon
    protected static ImageIcon friendChatPic = null;        
    protected static String FRIEND_CHAT_PIC_PATH = "rsrc/media/friendIcon.png";
    
    // The image/icon used for the global Chat tab icon
    protected static ImageIcon globalChatPic = null;        
    protected static String GLOBAL_CHAT_PIC_PATH = "rsrc/media/globalIcon.png";    
}
