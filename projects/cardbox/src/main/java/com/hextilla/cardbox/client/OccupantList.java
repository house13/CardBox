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

package com.hextilla.cardbox.client;

import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;

import com.threerings.util.Name;

import com.threerings.crowd.client.OccupantObserver;
import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.OccupantInfo;
import com.threerings.crowd.data.PlaceObject;
import com.threerings.crowd.util.CrowdContext;

/**
 * The occupant list displays the list of users that are in a particular
 * place.
 */
public class OccupantList extends JList
    implements PlaceView, OccupantObserver
{
    /**
     * Constructs an occupant list with the supplied context which it will
     * use to register itself with the necessary managers.
     */
    public OccupantList (CrowdContext ctx)
    {
        // set up our list model
        _model = new DefaultListModel();
        setModel(_model);

        setBorder(BorderFactory.createEtchedBorder());

        // keep our context around for later
        _ctx = ctx;

        // register ourselves as an occupant observer
        _ctx.getOccupantDirector().addOccupantObserver(this);
    }

    // documentation inherited
    public void willEnterPlace (PlaceObject plobj)
    {
        // add all of the occupants of the place to our list
        for (OccupantInfo info : plobj.occupantInfo) {
            _model.addElement(info.username);
        }
    }

    // documentation inherited
    public void didLeavePlace (PlaceObject plobj)
    {
        // clear out our occupant entries
        _model.clear();
    }

    // documentation inherited
    public void occupantEntered (OccupantInfo info)
    {
        // simply add this user to our list
        _model.addElement(info.username);
    }

    // documentation inherited
    public void occupantLeft (OccupantInfo info)
    {
        // remove this occupant from our list
        _model.removeElement(info.username);
    }

    // documentation inherited
    public void occupantUpdated (OccupantInfo oinfo, OccupantInfo info)
    {
        int idx = _model.indexOf(getName(oinfo.username, oinfo.status));
        if (idx >= 0) {
            _model.setElementAt(getName(info.username, info.status), idx);
        }
    }

    // documentation inherited
    @Override
    public Dimension getPreferredSize ()
    {
        Dimension d = super.getPreferredSize();
        d.width = Math.min(Math.max(d.width, 100), 150);
        return d;
    }

    protected Name getName (Name username, int status)
    {
        switch (status) {
        case OccupantInfo.IDLE:
            return new Name("(" + username + ")");
        case OccupantInfo.DISCONNECTED:
            return new Name("{" + username + "}");
        default:
        case OccupantInfo.ACTIVE:
            return username;
        }
    }

    /** Our client context. */
    protected CrowdContext _ctx;

    /** A list model that provides a vector interface. */
    protected DefaultListModel _model;
}
