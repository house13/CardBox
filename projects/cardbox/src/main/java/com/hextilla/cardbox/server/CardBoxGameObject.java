package com.hextilla.cardbox.server;

import javax.annotation.Generated;
import com.threerings.parlor.game.data.GameObject;

public class CardBoxGameObject extends GameObject {    

    // AUTO-GENERATED: FIELDS START
    /** The field name of the <code>gameMode</code> field. */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public static final String GAME_MODE = "gameMode";

    /** The field name of the <code>player1Score</code> field. */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public static final String PLAYER1_SCORE = "player1Score";

    /** The field name of the <code>player2Score</code> field. */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public static final String PLAYER2_SCORE = "player2Score";
    // AUTO-GENERATED: FIELDS END

    /** The type of game (AI, Stranger, Friend) **/	
    public String gameMode;
        
    // The 1st players score
    public int player1Score = 0;

    // The 2nd players score
    public int player2Score = 0; 

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

    /**
     * Requests that the <code>player1Score</code> field be set to the
     * specified value. The local value will be updated immediately and an
     * event will be propagated through the system to notify all listeners
     * that the attribute did change. Proxied copies of this object (on
     * clients) will apply the value change when they received the
     * attribute changed notification.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void setPlayer1Score (int value)
    {
        int ovalue = this.player1Score;
        requestAttributeChange(
            PLAYER1_SCORE, Integer.valueOf(value), Integer.valueOf(ovalue));
        this.player1Score = value;
    }

    /**
     * Requests that the <code>player2Score</code> field be set to the
     * specified value. The local value will be updated immediately and an
     * event will be propagated through the system to notify all listeners
     * that the attribute did change. Proxied copies of this object (on
     * clients) will apply the value change when they received the
     * attribute changed notification.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void setPlayer2Score (int value)
    {
        int ovalue = this.player2Score;
        requestAttributeChange(
            PLAYER2_SCORE, Integer.valueOf(value), Integer.valueOf(ovalue));
        this.player2Score = value;
    }
    // AUTO-GENERATED: METHODS END
}
