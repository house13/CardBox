//
// Atlantis - A tile laying game for the Game Gardens platform
// http://github.com/threerings/game-gardens/blob/master/projects/games/atlantis/LICENSE

package com.samskivert.atlanti.client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import com.samskivert.atlanti.data.AtlantiTile;
import com.samskivert.atlanti.data.TileCodes;

/**
 * Displays a single tile in a Swing component.
 */
public class TileLabel extends JComponent
    implements TileCodes
{
    /**
     * Configures the component to display the specified tile.
     *
     * @param tile a reference to the tile to display or null if no tile
     * should be displayed.
     */
    public void setTile (AtlantiTile tile)
    {
        _tile = tile;
        repaint();
    }

    // documentation inherited
    @Override
    public void paintComponent (Graphics g)
    {
        super.paintComponent(g);

        // simply paint the tile if we have one
        if (_tile != null) {
            _tile.paint((Graphics2D)g, 0, 0);
        }
    }

    // documentation inherited
    @Override
    public Dimension getPreferredSize ()
    {
        return new Dimension(TILE_WIDTH, TILE_HEIGHT);
    }

    /** The tile we are displaying. */
    protected AtlantiTile _tile;
}
