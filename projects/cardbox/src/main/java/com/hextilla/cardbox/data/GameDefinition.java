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

package com.hextilla.cardbox.data;

import java.util.List;

import com.samskivert.util.StringUtil;

import com.threerings.io.Streamable;
import com.threerings.util.ActionScript;

import com.threerings.parlor.data.Parameter;

/**
 * Contains the information about a game as described by the game definition XML file.
 */
public class GameDefinition implements Streamable
{
    /** A string identifier for the game. */
    public String ident;

    /** The class name of the <code>GameController</code> derivation that we use to bootstrap on
     * the client. */
    public String controller;

    /** The class name of the <code>GameManager</code> derivation that we use to manage the game on
     * the server. */
    public String manager;

    /** The MD5 digest of the game media file. */
    public String digest;

    /** The configuration of the match-making mechanism. */
    public MatchConfig match;

    /** Parameters used to configure the game itself. */
    public Parameter[] params;

    /**
     * Provides the path to this game's media (a jar file).
     *
     * @param gameId the unique id of the game provided when this game definition was registered
     * with the system, or -1 if we're running in test mode.
     */
    public String getMediaPath (int gameId)
    {
        return (gameId == -1) ? ident + ".jar" : ident + "-" + gameId + ".jar";
    }

    /**
     * Returns true if a single player can play this game (possibly against AI opponents), or if
     * opponents are needed.
     */
    public boolean isSinglePlayerPlayable ()
    {
        // maybe it's just single player no problem
        int minPlayers = 2;
        if (match != null) {
            minPlayers = match.getMinimumPlayers();
            if (minPlayers <= 1) {
                return true;
            }
        }

        // or maybe it has AIs
        int aiCount = 0;
        for (Parameter param : params) {
            if (param instanceof AIParameter) {
                aiCount = ((AIParameter)param).maximum;
            }
        }
        return (minPlayers - aiCount) <= 1;
    }

    /** Called when parsing a game definition from XML. */
    @ActionScript(omit=true)
    public void setParams (List<Parameter> list)
    {
        params = list.toArray(new Parameter[list.size()]);
    }

    /** Generates a string representation of this instance. */
    @Override
    public String toString ()
    {
        return StringUtil.fieldsToString(this);
    }
}
