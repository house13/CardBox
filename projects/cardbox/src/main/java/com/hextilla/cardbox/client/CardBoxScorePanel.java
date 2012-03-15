package com.hextilla.cardbox.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.Icon;
import javax.swing.JLabel;

import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.server.CardBoxGameObject;
import com.hextilla.cardbox.swing.ShapeLabel;
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
	}
	
	@Override
    public void attributeChanged (AttributeChangedEvent event)
    {
	    // Player 1 score was updated
	    if (event.getName().equals(CardBoxGameObject.PLAYER1_SCORE)) {
	    	((ShapeLabel) _playerIcons[0]).setText("" + _gameobj.player1Score);
	    	this.repaint();
	    	return;
	    }
	    
	    // Player 2 score was updated
	    else if (event.getName().equals(CardBoxGameObject.PLAYER2_SCORE)) {
	    	((ShapeLabel) _playerIcons[1]).setText("" + _gameobj.player2Score);
	    	this.repaint();
	    	return;
	    }
	    
	    super.attributeChanged(event);
    }
	
	// Set the win icon
	public void setWinnerIcon(Icon icon)
	{
		_winIcon = icon;
		setMinIconDim(icon);
	}
	
	// Set the draw icon
	public void setDrawIcon(Icon icon)
	{
		_drawIcon = icon;
		setMinIconDim(icon);
	}
	
	@Override
	public void setTurnIcon(Icon icon)
	{
		super.setTurnIcon(icon);
		setMinIconDim(icon);
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
                	System.out.println("TurnIcon");
                    iconLabel.setIcon(_turnIcon);                  
                }
            } else if (_gameobj.isDraw()) {
            	System.out.println("draw");
                iconLabel.setIcon(_drawIcon);
            } else if (winners[i]) {
            	System.out.println("win");
                iconLabel.setIcon(_winIcon);
            }
            iconLabel.setMinimumSize(_minIconDim);                                
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
                label.setMinimumSize(new Dimension(_playerIcons[i].getIconWidth(), _playerIcons[i].getIconHeight()+1));
            }                                   
            parNameGroup.addComponent(label);
            nameIconPair.addComponent(label);            
            
            nameIconPairs.addGroup(nameIconPair);
        }
        
        // Add the groups to the component
        layout.setHorizontalGroup(layout.createSequentialGroup()
        		.addGroup(parTurnIconGroup)
        		.addGroup(parNameGroup));
        
        layout.setVerticalGroup(nameIconPairs);

        // Redraw the display
        SwingUtil.refresh(this);     
    }    
    
    protected void setMinIconDim(Icon icon){
    	_minIconDim = new Dimension(Math.max(icon.getIconWidth(), _minIconDim.width),
    			Math.max(icon.getIconHeight(), _minIconDim.height)+1);
    }

    /** A reference to our game object. */
    protected CardBoxGameObject _gameobj;  
    
    protected Icon _winIcon = null;
    protected Icon _drawIcon = null;
    
    protected Dimension _minIconDim = new Dimension(0, 0);
}
