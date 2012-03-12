package com.hextilla.cardbox.lobby.matchmaking;

import static com.hextilla.cardbox.lobby.Log.log;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import com.hextilla.cardbox.client.CardBoxButton;
import com.hextilla.cardbox.data.CardBoxGameConfig;
import com.hextilla.cardbox.data.TableMatchConfig;
import com.hextilla.cardbox.lobby.data.LobbyCodes;
import com.hextilla.cardbox.lobby.table.TableItem;
import com.hextilla.cardbox.util.CardBoxContext;
import com.threerings.crowd.data.PlaceObject;
import com.threerings.parlor.client.SeatednessObserver;
import com.threerings.parlor.client.TableDirector;
import com.threerings.parlor.client.TableObserver;
import com.threerings.parlor.data.Table;
import com.threerings.parlor.data.TableConfig;
import com.threerings.parlor.game.data.GameAI;
import com.threerings.util.MessageBundle;

public class ComputerOpponentView extends JPanel
	    implements TableObserver, SeatednessObserver
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
	        _msgs = _ctx.getMessageManager().getBundle(LobbyCodes.LOBBY_MSGS);

	        // create our table director
	        _tdtr = new TableDirector(ctx, "aiTableSet", this);

	        // add ourselves as a seatedness observer
	        _tdtr.addSeatednessObserver(this);
	        
	        _playButton = new CardBoxButton(_msgs.xlate("m.ai"));
	        
	        // Different AI radio buttons, set the difficulty level
	        JRadioButton _randomButton = new JRadioButton("Easy", true);
	        _randomButton.setOpaque(false);
	        _randomButton.addActionListener(new ActionListener() {	
				public void actionPerformed(ActionEvent arg0) {
					_difficultyLevel = 0;
				}
			});
	        
	        JRadioButton _aggressiveButton = new JRadioButton("Hard");
	        _aggressiveButton.setOpaque(false);
	        _aggressiveButton.addActionListener(new ActionListener() {	
				public void actionPerformed(ActionEvent arg0) {
					_difficultyLevel = 1;
				}
			});
	        
	        JRadioButton _defensiveButton = new JRadioButton("Medium");
	        _defensiveButton.setOpaque(false);
	        _defensiveButton.addActionListener(new ActionListener() {	
				public void actionPerformed(ActionEvent arg0) {
					_difficultyLevel = 2;
				}
			});	 
	        
	        // Group the radio buttons
	        ButtonGroup buttonGroup = new ButtonGroup();
	        buttonGroup.add(_randomButton);
	        buttonGroup.add(_defensiveButton);
	        buttonGroup.add(_aggressiveButton);
	        
	        // Add the buttons and such to the pane
	        GroupLayout layout = new GroupLayout(this);        
	        this.setLayout(layout);  
	        
	        // Horizontal Grouping
	        layout.setHorizontalGroup(
	        		layout.createParallelGroup(GroupLayout.Alignment.CENTER, true)
	        			.addComponent(_playButton)
	        			.addGroup(layout.createSequentialGroup()
	        					.addComponent(_randomButton)
	        					.addComponent(_defensiveButton)
	        					.addComponent(_aggressiveButton))
		    		);    
	        
	        // Vertical Grouping
	        layout.setVerticalGroup(
	        		layout.createSequentialGroup()
        			.addComponent(_playButton)
        			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER, true)
        					.addComponent(_randomButton)
        					.addComponent(_defensiveButton)
        					.addComponent(_aggressiveButton))
	    		);        
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
	    public void startAIMatch ()
	    {
	        // the create table button was clicked. use the game config as configured by the
	        // configurator to create a table
	        CardBoxGameConfig config = _config;
	        
	        //Add the AI player
	        GameAI ai = new GameAI();
	        ai.skill = _difficultyLevel;
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
	    	_playButton.setEnabled(!isSeated);
	    }

	    /**
	     * Fetches the table item component associated with the specified table id.
	     */
	    protected TableItem getTableItem (int tableId)
	    {
	        return null;
	    }
	    
	    public void addActionListener(ActionListener listener)
	    {
	    	_playButton.addActionListener(listener);
	    }

	    /** A reference to the client context. */
	    protected CardBoxContext _ctx;
	    
	    /** Our translation messages. */
	    protected MessageBundle _msgs;

	    /** The configuration for the game that we're match-making. */
	    protected CardBoxGameConfig _config;

	    /** A reference to our table director. */
	    protected TableDirector _tdtr;

	    /** Our create table button. */
	    public JButton _playButton;

	    /** Our number of players indicator. */
	    protected JLabel _pcount;
	    
	    // Play Button 
	    protected static String SOLOPLAY_BUTTON_TEXT = "Play by Yourself";	   
	    
	    // The AI difficulty level
	    protected static int _difficultyLevel = 0;
	    
	}
