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

package com.hextilla.cardbox.lobby.client;

import com.threerings.getdown.data.Resource;
import com.threerings.getdown.net.Downloader;

import com.threerings.crowd.client.PlaceController;
import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.PlaceConfig;
import com.threerings.crowd.data.PlaceObject;
import com.threerings.crowd.util.CrowdContext;

import com.hextilla.cardbox.lobby.data.LobbyConfig;
import com.hextilla.cardbox.util.CardBoxContext;

import static com.hextilla.cardbox.lobby.Log.log;

/**
 * Handles the client side of the CardBox match-making interface.
 */
public class LobbyController extends PlaceController
{
    // documentation inherited
    @Override
    public void init (CrowdContext ctx, PlaceConfig config)
    {
        super.init(ctx, config);

        // cast our references
        _ctx = (CardBoxContext)ctx;
        _config = (LobbyConfig)config;
    }

    // documentation inherited
    @Override
    public void willEnterPlace (PlaceObject plobj)
    {
        super.willEnterPlace(plobj);     

        // let the cardbox director know that we're in
        _ctx.getCardBoxDirector().enteredLobby(_config);

        // TODO: hold off on creating the friends list until;
        // resources are downloaded (indeed show the download progress in
        // that same location)

        // have the cardbox director download this game's jar files
        Downloader.Observer obs = new Downloader.Observer() {
            public void resolvingDownloads () {
                log.info("Resolving downloads...");
                // TODO: show download progress
            }
            public boolean downloadProgress (int percent, long remaining) {
                log.info("Download progress: " + percent);             
                if (percent == 100) {
                	_panel.loadGamePanel(_config);
                } else {
                   _panel.setDownloadProgress(percent);
                }
                return true;
            }
            public void downloadFailed (Resource rsrc, Exception e) {
                log.info("Download failed [rsrc=" + rsrc + ", e=" + e + "].");
                // TODO: report warning
            }
        };
        _ctx.getCardBoxDirector().resolveResources(
            _config.getGameId(), _config.getGameDefinition(), obs);
    }

    // documentation inherited
    @Override
    protected PlaceView createPlaceView (CrowdContext ctx)
    {
        return (_panel = new LobbyPanel((CardBoxContext)ctx));
    }

    protected CardBoxContext _ctx;
    protected LobbyConfig _config;
    protected LobbyPanel _panel;
}
