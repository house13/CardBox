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

import javax.annotation.Generated;

import com.threerings.presents.client.InvocationService;
import com.threerings.presents.data.ClientObject;
import com.threerings.presents.server.InvocationException;
import com.threerings.presents.server.InvocationProvider;
import com.hextilla.cardbox.client.CardBoxService;

/**
 * Defines the server-side of the {@link CardBoxService}.
 */
@Generated(value={"com.threerings.presents.tools.GenServiceTask"},
           comments="Derived from CardBoxService.java.")
public interface CardBoxProvider extends InvocationProvider
{
    /**
     * Handles a {@link CardBoxService#getLobbyOid} request.
     */
    void getLobbyOid (ClientObject caller, int arg1, InvocationService.ResultListener arg2)
        throws InvocationException;
}
