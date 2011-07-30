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

package com.hextilla.cardbox.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import java.security.CodeSource;
import java.security.PermissionCollection;

import static com.hextilla.cardbox.Log.log;

/**
 * Provides class loading and appropriate permissions for CardBox games.
 */
public class CardBoxClassLoader extends URLClassLoader
{
    public CardBoxClassLoader (URL[] sources)
    {
        super(sources, CardBoxClassLoader.class.getClassLoader());
        _sources = sources;
        _lastModified = computeLastModified();
    }

    /**
     * Returns true if none of the jar files referenced by this class
     * loader have changed since it was first created, false otherwise.
     */
    public boolean isUpToDate ()
    {
        long[] curModified = computeLastModified();
        for (int ii = 0; ii < curModified.length; ii++) {
            if (curModified[ii] > _lastModified[ii]) {
                log.info(_sources[ii] + " has changed.");
                return false;
            }
        }
        return true;
    }

    // documentation inherited
    @Override
    protected PermissionCollection getPermissions (CodeSource codesource)
    {
        PermissionCollection perms = super.getPermissions(codesource);
        // TODO: various permissions magic
        return perms;
    }

    /** Looks up the last modified time for all of our source jar files. */
    protected long[] computeLastModified ()
    {
        long[] stamps = new long[_sources.length];
        for (int ii = 0; ii < _sources.length; ii++) {
            URL source = _sources[ii];
            // these should all be file URLs but we'll be safe
            if (!source.getProtocol().equals("file")) {
                log.warning("Can't check up-to-dateness of '" + source + "'.");
                continue;
            }
            stamps[ii] = new File(source.getPath()).lastModified();
        }
        return stamps;
    }

    /** The URLs from which we get our jar files. */
    protected URL[] _sources;

    /** The last modified times of our jar files at the time we were
     * created. */
    protected long[] _lastModified;
}
