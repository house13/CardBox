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

import com.samskivert.text.MessageUtil;

import com.threerings.presents.data.InvocationCodes;
import com.hextilla.cardbox.client.CardBoxService;

/**
 * Codes and constants relating to the Toy Box services.
 */
public class CardBoxCodes implements InvocationCodes
{
    /** Defines our invocation services group. */
    public static final String TOYBOX_GROUP = "cardbox";

    /** Defines the general CardBox translation message bundle.*/
    public static final String TOYBOX_MSGS = "cardbox";

    /** An error constant returned by the {@link CardBoxService}. */
    public static final String ERR_NO_SUCH_GAME =
        MessageUtil.qualify(TOYBOX_MSGS, "e.no_such_game");

    /** An error constant indicating a malformed game definition. */
    public static final String ERR_MALFORMED_GAMEDEF =
        MessageUtil.qualify(TOYBOX_MSGS, "e.malformed_gamedef");
}
