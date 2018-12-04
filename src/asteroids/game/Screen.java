package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.*;
import java.util.Iterator;
import javax.swing.*;

/**
 * The area of the display in which the game takes place.
 */
@SuppressWarnings("serial")
public class Screen extends JPanel
{
    /** Legend that is displayed across the screen */
    private String legend;

    /** Game controller */
    private Controller controller;

    /** Current Score */
    private String score;

    /** Current Level */
    private String level;

    /**
     * Creates an empty screen
     */
    public Screen (Controller controller)
    {
        this.controller = controller;
        legend = "";
        setPreferredSize(new Dimension(SIZE, SIZE));
        setMinimumSize(new Dimension(SIZE, SIZE));
        setBackground(Color.black);
        setForeground(Color.white);

        setFocusable(true);
    }

    /**
     * Set the legend
     */
    public void setLegend (String legend)
    {
        this.legend = legend;
    }

    /**
     * Set the score
     */
    public void setScore (int score)
    {
        this.score = "" + score;
    }

    /**
     * Set the level
     */
    public void setLevel (int level)
    {
        this.level = "" + level;
    }

    /**
     * Paint the participants onto this panel
     */
    @Override
    public void paintComponent (Graphics graphics)
    {
        // Use better resolution
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Do the default painting
        super.paintComponent(g);

        // Draw each participant in its proper place
        Iterator<Participant> iter = controller.getParticipants();
        while (iter.hasNext())
        {
            iter.next().draw(g);
        }

        // Draw the Label Legend
        drawLegend(g);

        // Draw Labels Score and Level If game has Begun
        if (this.score != null && this.level != null)
        {
            drawLevel(g);
            drawScore(g);
        }

    }

    /**
     * sets the font to what type of label. Input "Legend" to set large font type, or Input "Score"/"Level" to set a
     * smaller font type
     * 
     * @param type
     */
    private void setDesiredFont (String type)
    {
        if (type.equalsIgnoreCase("legend"))
        {
            setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 120));
        }
        else if (type.equalsIgnoreCase("score") || type.equalsIgnoreCase("level") || type.equalsIgnoreCase("scores"))
        {
            setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 60));
        }
        else
        {
            throw (new IllegalArgumentException("Font for " + type + " has not been set up yet."));
        }
    }

    /**
     * Draws text across the middle of the panel
     * 
     * @param Graphics object
     */
    private void drawLegend (Graphics g)
    {
        // Set the font and size
        setDesiredFont("legend");
        int size = g.getFontMetrics().stringWidth(legend);

        // Draw text
        g.drawString(legend, (SIZE - size) / 2, SIZE / 2);
    }

    /**
     * Draws the current score in the top left corner
     * 
     * @param Graphics object
     */
    private void drawScore (Graphics g)
    {
        // Set the font
        setDesiredFont("score");

        // Get really good consistent spacing from wall
        FontMetrics fm = g.getFontMetrics(g.getFont());
        Double xOffset = g.getFont().getStringBounds(this.score, fm.getFontRenderContext()).getMinX();

        // Draw text
        g.drawString(this.score, LABEL_HORIZONTAL_OFFSET + xOffset.intValue(), LABEL_VERTICAL_OFFSET * 2);
    }

    /**
     * Draws the current Level in the top right corner
     * 
     * @param Graphics object
     */
    private void drawLevel (Graphics g)
    {
        // Set the font
        setDesiredFont("level");

        // Get really good consistent spacing from wall
        FontMetrics fm = g.getFontMetrics(g.getFont());
        Double xOffset = g.getFont().getStringBounds(this.level, fm.getFontRenderContext()).getMaxX();

        // Draw text
        g.drawString(this.level, SIZE - LABEL_HORIZONTAL_OFFSET - xOffset.intValue(), LABEL_VERTICAL_OFFSET * 2);
    }
}
