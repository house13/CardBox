//
// Atlantis - A tile laying game for the Game Gardens platform
// http://github.com/threerings/game-gardens/blob/master/projects/games/atlantis/LICENSE

package com.samskivert.atlanti.client;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import com.samskivert.swing.Controller;
import com.samskivert.swing.ControllerProvider;
import com.samskivert.swing.HGroupLayout;
import com.samskivert.swing.MultiLineLabel;
import com.samskivert.swing.VGroupLayout;
import com.samskivert.swing.util.SwingUtil;

import com.threerings.media.SafeScrollPane;
import com.threerings.media.image.ImageManager;
import com.threerings.media.tile.TileManager;
import com.threerings.util.MessageBundle;

import com.threerings.crowd.data.PlaceObject;
import com.threerings.crowd.client.PlaceView;

import com.hextilla.cardbox.client.ChatPanel;
import com.hextilla.cardbox.client.OccupantList;
import com.hextilla.cardbox.client.CardBoxUI;
import com.hextilla.cardbox.util.CardBoxContext;

import com.samskivert.atlanti.data.AtlantiCodes;
import com.samskivert.atlanti.data.AtlantiTile;
import com.samskivert.atlanti.util.PiecenUtil;

import static com.samskivert.atlanti.Log.log;

/**
 * The top-level user interface component for the game display.
 */
public class AtlantiPanel extends JPanel
    implements PlaceView, ControllerProvider, AtlantiCodes
{
    /** A reference to the board that is accessible to the controller. */
    public AtlantiBoardView board;

    /** A reference to our _noplace button. */
    public JButton noplace;

    /**
     * Constructs a new game display.
     */
    public AtlantiPanel (CardBoxContext ctx, AtlantiController controller)
    {
        // give ourselves a wee bit of a border
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        HGroupLayout gl = new HGroupLayout(
            HGroupLayout.STRETCH, 10, HGroupLayout.CENTER);
        gl.setOffAxisPolicy(HGroupLayout.STRETCH);
        setLayout(gl);

        // create the board
        board = new AtlantiBoardView(controller);
        board.setOpaque(false);

        // create a scroll area to contain the board
        SafeScrollPane scrolly = new SafeScrollPane(board);
        scrolly.getViewport().setBackground(CardBoxUI.LIGHT_BLUE);
        add(scrolly);

        // create our side panel
        VGroupLayout sgl = new VGroupLayout(VGroupLayout.STRETCH);
        sgl.setOffAxisPolicy(VGroupLayout.STRETCH);
        sgl.setJustification(VGroupLayout.TOP);
        JPanel sidePanel = new JPanel(sgl);

        MessageBundle msgs =
            ctx.getMessageManager().getBundle(ATLANTI_MESSAGE_BUNDLE);

        // add a big fat label because we love it!
        MultiLineLabel vlabel = new MultiLineLabel(msgs.get("m.title"));
        vlabel.setFont(CardBoxUI.fancyFont);
        sidePanel.add(vlabel, VGroupLayout.FIXED);

        // add a player info view to the side panel
        sidePanel.add(new JLabel(msgs.get("m.scores")), VGroupLayout.FIXED);
        sidePanel.add(new PlayerInfoView(), VGroupLayout.FIXED);

        // add a turn indicator to the side panel
        sidePanel.add(new JLabel(msgs.get("m.turn")), VGroupLayout.FIXED);
        sidePanel.add(new TurnIndicatorView(), VGroupLayout.FIXED);

        // add a "place nothing" button
        noplace = new JButton(msgs.get("m.place_nothing"));
        noplace.setEnabled(false);
        noplace.setActionCommand("placeNothing");
        noplace.addActionListener(Controller.DISPATCHER);
        sidePanel.add(noplace, VGroupLayout.FIXED);

        // de-opaquify everything before we add the chat box
        SwingUtil.setOpaque(sidePanel, false);
        setOpaque(true);
        setBackground(new Color(0xDAEB9C));

        JTabbedPane pane = new JTabbedPane();
        pane.setBackground(new Color(0xDAEB9C));
        sidePanel.add(pane);

        // add a chat box
        ChatPanel chat = new ChatPanel(ctx);
        chat.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        pane.addTab(msgs.get("m.chat_header"), chat);

        // add a room occupants list
        OccupantList occs = new OccupantList(ctx);
        occs.setBorder(BorderFactory.createCompoundBorder(
                           BorderFactory.createEmptyBorder(2, 2, 2, 2),
                           occs.getBorder()));
        occs.setBackground(CardBoxUI.LIGHT_BLUE);
        pane.addTab(msgs.get("m.who_header"), occs);

        // add a "back" button
        JButton back = new JButton(msgs.get("m.back_to_lobby"));
        back.setActionCommand("backToLobby");
        back.addActionListener(Controller.DISPATCHER);
        sidePanel.add(HGroupLayout.makeButtonBox(HGroupLayout.RIGHT, back),
                      VGroupLayout.FIXED);

        // add our side panel to the main display
        add(sidePanel, HGroupLayout.FIXED);

        // we'll need these later
        _controller = controller;
        _ctx = ctx;
    }

    @Override
    public void addNotify ()
    {
        super.addNotify();

        // we can't create our image manager until we have access to our containing frame
        JRootPane rpane = getRootPane();
        ImageManager imgr = new ImageManager(_ctx.getResourceManager(), rpane);
        TileManager tmgr = new TileManager(imgr);
        AtlantiTile.setManagers(imgr, tmgr);
        PiecenUtil.init(tmgr);
    }

    // documentation inherited
    public void willEnterPlace (PlaceObject plobj)
    {
        log.info("Panel entered place.");
    }

    // documentation inherited
    public void didLeavePlace (PlaceObject plobj)
    {
        log.info("Panel left place.");
    }

    // documentation inherited
    public Controller getController ()
    {
        return _controller;
    }

    /** Provides access to needed services. */
    protected CardBoxContext _ctx;

    /** A reference to our controller that we need to implement the {@link
     * ControllerProvider} interface. */
    protected AtlantiController _controller;
}
