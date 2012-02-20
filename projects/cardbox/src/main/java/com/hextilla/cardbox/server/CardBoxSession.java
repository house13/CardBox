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

package com.hextilla.cardbox.server;

import com.threerings.crowd.data.TokenRing;
import com.threerings.crowd.server.CrowdSession;

import com.hextilla.cardbox.data.CardBoxUserObject;

/**
 * Extends {@link CrowdSession} and customizes it for the CardBox system.
 */
public class CardBoxSession extends CrowdSession
{
    // documentation inherited
    @Override
    protected void sessionWillStart ()
    {
        super.sessionWillStart();

        // if we have auth data in the form of a token ring or session ID, use it (we set things directly here
        // rather than use the setter methods because the user object is not yet out in the wild)
        CardBoxUserObject user = (CardBoxUserObject)_clobj;
        if (_authdata instanceof TokenRing) {
            user.tokens = (TokenRing)_authdata;
        } else if (_authdata instanceof String) {
        	user.session = (String)_authdata;
        	user.tokens = new TokenRing(0);
        } else {
            // otherwise give them zero privileges
            user.tokens = new TokenRing(0);
        }
    }
}
