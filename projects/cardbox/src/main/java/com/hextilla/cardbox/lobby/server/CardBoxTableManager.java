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

package com.hextilla.cardbox.lobby.server;

import static com.hextilla.cardbox.lobby.Log.log;

import com.threerings.presents.dobj.RootDObjectManager;
import com.threerings.presents.server.InvocationException;
import com.threerings.presents.server.InvocationManager;

import com.threerings.crowd.server.PlaceRegistry;

import com.threerings.parlor.data.Table;
import com.threerings.parlor.game.data.GameObject;
import com.threerings.parlor.game.server.GameManager;
import com.threerings.parlor.server.TableManager;

import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.server.CardBoxManager;


/**
 * Customizes the normal table manager with CardBox specific requirements.
 */
public class CardBoxTableManager extends TableManager
{
    public CardBoxTableManager (RootDObjectManager omgr, InvocationManager invmgr,
                               PlaceRegistry plreg, CardBoxManager cardmgr, LobbyManager lmgr)
    {
        super(omgr, invmgr, plreg, lmgr.getPlaceObject());
        _cardmgr = cardmgr;
        _lmgr = lmgr;
    }

    // documentation inherited
    @Override
    protected int createGame (Table table)
        throws InvocationException
    {
        // fill the players array into the game config  
    	table.config.players = table.getPlayers();

        // pass the buck to the cardbox manager to create the game
        GameManager gmgr = _cardmgr.createGame(table.config);

        // tell the table manager about this game
        gameCreated(table, (GameObject)gmgr.getPlaceObject(), gmgr);

        return gmgr.getPlaceObject().getOid();
    }

    /** The cardbox manager with whom we operate. */
    protected CardBoxManager _cardmgr;

    /** The lobby manager for which we're working. */
    protected LobbyManager _lmgr;
}
