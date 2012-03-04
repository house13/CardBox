package com.hextilla.cardbox.client;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import com.hextilla.cardbox.facebook.CardBoxName;
import com.hextilla.cardbox.server.CardBoxGameObject;
import com.samskivert.swing.util.SwingUtil;
import com.threerings.parlor.turn.client.TurnDisplay;
import com.threerings.util.Name;

public class CardBoxTurnDisplay extends TurnDisplay {
	
    public CardBoxTurnDisplay ()
    {
    	super();
    }
    
	// Copy pasta of TurnDisplay. Need to override to take into account friendly/stranger names but otherwise it is the same
    @Override 
    protected void createList ()
    {
        removeAll();
        _labels.clear();

        GridBagLayout gridbag = new GridBagLayout();
        setLayout(gridbag);

        GridBagConstraints iconC = new GridBagConstraints();
        GridBagConstraints labelC = new GridBagConstraints();
        iconC.fill = labelC.fill = GridBagConstraints.BOTH;
        labelC.weightx = 1.0;
        labelC.insets.left = 10;
        labelC.gridwidth = GridBagConstraints.REMAINDER;

        Name[] names = _turnObj.getPlayers();
        CardBoxGameObject gameobj = (CardBoxGameObject)_turnObj;
        boolean[] winners = gameobj.winners;
        Name holder = _turnObj.getTurnHolder();
        for (int ii=0, jj=0; ii < names.length; ii++, jj++) {
            if (names[ii] == null) continue;

            JLabel iconLabel = new JLabel();
            if (winners == null) {
                if (names[ii].equals(holder)) {
                    iconLabel.setIcon(_turnIcon);
                }
            } else if (gameobj.isDraw()) {
                iconLabel.setText(_drawText);
            } else if (winners[ii]) {
                iconLabel.setText(_winnerText);
            }
            iconLabel.setForeground(Color.BLACK);
            _labels.put(names[ii], iconLabel);
            add(iconLabel, iconC);
            
            JLabel label = new JLabel(names[ii].toString());
            
            // Fix up the name
        	if (names[ii] instanceof CardBoxName){       	
	        	if (gameobj.gameMode.equals("friendly") || gameobj.gameMode.equals("ai")){
	                label = new JLabel(((CardBoxName)names[ii]).getFriendlyName().toString());
	            } else if (gameobj.gameMode.equals("stranger")){            	
	                label = new JLabel(((CardBoxName)names[ii]).getStrangerName().toString());
	            }
        	}
        	
            if (_playerIcons != null) {
                label.setIcon(_playerIcons[jj]);
            }
            add(label, labelC);
        }

        SwingUtil.refresh(this);
    }
}
