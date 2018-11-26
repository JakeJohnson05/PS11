package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;
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

    /** Controller for the AlienShip */
    private Controller controller;

    /** Ship General Horizontal Direction in Radians */
    private Double generalDirection;

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

        // Set Alien ship position and orientation
        setPosition(SIZE + 5, RANDOM.nextInt(400) + 150);
        setDirection(-Math.PI / 2);

        // Set AlienShip General Direction (Left || Right)
        this.generalDirection = ALIENSHIP_HORIZONTAL_DIRECTIONS[RANDOM.nextInt(2)];

        // Assign Velocity
        this.changeVelocity();

        // Assign Vars
        this.size = size;
        this.controller = controller;
        this.createOutline();

        // AlienShip has a 3 second delay between firing.
        new ParticipantCountdownTimer(this, "fireBullet", ALIENSHIP_SHOT_DELAY);

        // AlienMovement changes between each delay
        new ParticipantCountdownTimer(this, "changeDirection", ALIENSHIP_MOVEMENT_DELAY);
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
     * Change this.setVelocity
     */
    public void changeVelocity ()
    {
        // Movement from Horizontal Direction
        int change = RANDOM.nextInt(3) - 1;

        // Change Speed relative to AlienShip.size and add Value change to generalDirection
        setVelocity(ALIENSHIP_SPEED - (3 * this.size), this.generalDirection + change);
    }

    /**
     * Get the x-coordinate of the center of the AlienShip
     * 
     * @return double
     */
    public double getX ()
    {
        return super.getX();
    }

    /**
     * Get the y-coordinate of the center of the AlienShip
     * 
     * @return double
     */
    public double getY ()
    {
        return super.getY();
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
     * Called when alien ship collides with another object
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof ShipDestroyer || p instanceof AsteroidDestroyer)
        {
            // Create Debris
            this.controller.createShipDebris(this.getX(), this.getY());
            
            // Add points
            this.controller.addScore(ALIENSHIP_SCORE[this.size]);

            // Expire ship
            Participant.expire(this);

            // Inform the controller
            this.controller.alienShipDestroyed();
        }
    }

    /**
     * This method is invoked when a ParticipantCountdownTimer completes its countdown.
     */
    @Override
    public void countdownComplete (Object payload)
    {
        // After a Delay of 3000 ms after each shot, another shot is fired
        if (payload.equals("fireBullet") && this.controller.getAlienShip() != null)
        {
            double direction;

            if (this.size == 1)
            {
                direction = RANDOM.nextDouble() * 2 * Math.PI;
            }
            else
            {
                direction = Math.PI / 2.0;
            }
            AlienBullet alienBullet = new AlienBullet(this.getX(), this.getY(), direction, this.controller);
            alienBullet.move();
            this.controller.addParticipant(alienBullet);

            // Restart CountdownTimer
            new ParticipantCountdownTimer(this, "fireBullet", ALIENSHIP_SHOT_DELAY);
        }
        
        // After a Delay of 1000 ms after each change in direction, another change is made
        else if (payload.equals("changeDirection") && this.controller.getAlienShip() != null)
        {
            this.changeVelocity();
            
            // Restart CountdownTimer
            new ParticipantCountdownTimer(this, "changeDirection", ALIENSHIP_MOVEMENT_DELAY);
        }
    }
}
