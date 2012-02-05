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

import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import com.samskivert.servlet.util.ParameterUtil;
import com.samskivert.velocity.InvocationContext;

import com.hextilla.cardbox.server.persist.GameRecord;

import com.hextilla.cardbook.Log;
import com.hextilla.cardbook.CardbookApp;
import com.hextilla.cardbook.auth.FBUser;

/**
 * Displays a list of all registered games.
 */
public class browse extends OptionalUserLogic
{
    // documentation inherited
    public void invoke (InvocationContext ctx, CardbookApp app, FBUser user)
        throws Exception
    {
        String category = ParameterUtil.getParameter(
            ctx.getRequest(), "category", false);

        List<GameRecord> games = category.equals("") ?
            // load up the metadata for all of our games
            app.getCardBoxRepository().loadGames() :
            // load up the metadata for the games in this category
            app.getCardBoxRepository().loadGames(category);

        // sort our games by name
        Collections.sort(games, new Comparator<GameRecord>() {
            public int compare (GameRecord g1, GameRecord g2) {
                return g1.name.compareTo(g2.name);
            }
        });
        ctx.put("games", games);
        ctx.put("category", category);
    }
}
