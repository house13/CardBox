package com.hextilla.cardbox.client;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.Icon;
import javax.swing.JLabel;

import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.server.CardBoxGameObject;
import com.hextilla.cardbox.util.CardBoxContext;
import com.samskivert.swing.MultiLineLabel;
import com.samskivert.swing.ShapeIcon;
import com.samskivert.swing.util.SwingUtil;
import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.PlaceObject;
import com.threerings.parlor.turn.client.TurnDisplay;
import com.threerings.parlor.turn.data.TurnGameObject;
import com.threerings.presents.dobj.AttributeChangeListener;
import com.threerings.presents.dobj.AttributeChangedEvent;
import com.threerings.presents.dobj.EntryAddedEvent;
import com.threerings.presents.dobj.EntryRemovedEvent;
import com.threerings.presents.dobj.EntryUpdatedEvent;
import com.threerings.util.Name;
import static com.hextilla.cardbox.lobby.Log.log;

public class CardBoxScorePanel extends TurnDisplay implements AttributeChangeListener, PlaceView
{
	public CardBoxScorePanel(CardBoxContext ctx)
	{
		super();
		
		_player1Score = new MultiLineLabel("0");
		_player2Score = new MultiLineLabel("0");
		
		// Set winner/draw text
		setWinnerText(ctx.xlate("hextilla", "m.winner"));
		setDrawText(ctx.xlate("hextilla", "m.draw"));		
		
		// Create the turn icon
        Polygon triangle = new Polygon(new int[] { 0, 12, 0 },
		                new int[] { 0, 6, 12 }, 3);
		setTurnIcon(new ShapeIcon(triangle, Color.DARK_GRAY, Color.BLACK));		
		
		// Create the player icon
		Ellipse2D lips = new Ellipse2D.Float(0, 0, 12, 12);
		Polygon hexagon = new Polygon(new int[] { 8, 22, 30, 22, 8, 0 },
		new int[] { 0, 0, 12, 24, 24, 12 }, 6);
		
		setPlayerIcons(new Icon[] {
		new ShapeIcon(hexagon, new Color(255, 255, 30), Color.DARK_GRAY),
		new ShapeIcon(hexagon, new Color(255, 0, 0), Color.DARK_GRAY) });
	}
	
	@Override
    public void attributeChanged (AttributeChangedEvent event)
    {
	    // Player 1 score was updated
	    if (event.getName().equals(CardBoxGameObject.PLAYER1_SCORE)) {
	    	_player1Score.setText("" + _gameobj.player1Score);
	    	return;
	    }
	    
	    // Player 2 score was updated
	    else if (event.getName().equals(CardBoxGameObject.PLAYER2_SCORE)) {
	    	_player2Score.setText("" + _gameobj.player2Score);
	    	return;
	    }
	    
	    super.attributeChanged(event);
    }

    // from interface PlaceView
    public void willEnterPlace (PlaceObject plobj)
    {
    	_turnObj = (TurnGameObject) plobj;
    	_gameobj = (CardBoxGameObject)plobj;
        _gameobj.addListener(this);
        createList();
    }

    // from interface PlaceView
    @Override 
    public void didLeavePlace (PlaceObject plobj)
    {
    	_gameobj.removeListener(this);
        _gameobj = null;
        _turnObj = null;
        removeAll();
    }
    
	// Overwritten to display our own turn display with a score
    @Override 
    protected void createList ()
    {
        removeAll();
        _labels.clear();
        		
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);  
        
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);        

        boolean[] winners = _gameobj.winners;
        Name holder = _turnObj.getTurnHolder();
        
        // Groupings
        ParallelGroup parNameGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        ParallelGroup parTurnIconGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        ParallelGroup parScoreGroup = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
        
        // Vertical layout group (sequential group of parallel name/icon pairs
        SequentialGroup nameIconPairs = layout.createSequentialGroup();
        
        // Loop through the players
        Name[] names = _turnObj.getPlayers();        
        for (int i=0; i < names.length; i++) {
            if (names[i] == null) continue;

            ParallelGroup nameIconPair = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
            
            // Set winner/draw text or the turn icon
            JLabel iconLabel = new JLabel();
            if (winners == null) {
                if (names[i].equals(holder)) {
                    iconLabel.setIcon(_turnIcon);
                }
            } else if (_gameobj.isDraw()) {
                iconLabel.setText(_drawText);
            } else if (winners[i]) {
                iconLabel.setText(_winnerText);
            }
            iconLabel.setForeground(Color.BLACK);
            _labels.put(names[i], iconLabel);
            parTurnIconGroup.addComponent(iconLabel);
            nameIconPair.addComponent(iconLabel);
            
            // Fix up the name to friendly/stranger version based on the type
            JLabel label = null;
        	if (names[i] instanceof CardBoxName){       	
	        	if (_gameobj.gameMode.equals("friendly") || _gameobj.gameMode.equals("ai")){
	                label = new JLabel(((CardBoxName)names[i]).getFriendlyName().toString());
	            } else if (_gameobj.gameMode.equals("stranger")){            	
	                label = new JLabel(((CardBoxName)names[i]).getStrangerName().toString());
	            }
	        	label = new JLabel(names[i].toString());
        	} 
        	else
        	{
        		 label = new JLabel(names[i].toString());
        	}
        	        	
        	// Set the player icons
            if (_playerIcons != null) {
                label.setIcon(_playerIcons[i]);
            }                                   
            parNameGroup.addComponent(label);
            nameIconPair.addComponent(label);
            
            // Add the score if possible
            switch (i){
            case 0:
            	nameIconPair.addComponent(_player1Score);
            	parScoreGroup.addComponent(_player1Score);
            	break;
            case 1:
            	nameIconPair.addComponent(_player2Score);
            	parScoreGroup.addComponent(_player2Score);
            	break;
            default:
            	// TODO: add score support for variable number of players
            	break;
            }
            
            
            nameIconPairs.addGroup(nameIconPair);
        }
        
        // Add the groups to the component
        layout.setHorizontalGroup(layout.createSequentialGroup()
        		.addGroup(parTurnIconGroup)
        		.addGroup(parNameGroup)
        		.addGroup(parScoreGroup));
        
        layout.setVerticalGroup(nameIconPairs);

        // Redraw the display
        SwingUtil.refresh(this);     
    }    
	
	// Labels for which show the score
    MultiLineLabel _player1Score;
    MultiLineLabel _player2Score;

    /** A reference to our game object. */
    protected CardBoxGameObject _gameobj;    
}
