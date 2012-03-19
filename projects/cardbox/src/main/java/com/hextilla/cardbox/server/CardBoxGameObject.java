package com.hextilla.cardbox.server;

import javax.annotation.Generated;

import com.threerings.parlor.card.data.CardGameObject;
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

    /** The field name of the <code>rematch</code> field. */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public static final String REMATCH = "rematch";
    // AUTO-GENERATED: FIELDS END

    /** The type of game (AI, Stranger, Friend) **/	
    public String gameMode;
        
    // The 1st players score
    public int player1Score = 0;

    // The 2nd players score
    public int player2Score = 0; 
    
    /** Indicates that the player has not requested or accepted a rematch. */
    public static final int NO_REQUEST = 0;

    /** Indicates that the player has requested a rematch. */
    public static final int REQUESTS_REMATCH = 1;

    /** Indicates that the player has accepted the rematch request. */
    public static final int ACCEPTS_REMATCH = 2;
    
    /** The game state, one of {@link #NO_REQUEST}, {@link #REQUESTS_REMATCH}, or
     * {@link #ACCEPTS_REMATCH}. */
    public int rematch = NO_REQUEST;

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

    /**
     * Requests that the <code>rematch</code> field be set to the
     * specified value. The local value will be updated immediately and an
     * event will be propagated through the system to notify all listeners
     * that the attribute did change. Proxied copies of this object (on
     * clients) will apply the value change when they received the
     * attribute changed notification.
     */
    @Generated(value={"com.threerings.presents.tools.GenDObjectTask"})
    public void setRematch (int value)
    {
        int ovalue = this.rematch;
        requestAttributeChange(
            REMATCH, Integer.valueOf(value), Integer.valueOf(ovalue));
        this.rematch = value;
    }
    // AUTO-GENERATED: METHODS END
}
