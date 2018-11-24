package asteroids.participants;

import static asteroids.game.Constants.*;
import asteroids.destroyers.OnscreenLabel;
import asteroids.game.Participant;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * represents Lives remaining
 * 
 * @author Jake Johnson
 */
public class Lives extends Participant implements OnscreenLabel
{
    /** Outline of the life object */
    private Shape outline;

    /** Center x-value of the life object */
    private int x;
    
    /** Center y-value of the life object */
    private int y = SIZE - (LABEL_VERTICAL_OFFSET + (LIFE_HEIGHT / 2));

    /**
     * Creates a picture of a ship representing 1 life
     */
    public Lives (int lifeNum)
    {
        // Assign outline of the life object
        this.outline = drawLifeOutline();

        // Set life object position
        this.x = LABEL_HORIZONTAL_OFFSET + LIFE_WIDTH + (lifeNum * SHIP_SEPARATION);
        setPosition(x, y);

        // Point life object UP
        setRotation(-Math.PI / 2);
    }

    /**
     * Draws a miniature version of the Ship
     * 
     * @return Shape
     */
    public Shape drawLifeOutline ()
    {
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(11, 0);
        poly.lineTo(-11, 6);
        poly.lineTo(-7, 5);
        poly.lineTo(-7, -5);
        poly.lineTo(-11, -6);
        poly.closePath();
        return poly;
    }

    /**
     * Returns the shape of the object
     * 
     * @return Shape
     */
    @Override
    protected Shape getOutline ()
    {
        return this.outline;
    }

    // Ignored
    @Override
    public void collidedWith (Participant p)
    {
    }
}
