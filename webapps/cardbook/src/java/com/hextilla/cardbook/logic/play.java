//
//CardBox library - framework for matchmaking networked games
//Copyright (C) 2005-2011 Three Rings Design, Inc., All Rights Reserved
//http://github.com/threerings/game-gardens
//
//This library is free software; you can redistribute it and/or modify it
//under the terms of the GNU Lesser General Public License as published
//by the Free Software Foundation; either version 2.1 of the License, or
//(at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this library; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package com.hextilla.cardbook.logic;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.restfb.FacebookClient;
import com.restfb.DefaultFacebookClient;
import com.restfb.types.User;

import com.samskivert.servlet.user.InvalidUsernameException;
import com.samskivert.servlet.user.Username;
import com.samskivert.servlet.util.FriendlyException;
import com.samskivert.servlet.util.ParameterUtil;

import com.samskivert.util.StringUtil;
import com.samskivert.util.Tuple;
import com.samskivert.velocity.Application;
import com.samskivert.velocity.InvocationContext;
import com.samskivert.velocity.Logic;

import com.hextilla.cardbox.data.CardBoxCodes;
import com.hextilla.cardbox.server.CardBoxConfig;
import com.hextilla.cardbox.server.persist.GameRecord;

import com.hextilla.cardbook.CardbookApp;
import com.hextilla.cardbook.auth.FBUserManager;

import com.hextilla.cardbox.server.persist.FBUserRecord;
import com.hextilla.cardbox.server.persist.FBUserRepository;

import com.hextilla.cardbox.facebook.CardBoxFacebookConfig;

import static com.hextilla.cardbook.Log.log;

/**
*  OAuth 2.0 authentication through Facebook, and
*  handling user authentication accordingly
*/
public class play extends UserLogic
{
	 // documentation inherited
	 public void invoke (InvocationContext ctx, CardbookApp app, FBUserRecord user)
	     throws Exception
	 {
		 ctx.put("fbauthed", true);
	 }
}

