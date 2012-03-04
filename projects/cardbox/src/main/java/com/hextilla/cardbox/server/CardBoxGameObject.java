package com.hextilla.cardbox.server;

import javax.annotation.Generated;
import com.threerings.parlor.game.data.GameObject;

public class CardBoxGameObject extends GameObject {    

    // AUTO-GENERATED: FIELDS START
    /** The field name of the <code>gameMode</code> field. */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public static final String GAME_MODE = "gameMode";
    // AUTO-GENERATED: FIELDS END

    /** The type of game (AI, Stranger, Friend) **/	
    public String gameMode;

    // AUTO-GENERATED: METHODS START
    /**
     * Requests that the <code>gameMode</code> field be set to the
     * specified value. The local value will be updated immediately and an
     * event will be propagated through the system to notify all listeners
     * that the attribute did change. Proxied copies of this object (on
     * clients) will apply the value change when they received the
     * attribute changed notification.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void setGameMode (String value)
    {
        String ovalue = this.gameMode;
        requestAttributeChange(
            GAME_MODE, value, ovalue);
        this.gameMode = value;
    }
    // AUTO-GENERATED: METHODS END
}
