package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.*;
import java.util.ArrayList;
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

    /** HighScores */
    private ArrayList<Integer> highScores;

    /** Legend Font */
    private Font legendFont = new Font(Font.SANS_SERIF, Font.PLAIN, 120);

    /** Score and Level Font */
    private Font scoreLevelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 60);

    // /** HighScores Font */
    // private Font highScoresFont = new Font(Font.SANS_SERIF)

    /**
     * Creates an empty screen
     */
    public Screen (Controller controller)
    {
        this.controller = controller;
        this.legend = "";
        this.highScores = null;

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
     * Set the high scores
     */
    public void setHighScores (ArrayList<Integer> highScores)
    {
        if (highScores != null)
        {
            this.highScores = highScores;
        }
        else
        {
            this.highScores = null;
        }
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
        setFont(legendFont);
        drawLegend(g);

        // Draw the highScores
        if (highScores != null)
        {
            setFont(scoreLevelFont);
            drawHighScores(g);
        }

        // Draw Labels Score and Level If game has Begun
        if (this.score != null && this.level != null)
        {
            setFont(scoreLevelFont);
            drawLevel(g);
            drawScore(g);
        }
    }

    /**
     * Draws text across the middle of the panel
     */
    private void drawLegend (Graphics g)
    {
        int size = g.getFontMetrics().stringWidth(legend);

        // Draw text
        g.drawString(legend, (SIZE - size) / 2, SIZE / 2);
    }

    /**
     * Draws the current score in the top left corner
     */
    private void drawScore (Graphics g)
    {
        // Get really good consistent spacing from wall
        FontMetrics fm = g.getFontMetrics(g.getFont());
        Double xOffset = g.getFont().getStringBounds(this.score, fm.getFontRenderContext()).getMinX();

        // Draw text
        g.drawString(this.score, LABEL_HORIZONTAL_OFFSET + xOffset.intValue(), LABEL_VERTICAL_OFFSET * 2);
    }

    /**
     * Draws the current Level in the top right corner
     */
    private void drawLevel (Graphics g)
    {
        // Get really good consistent spacing from wall
        FontMetrics fm = g.getFontMetrics(g.getFont());
        Double xOffset = g.getFont().getStringBounds(this.level, fm.getFontRenderContext()).getMaxX();

        // Draw text
        g.drawString(this.level, SIZE - LABEL_HORIZONTAL_OFFSET - xOffset.intValue(), LABEL_VERTICAL_OFFSET * 2);
    }

    /**
     * Draws the highScores underneath GameOver text
     */
    private void drawHighScores (Graphics g)
    {
        FontMetrics fm = g.getFontMetrics(g.getFont());
        Double xOffset = g.getFont().getStringBounds("High Scores", fm.getFontRenderContext()).getWidth();

        // Draw "High Scores"
        g.drawString("High Scores", (SIZE - xOffset.intValue()) / 2, SIZE / 2 + 100);

        // Get Spacing
        Integer firstScore = this.highScores.get(2);
        fm = g.getFontMetrics(g.getFont());
        xOffset = g.getFont().getStringBounds(firstScore.toString(), fm.getFontRenderContext()).getMaxX();

        // Draw each Score
        g.drawString(firstScore.toString(), (SIZE - xOffset.intValue()) / 2, SIZE / 2 + 170);
        g.drawString(this.highScores.get(1).toString(), (SIZE - xOffset.intValue()) / 2, SIZE / 2 + 220);
        g.drawString(this.highScores.get(0).toString(), (SIZE - xOffset.intValue()) / 2, SIZE / 2 + 270);
    }
}
