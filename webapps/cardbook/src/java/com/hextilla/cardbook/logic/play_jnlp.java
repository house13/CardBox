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

import java.util.Calendar;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.net.URL;
import javax.servlet.http.HttpServletRequest;

import com.samskivert.servlet.util.FriendlyException;
import com.samskivert.servlet.util.ParameterUtil;

import com.samskivert.velocity.Application;
import com.samskivert.velocity.InvocationContext;
import com.samskivert.velocity.Logic;

import com.hextilla.cardbox.data.CardBoxCodes;
import com.hextilla.cardbox.server.CardBoxConfig;
import com.hextilla.cardbox.server.persist.FBUserRecord;
import com.hextilla.cardbox.server.persist.GameRecord;

import com.hextilla.cardbook.CardbookApp;

import static com.hextilla.cardbook.Log.log;

/**
 * Provides a JNLP file for a particular game.
 */
public class play_jnlp extends UserLogic
{
    // documentation inherited
    public void invoke (InvocationContext ctx, CardbookApp app, FBUserRecord user)
        throws Exception
    {
        HttpServletRequest req = ctx.getRequest();
        int gameId = CardBoxConfig.getGameId();
        GameRecord game = app.getCardBoxRepository().loadGame(gameId);
        if (game == null) {
            throw new FriendlyException("error.no_such_game");
        }
        ctx.put("game", game);

        // fake up a last modified header
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR), day = cal.get(Calendar.DAY_OF_YEAR);
        cal.setTime(game.lastUpdated);
        // if it was last updated today, use the current time as the last
        // modification as we don't have finer granularity
        if (year == cal.get(Calendar.YEAR) &&
            day == cal.get(Calendar.DAY_OF_YEAR)) {
            cal = Calendar.getInstance();
        } else {
            // otherwise claim last modification at 11:59:59 on the known date
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
        }
        long lastModified = cal.getTime().getTime();
        ctx.getResponse().setDateHeader("Last-Modified", lastModified);

        String path = CLIENT_PATH;
        URL codebase;
        try {
            codebase = new URL("http", game.host, path);
        } catch (Exception e) {
            log.warning("Error creating codebase URL " +
                    "[ghost=" + game.host + ", path=" + path + "].", e);
            throw new FriendlyException("error.internal_error");
        }

        ctx.put("base_path", req.getContextPath());
        ctx.put("codebase", codebase.toString());
        ctx.put("server", game.host);
        ctx.put("port", CardBoxConfig.getServerPort());
        ctx.put("resource_url", CardBoxConfig.getResourceURL());
        ctx.put("session_id", app.getUserManager().getSession(req));

        ctx.getResponse().setContentType("application/x-java-jnlp-file");
    }

    protected static final String CLIENT_PATH = "/client";
}
