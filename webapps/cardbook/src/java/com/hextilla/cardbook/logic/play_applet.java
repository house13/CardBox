package com.hextilla.cardbook.logic;

import javax.servlet.http.HttpServletRequest;

import com.hextilla.cardbook.CardbookApp;
import com.hextilla.cardbox.server.CardBoxConfig;
import com.hextilla.cardbox.server.persist.FBUserRecord;
import com.hextilla.cardbox.server.persist.GameRecord;
import com.samskivert.servlet.util.FriendlyException;
import com.samskivert.velocity.InvocationContext;
import com.threerings.presents.server.InvocationException;

import static com.hextilla.cardbook.Log.log;

/**
 * Handles the logic behind creating and managing a game's metadata.
 */
public class play_applet extends UserLogic
{
    // documentation inherited
    public void invoke (InvocationContext ctx, CardbookApp app, FBUserRecord user)
        throws Exception
    {
        HttpServletRequest req = ctx.getRequest();
        int gameId = CardBoxConfig.getGameId();
        GameRecord game = app.getCardBoxRepository().loadGame(gameId);
        if (game == null) {
            throw new FriendlyException("error.no_such_game");
        }
        ctx.put("game", game);
        ctx.put("players", app.getCardBoxRepository().getOnlineCount(gameId));
        try {
            ctx.put("single_player", game.parseGameDefinition().isSinglePlayerPlayable());
        } catch (InvocationException ie) {
            String errmsg = (ie.getCause() == null) ? ie.getMessage() : ie.getCause().getMessage();
            log.warning("Failed to parse gamedef [game=" + game.which() +
                            ", error=" + errmsg + "].");
        }
        ctx.put("session_id", app.getUserManager().getSession(req));
        ctx.put("port", CardBoxConfig.getServerPort());
        ctx.put("resource_url", CardBoxConfig.getResourceURL());
    }
}