package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import javax.swing.Timer;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Controller;
import asteroids.game.Participant;
import static asteroids.game.Constants.*;

/**
 * Represents an AlienShip
 * 
 * @author Jake Johnson
 */
public class AlienShip extends Participant implements AsteroidDestroyer, ShipDestroyer
{
    /** Outline of the AlienShip */
    private Shape outline;

    /** Size of the AlienShip */
    private int size;

    /** Timer for AlienShip shots */
    private Timer alienShotTimer;
    
    /** Timer for Change in AlienMovement */
    private Timer alienMovementTimer;

    /** Controller for the AlienShip */
    private Controller controller;
    
    /** Number for whether AlienShip Moves Right = 0 or left = 1 */
    private int rightOrLeft;
    
    /**
     * Creates an AlienShip of size Small = 0 or Medium = 1. Throws IllegalArgumentException if size is invalid.
     * 
     * @param size
     */
    public AlienShip (int size, Controller controller)
    {
        // Ensure argument is valid
        if (size < 0 || size > 1)
        {
            throw (new IllegalArgumentException("AlienShip Size is invalid"));
        }
        
        rightOrLeft = RANDOM.nextInt(2);
        setPosition(SIZE + 5, RANDOM.nextInt(400) + 150);
        setDirection(-Math.PI / 2);
        setVelocity(7, 2 * ALIENSHIP_BASE_DIRECTION * rightOrLeft);
        

        // Assign Vars
        this.size = size;
        this.controller = controller;
        this.createOutline();

        // AlienShip has a 3 second delay between firing.
        this.alienShotTimer = new Timer(3000, this.controller);
        
        // AlienMovement Timer changes course every time this changes
        this.alienMovementTimer = new Timer(1000, this.controller);
    }

    /**
     * Creates the outline of the AlienShip with its respective size
     */
    private void createOutline ()
    {
        Double[] x;
        Double[] y;

        x = new Double[] { 7.0, -7.0, -10.0, 10.0 };
        y = new Double[] { -5.0, -5.0, 5.0, 5.0 };
        Path2D.Double poly = createFigure(x, y);

        x = new Double[] { 10.0, -10.0, -22.0, 22.0 };
        y = new Double[] { 5.0, 5.0, 17.0, 17.0 };
        poly.append(createFigure(x, y), false);

        x = new Double[] { 22.0, -22.0, -10.0, 10.0 };
        y = new Double[] { 17.0, 17.0, 27.0, 27.0 };
        poly.append(createFigure(x, y), false);
        
        double scale = ALIENSHIP_SCALE[size];
        poly.transform(AffineTransform.getScaleInstance(scale, scale));

        this.outline = poly;
    }

    /**
     * Draws a 4-Sided figure with values
     * 
     * @params x values, y values
     * @return Path2D.Double
     */
    private static Path2D.Double createFigure (Double[] x, Double[] y)
    {
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(x[0], y[0]);
        poly.lineTo(x[1], y[1]);
        poly.lineTo(x[2], y[2]);
        poly.lineTo(x[3], y[3]);
        poly.closePath();

        return poly;
    }
    
    /**
     * Change movement
     */
    public void changeMovement ()
    {
        int randint = RANDOM.nextInt(3) - 1;
        
        setVelocity(7, rightOrLeft * ALIENSHIP_BASE_DIRECTION + randint);
    }
    
    /**
     * Get the size of the AlienShip
     * 
     * @return int
     */
    public int getAlienShipSize ()
    {
        return this.size;
    }

    /**
     * Returns the outline of the AlienShip
     * 
     * @return Shape
     */
    @Override
    protected Shape getOutline ()
    {
        return this.outline;
    }

    /**
     * Returns the timer of the AlienShip shooting
     * 
     * @return Timer
     */
    public Timer getAlienShotTimer ()
    {
        return this.alienShotTimer;
    }
    
    /**
     * Returns the timer for the movement of the AlienShip
     * 
     * @return Timer
     */
    public Timer getAlienMovementTimer ()
    {
        return this.alienMovementTimer;
    }

    /**
     * Called when alien ship collides with another object
     */
    @Override
    public void collidedWith (Participant p)
    {

    }
}
