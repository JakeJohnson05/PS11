package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.*;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

/**
 * Represents Bullets
 * 
 * @author Jake Johnson
 */
public class AlienBullet extends Participant implements AsteroidDestroyer, ShipDestroyer
{
    /** The outline of the bullet */
    private Shape outline;

    /** The controller of the game */
    private Controller controller;

    /** Bullet Diameter */
    private static double BULLET_DIAM = 3.0;

    /**
     * Creates a bullet with the same coordinates as the nose of the ship and moves in the direction of the ship when
     * fired with a set velocity, size, and time duration
     */
    public AlienBullet (double x, double y, double direction, Controller controller)
    {
        // Create Bullet
        this.controller = controller;
        setPosition(x, y);
        setVelocity(BULLET_SPEED, direction);

        // Create Outline
        Ellipse2D.Double ellipse = new Ellipse2D.Double(-(BULLET_DIAM / 2.0), -(BULLET_DIAM / 2.0), BULLET_DIAM,
                BULLET_DIAM);
        this.outline = ellipse;
        
        // Create countdown timer for when the bullet has reached its max duration
        new ParticipantCountdownTimer(this, "bulletTimeOut", BULLET_DURATION);
    }

    /**
     * Returns the outline of the bullet
     * 
     * @return
     */
    @Override
    protected Shape getOutline ()
    {
        return this.outline;
    }

    /**
     * Called when a bullet hits a ShipDestroyer
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof ShipDestroyer)
        {
            // Expire the Bullet
            Participant.expire(this);

            // Inform the controller
            controller.asteroidDestroyed();
        }
    }

    /**
     * This method is invoked when a ParticipantCountdownTimer completes its countdown.
     */
    @Override
    public void countdownComplete (Object payload)
    {
        // When a bullets active time exceeds BULLET_DURATION it expires
        
        if (payload.equals("bulletTimeOut"))
        {
            // Expire bullet
            Participant.expire(this);
        }
    }
}

