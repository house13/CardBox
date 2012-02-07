//
// ToyBox library - framework for matchmaking networked games
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

package com.hextilla.cardbook;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.samskivert.depot.PersistenceContext;
import com.samskivert.io.PersistenceException;
import com.samskivert.jdbc.ConnectionProvider;
import com.samskivert.jdbc.StaticConnectionProvider;

import com.samskivert.velocity.Application;

import com.samskivert.util.BasicRunQueue;
import com.samskivert.util.ConfigUtil;
import com.samskivert.util.PropertiesUtil;
import com.samskivert.util.ServiceUnavailableException;

import com.hextilla.cardbook.auth.FBUserManager;
import com.hextilla.cardbox.server.CardBoxConfig;
import com.hextilla.cardbox.server.persist.CardBoxRepository;
import com.hextilla.cardbox.server.persist.FBUserRepository;

import static com.hextilla.cardbook.Log.log;

/**
 * Contains references to application-wide resources (like the database
 * repository) and handles initialization and cleanup for those resources.
 */
public class CardbookApp extends Application {
	/** Returns the connection provider in use by this application. */
	public ConnectionProvider getConnectionProvider() {
		return _conprov;
	}

	/** Returns the user manager in use by this application. */
	public FBUserManager getUserManager() {
		return _usermgr;
	}

	/** Provides access to the toybox repository. */
	public CardBoxRepository getCardBoxRepository() {
		return _cbrepo;
	}

	/**
	 * Looks up a configuration property in our <code>gardens.properties</code>
	 * application configuration file.
	 */
	public String getProperty(String key) {
		return _config.getProperty(key);
	}

	// documentation inherited
	protected void configureVelocity(ServletConfig config, Properties props) {
		String ipath = config.getServletContext().getRealPath("/");
		if (ipath != null && ipath.indexOf("cache") == -1
				&& new File(ipath).exists()) {
			props.setProperty("file.resource.loader.path", ipath);
			log.info("Velocity loading directly from " + ipath + ".");
		}
	}

	/** Initialize the user management application. */
	protected void willInit(ServletConfig config) {
		super.willInit(config);

		try {
			// load up our configuration properties
			_config = CardBoxConfig.config.getSubProperties("web");

			// create a static connection provider
			_conprov = new StaticConnectionProvider(
					CardBoxConfig.getJDBCConfig());

			// configure our persistence objects
			PersistenceContext gamectx = new PersistenceContext();
			gamectx.init(CardBoxRepository.GAME_DB_IDENT, _conprov, null);
			_cbrepo = new CardBoxRepository(gamectx);
			gamectx.initializeRepositories(true);
			
			PersistenceContext userctx = new PersistenceContext();
			userctx.init(FBUserRepository.USER_DB_IDENT, _conprov, null);
			_usermgr = new FBUserManager(userctx);
			userctx.initializeRepositories(true);

			// load up our build stamp so that we can report it
			String bstamp = PropertiesUtil.loadAndGet("build.properties",
					"build.time");
			log.info("Game Gardens application initialized [built=" + bstamp
					+ "].");

		} catch (Throwable t) {
			log.warning("Error initializing application", t);
		}
	}

	/** Shut down the user management application. */
	public void shutdown() {
		try {
			_usermgr.shutdown();
			log.info("Hextilla application shutdown.");

		} catch (Throwable t) {
			log.warning("Error shutting down repository", t);
		}
	}

	/** A reference to our user manager. */
	protected FBUserManager _usermgr;

	/** A reference to our connection provider. */
	protected ConnectionProvider _conprov;

	/** Our repository of game information. */
	protected CardBoxRepository _cbrepo;

	/** Our application configuration information. */
	protected Properties _config;

	/**
	 * Used to configure velocity to load files right out of the development
	 * directory.
	 */
	protected static final String VEL_RELOAD_KEY = "web.velocity_file_loader";
}
