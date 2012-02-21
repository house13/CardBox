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
import com.threerings.presents.server.InvocationDispatcher;
import com.threerings.presents.server.InvocationException;

import com.hextilla.cardbox.data.CardBoxCardMarshaller;

/**
 * Dispatches requests to the {@link CardBoxCardProvider}.
 */
@Generated(value={"com.threerings.presents.tools.GenServiceTask"},
           comments="Derived from CardBoxCardService.java.")
public class CardBoxCardDispatcher extends InvocationDispatcher<CardBoxCardMarshaller>
{
    /**
     * Creates a dispatcher that may be registered to dispatch invocation
     * service requests for the specified provider.
     */
    public CardBoxCardDispatcher (CardBoxCardProvider provider)
    {
        this.provider = provider;
    }

    @Override
    public CardBoxCardMarshaller createMarshaller ()
    {
        return new CardBoxCardMarshaller();
    }

    @Override
    public void dispatchRequest (
        ClientObject source, int methodId, Object[] args)
        throws InvocationException
    {
        switch (methodId) {
        case CardBoxCardMarshaller.GET_CARDS:
            ((CardBoxCardProvider)provider).getCards(
                source, ((Integer)args[0]).intValue(), (InvocationService.ResultListener)args[1]
            );
            return;

        default:
            super.dispatchRequest(source, methodId, args);
            return;
        }
    }
}
