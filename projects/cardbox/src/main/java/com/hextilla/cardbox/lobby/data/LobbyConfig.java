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

package com.hextilla.cardbox.lobby.data;

import com.threerings.crowd.client.PlaceController;
import com.threerings.crowd.data.PlaceConfig;

import com.hextilla.cardbox.data.GameDefinition;
import com.hextilla.cardbox.lobby.client.LobbyController;

/**
 * Defines the configuration of a CardBox match-making lobby.
 */
public class LobbyConfig extends PlaceConfig
{
    /**
     * A default constructor used when unserializing.
     */
    public LobbyConfig ()
    {
    }

    /**
     * Creates the config for a new lobby that will match-make games with
     * the specified configuration.
     */
    public LobbyConfig (int gameId, GameDefinition gamedef)
    {
        _gameId = gameId;
        _gamedef = gamedef;
    }

    // documentation inherited
    @Override
    public PlaceController createController ()
    {
        return new LobbyController();
    }

    // documentation inherited
    @Override
    public String getManagerClassName ()
    {
        return "com.hextilla.cardbox.lobby.server.LobbyManager";
    }

    /**
     * Returns this game's unique identifier.
     */
    public int getGameId ()
    {
        return _gameId;
    }

    /**
     * Returns the definition of the game we're matchmaking in this lobby.
     */
    public GameDefinition getGameDefinition ()
    {
        return _gamedef;
    }

    // documentation inherited
    @Override
    protected void toString (StringBuilder buf)
    {
        super.toString(buf);
        if (buf.length() > 1) {
            buf.append(", ");
        }
        buf.append("gamedef=").append(_gamedef);
    }

    /** The unique id for the game we'll be matchmaking. */
    protected int _gameId;

    /** The definition for the game we'll be matchmaking. */
    protected GameDefinition _gamedef;
}
