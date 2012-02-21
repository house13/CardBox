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

import javax.annotation.Generated;

import com.hextilla.cardbox.client.CardBoxCardService;

import com.threerings.presents.client.InvocationService;
import com.threerings.presents.data.InvocationMarshaller;

/**
 * Provides the implementation of the {@link CardBoxCardService} interface
 * that marshalls the arguments and delivers the request to the provider
 * on the server. Also provides an implementation of the response listener
 * interfaces that marshall the response arguments and deliver them back
 * to the requesting client.
 */
@Generated(value={"com.threerings.presents.tools.GenServiceTask"},
           comments="Derived from CardBoxCardService.java.")
public class CardBoxCardMarshaller extends InvocationMarshaller
    implements CardBoxCardService
{
    /** The method id used to dispatch {@link #getCards} requests. */
    public static final int GET_CARDS = 1;

    // from interface CardBoxCardService
    public void getCards (int arg1, InvocationService.ResultListener arg2)
    {
        InvocationMarshaller.ResultMarshaller listener2 = new InvocationMarshaller.ResultMarshaller();
        listener2.listener = arg2;
        sendRequest(GET_CARDS, new Object[] {
            Integer.valueOf(arg1), listener2
        });
    }
}
