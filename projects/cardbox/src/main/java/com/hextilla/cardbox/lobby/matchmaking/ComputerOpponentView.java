package com.hextilla.cardbox.lobby.matchmaking;

import static com.hextilla.cardbox.lobby.Log.log;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.hextilla.cardbox.data.TableMatchConfig;
import com.hextilla.cardbox.lobby.table.TableItem;
import com.hextilla.cardbox.util.CardBoxContext;
import com.samskivert.swing.HGroupLayout;
import com.samskivert.swing.VGroupLayout;
import com.threerings.crowd.data.PlaceObject;
import com.threerings.parlor.client.SeatednessObserver;
import com.threerings.parlor.client.TableDirector;
import com.threerings.parlor.client.TableObserver;
import com.threerings.parlor.data.Table;
import com.threerings.parlor.data.TableConfig;
import com.threerings.parlor.game.data.GameAI;

public class ComputerOpponentView extends JPanel
	    implements TableObserver, ActionListener, SeatednessObserver
	{
	    /**
	 * 
	 */
	private static final long serialVersionUID = -5893833850770210445L;

		/**
	     * Creates a new table list view, suitable for providing the user interface for table-style
	     * matchmaking in a table lobby.
	     */
	    public ComputerOpponentView (CardBoxContext ctx, CardBoxGameConfig config)
	    {
	        // keep track of these
	        _config = config;
	        _ctx = ctx;

	        // create our table director
	        _tdtr = new TableDirector(ctx, "aiTableSet", this);

	        // add ourselves as a seatedness observer
	        _tdtr.addSeatednessObserver(this);

	        // set up a layout manager
			/*HGroupLayout gl = new HGroupLayout(HGroupLayout.STRETCH);
			gl.setOffAxisPolicy(HGroupLayout.STRETCH);
			setLayout(gl);

	        // we have two lists of tables, one of tables being matchmade...
	        VGroupLayout pgl = new VGroupLayout(VGroupLayout.STRETCH);
	        pgl.setOffAxisPolicy(VGroupLayout.STRETCH);
	        pgl.setJustification(VGroupLayout.TOP);
	        JPanel panel = new JPanel(pgl);
	        panel.add(new JLabel("Computer Opponents"));
	        
	        
	        JPanel bbox = HGroupLayout.makeButtonBox(HGroupLayout.RIGHT);
	        bbox.add(_randomButton);
	        bbox.add(_aggressiveButton);
	        bbox.add(_defensiveButton);
	        panel.add(bbox, VGroupLayout.FIXED);

	        add(panel);*/
	        /*_randomButton = new JButton("Easy Opponent");
	        _randomButton.addActionListener(this);
	        _aggressiveButton = new JButton("Aggressive Opponent");
	        _defensiveButton = new JButton("Defensive Opponent");
	        
	        setLayout(new GridLayout(2, 1));
			JPanel top = new JPanel(new BorderLayout());
			JPanel bottom = new JPanel();			
			bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
			top.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));			
			bottom.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			top.add(new JLabel("Computer Opponents"));
			bottom.add(_randomButton);
			
			add(top);
			add(bottom);*/
	        
	        _randomButton = new JButton("Easy Opponent");
	        _randomButton.addActionListener(this);
	        _aggressiveButton = new JButton("Aggressive Opponent");
	        _defensiveButton = new JButton("Defensive Opponent");
	        
	        setLayout(new GridBagLayout());
		
	        GridBagConstraints c = new GridBagConstraints();  
	        c.weightx = 1;
	        c.weighty = 0.1;             
	        c.gridheight = 1;
	        c.fill = GridBagConstraints.HORIZONTAL;
	        c.insets = new Insets(0, 5, 0, 5);
	        c.gridx = 0;
	        c.gridy = 0;
	        c.gridwidth = 3;
	        add(new JLabel("Computer Opponents"), c);
	        
	        c.gridx = 0;
	        c.gridy = 1;
	        c.fill = GridBagConstraints.BOTH;
	        c.gridwidth = 1;
	        c.weighty = 0.9;
	        add(_randomButton, c);
	        c.gridx = 1;
	        c.gridy = 1;
	        add(_aggressiveButton, c);
	        c.gridx = 2;
	        c.gridy = 1;
	        add(_defensiveButton, c);
	    }

	    // documentation inherited
	    public void setPlace (PlaceObject place)
	    {
	        // pass the good word on to our table director
	        _tdtr.setTableObject(place);
	    }

	    // documentation inherited
	    public void leavePlace (PlaceObject place)
	    {
	        // pass the good word on to our table director
	        _tdtr.clearTableObject();
	    }

	    // documentation inherited
	    public void tableAdded (Table table)
	    {
	        log.info("Table added [table=" + table + "].");
	    }

	    // documentation inherited
	    public void tableUpdated (Table table)
	    {
	    }

	    // documentation inherited
	    public void tableRemoved (int tableId)
	    {
	    }

	    // documentation inherited
	    public void actionPerformed (ActionEvent event)
	    {
	        // the create table button was clicked. use the game config as configured by the
	        // configurator to create a table
	        CardBoxGameConfig config = _config;
	        
	        //Add the AI player
	        GameAI ai = new GameAI();
	        ai.skill = 0;
	        ai.personality = 0;
	        config.ais = new GameAI[1];
	        config.ais[0] = ai;
	        
	        TableConfig tconfig = new TableConfig();
	        tconfig.minimumPlayerCount = ((TableMatchConfig)config.getGameDefinition().match).minSeats;
	        tconfig.desiredPlayerCount = ((TableMatchConfig)config.getGameDefinition().match).maxSeats;
	        _tdtr.createTable(tconfig, config);
	    }

	    // documentation inherited
	    public void seatednessDidChange (boolean isSeated)
	    {
	        // update the create table button
	    	_randomButton.setEnabled(!isSeated);
	    	_aggressiveButton.setEnabled(!isSeated);
	    	_defensiveButton.setEnabled(!isSeated);
	    }

	    /**
	     * Fetches the table item component associated with the specified table id.
	     */
	    protected TableItem getTableItem (int tableId)
	    {
	        return null;
	    }

	    /** A reference to the client context. */
	    protected CardBoxContext _ctx;

	    /** The configuration for the game that we're match-making. */
	    protected CardBoxGameConfig _config;

	    /** A reference to our table director. */
	    protected TableDirector _tdtr;

	    /** Our create table button. */
	    protected JButton _randomButton;
	    protected JButton _aggressiveButton;
	    protected JButton _defensiveButton;

	    /** Our number of players indicator. */
	    protected JLabel _pcount;
	    
	}
