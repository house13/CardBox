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

package com.hextilla.cardbook.logic;

import com.samskivert.velocity.InvocationContext;

import com.hextilla.cardbox.server.CardBoxConfig;

import com.hextilla.cardbook.CardbookApp;

import com.hextilla.cardbox.server.persist.FBUserRecord;

/**
 * Fires up a game in an applet.
 */
public class play_game extends view_game
{
    // documentation inherited
    public void invoke (InvocationContext ctx, CardbookApp app, FBUserRecord user)
        throws Exception
    {
        super.invoke(ctx, app, user);
        ctx.put("port", CardBoxConfig.getServerPort());
        ctx.put("resource_url", CardBoxConfig.getResourceURL());
    }
}
