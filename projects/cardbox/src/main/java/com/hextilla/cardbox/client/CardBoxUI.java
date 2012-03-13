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

import java.io.ByteArrayInputStream;
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
    public static Font AppFontItalicLarge;
    public static Font AppFontItalicMedium;
    public static Font AppFontItalicSmall;
    public static Font AppFontItalic;
    public static Font FbFontLarge;
    public static Font FbFontMedium;
    public static Font FbFontSmall;
    public static Font FbFont;
    public static Font FbFontBoldLarge;
    public static Font FbFontBoldMedium;
    public static Font FbFontBoldSmall;
    public static Font FbFontBold;
    
    /** The nice blue background we use for scrolly bits. */
    public static final Color LIGHT_BLUE = new Color(0xC8E1E9);
    public static final Color ORANGE = new Color(250, 153, 0);
    public static final Color DARK_BLUE = new Color(145, 185, 215);
    public static final Color GRAY = Color.DARK_GRAY;
    
    public static ImageIcon getDefaultDisplayPic() {
    	
    	return getImageIcon(defaultDisplayPic, DEFAULT_DISPLAY_PIC_PATH, 64, 64, 32, 32);
    }
    
    public static ImageIcon getGlobalChatIcon(){
    	
    	return getImageIcon(globalChatPic, GLOBAL_CHAT_PIC_PATH, 32, 32, 16, 16);
    }
    
    public static ImageIcon getFriendChatIcon() {
    	
    	return getImageIcon(friendChatPic, FRIEND_CHAT_PIC_PATH, 32, 32, 16, 16);
    }    
    
    public static ImageIcon getFacebookIcon() {
    	
    	return getImageIcon(facebookIcon, FACEBOOK_ICON_PATH, 140, 140, 20, 20);
    }  
    
    public static ImageIcon renderDisplayPic(String bytes) {
    	return renderDisplayPicFromRaw(bytes, 32, 32);
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
	            log.info("Could not load " + icon_path + ", defaulting to a black sqare!");
	        	image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        }
	        
	        // Scale that image, smooooth style
	        icon = new ImageIcon(image.getScaledInstance(scaleW, scaleH, Image.SCALE_SMOOTH));	        
    	}
    	
    	// Return the default pic
        return icon;
	}
    
    /** Given a byte array containing JPEG image data, return an ImageIcon of the given scale */ 
    private static ImageIcon renderDisplayPicFromRaw(String bytes, int scaleW, int scaleH)
    {
    	BufferedImage img = null;
    	try {
    		InputStream in = new ByteArrayInputStream(bytes.getBytes());
    		img = ImageIO.read(in);
    	} catch (Exception e) {
    		log.warning("Could not render display picture from raw", e);
    		img = new BufferedImage(scaleW, scaleH, BufferedImage.TYPE_INT_RGB);
    	}
    	ImageIcon pic = new ImageIcon(img.getScaledInstance(scaleW, scaleH, Image.SCALE_SMOOTH));
    	return pic;
    }

	public static void init (CardBoxContext ctx)
    {
        _ctx = ctx;

        // try to load our fancy font
        try {
            InputStream in =
                CardBoxUI.class.getClassLoader().getResourceAsStream("rsrc/media/OpenSans-Regular.ttf");
            AppFont = Font.createFont(Font.TRUETYPE_FONT, in);
            in.close();
        } catch (Exception e) {
            log.warning("Failed to load custom font, falling back to default.", e);
            AppFont = BORING_DEFAULT;
        }
        AppFontLarge = AppFont.deriveFont(Font.PLAIN, 30);
        AppFontMedium = AppFont.deriveFont(Font.PLAIN, 20);
        AppFontSmall = AppFont.deriveFont(Font.PLAIN, 14);
        try {
            InputStream in =
                CardBoxUI.class.getClassLoader().getResourceAsStream("rsrc/media/OpenSans-LightItalic.ttf");
            AppFontItalic = Font.createFont(Font.TRUETYPE_FONT, in);
            in.close();
        } catch (Exception e) {
            log.warning("Failed to load custom font, falling back to default.", e);
            AppFontItalic = BORING_DEFAULT;
        }
        AppFontItalicLarge = AppFontItalic.deriveFont(Font.PLAIN, 30);
        AppFontItalicMedium = AppFontItalic.deriveFont(Font.PLAIN, 20);
        AppFontItalicSmall = AppFontItalic.deriveFont(Font.PLAIN, 14);
        try {
        	InputStream in =
                    CardBoxUI.class.getClassLoader().getResourceAsStream("rsrc/media/League-Gothic.ttf");
                TitleFont = Font.createFont(Font.TRUETYPE_FONT, in);
                in.close();  
        } catch (Exception e) {
            log.warning("Failed to load custom font, falling back to default.", e);
            TitleFont = BORING_DEFAULT;
        }
        TitleFontLarge = TitleFont.deriveFont(Font.PLAIN, 52);
        TitleFontMedium = TitleFont.deriveFont(Font.PLAIN, 30);
        TitleFontSmall = TitleFont.deriveFont(Font.PLAIN, 20);
        try {
            InputStream in =
                CardBoxUI.class.getClassLoader().getResourceAsStream("rsrc/media/Lucida-Grande.ttf");
            FbFont = Font.createFont(Font.TRUETYPE_FONT, in);
            in.close();
        } catch (Exception e) {
            log.warning("Failed to load custom font, falling back to default.", e);
            FbFont = BORING_DEFAULT;
        }
        FbFontLarge = FbFont.deriveFont(Font.PLAIN, 30);
        FbFontMedium = FbFont.deriveFont(Font.PLAIN, 22);
        FbFontSmall = FbFont.deriveFont(Font.PLAIN, 14);
        try {
            InputStream in =
                CardBoxUI.class.getClassLoader().getResourceAsStream("rsrc/media/Lucida-Grande-Bold.ttf");
            FbFontBold = Font.createFont(Font.TRUETYPE_FONT, in);
            in.close();
        } catch (Exception e) {
            log.warning("Failed to load custom font, falling back to default.", e);
            FbFontBold = BORING_DEFAULT;
        }
        FbFontBoldLarge = FbFontBold.deriveFont(Font.PLAIN, 30);
        FbFontBoldMedium = FbFontBold.deriveFont(Font.PLAIN, 22);
        FbFontBoldSmall = FbFontBold.deriveFont(Font.PLAIN, 14);
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
    
    // The image/icon used for the global Chat tab icon
    protected static ImageIcon facebookIcon = null;        
    protected static String FACEBOOK_ICON_PATH = "rsrc/media/f_logo.png"; 
}
