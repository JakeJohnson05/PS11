package asteroids.participants;

import static asteroids.game.Constants.RANDOM;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import asteroids.destroyers.OnscreenLabel;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

/**
 * Represents a Debris object
 * @author Jake Johnson
 */
public class Debris extends Participant implements OnscreenLabel
{
    /** The outline of the ship */
    private Shape outline;
    
    /** Diameter of a DebrisCircle */
    private final Double CIRCLE_DIAM = 1.0;
    
    /** length of a DebrisLine */
    private final Double LINE_LENGTH = 10.0;
    
    /** Minimum speed of Debris */
    private final Double MIN_SPEED = 1.0;
    
    /** Maximum speed of Debris */
    private final Double MAX_SPEED = 3.0;
    
    /** Duration until debris vanishes */
    private final int DURATION = 2000;
    
    /**
     * Creates Debris in the shape of either a dot/circle or a line
     * 
     * @param x, y, objectToCreate, controller
     */
    public Debris (double x, double y, String objectToCreate)
    {
        // Create this.outline
        this.outline = this.createOutline(objectToCreate);
        
        // Set position
        setPosition(x, y);
        
        // Set random orientation
        setRotation(RANDOM.nextDouble() * 2 * Math.PI);
        
        // Set velocity with Random Speed [MIN_SPEED, MAX_SPEED] and random direction
        double speed = (RANDOM.nextDouble() * (MAX_SPEED - MIN_SPEED)) + MIN_SPEED;
        setVelocity(speed, RANDOM.nextDouble() * Math.PI * 2.0);
        
        // Set Timer for debris to expire
        new ParticipantCountdownTimer(this, "expire", DURATION);
    }
    
    /**
     * Create an outline for the Debris
     * @param String dot||circle||line
     * @return Shape
     */
    private Shape createOutline (String object)
    {
        if (object.equalsIgnoreCase("dot") || object.equalsIgnoreCase("circle"))
        {
            return new Ellipse2D.Double(-CIRCLE_DIAM / 2.0, -CIRCLE_DIAM / 2.0, CIRCLE_DIAM, CIRCLE_DIAM);
        }
        else if (object.equalsIgnoreCase("line"))
        {
            return new Line2D.Double(-LINE_LENGTH / 2.0, 0, LINE_LENGTH / 2.0, 0);
        }
        else
        {
            throw (new IllegalArgumentException("Object: " + object + " is not a valid Debris shape"));
        }
    }

    /**
     * Get the Shape object of the Debris outline
     * @return Shape
     */
    @Override
    protected Shape getOutline ()
    {
        return this.outline;
    }

    /**
     * Called when it's time for the Debris to expire
     */
    @Override
    public void countdownComplete (Object payload)
    {
        // When a bullets active time exceeds BULLET_DURATION it expires
        
        if (payload.equals("expire"))
        {
            // Expire Debris
            Participant.expire(this);
        }
    }
    
    /**
     * Function Ignored as Class is OnscreenLabel
     */
    @Override
    public void collidedWith (Participant p)
    {
    }
}
