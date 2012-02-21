package com.hextilla.cardbox.client;

import com.threerings.presents.client.InvocationService;

/**
 * Provides access to CardBox card-related invocation services.
 */
public interface CardBoxCardService extends InvocationService
{
    /**
     * Issues a request for the cards associated with the
     * specified game.
     */
    public void getCards (int gameId, ResultListener rl);
}
