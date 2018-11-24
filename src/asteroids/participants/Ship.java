package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.*;
import asteroids.destroyers.*;
import asteroids.game.Controller;
import asteroids.game.Participant;
import javax.swing.Timer;

/**
 * Represents ships
 */
public class Ship extends Participant implements AsteroidDestroyer
{
    /** The outline of the ship */
    private Shape outline;

    /** The outline of the ship with thrust animation */
    // private Shape outlineWithThrust;

    /** Game controller */
    private Controller controller;

    /** Timer to control smooth turning */
    private Timer timer;

    /** Radians to turn each occurance */
    private static double TURN_RADIANS = Math.PI / 80.0;

    /** Keeps track of which ship key controls are active { thrust, turnRight, turnLeft } */
    public boolean[] keyControls;

    /** Is ship invincible */
    private boolean invincible;
    
    /** Timer to end Invincibility */
    private Timer invincibilityTimer;

    /**
     * Constructs a ship at the specified coordinates that is pointed in the given direction.
     */
    public Ship (int x, int y, double direction, Controller controller)
    {
        // starting stuff like assigning var and Particpant controls
        this.controller = controller;
        setPosition(x, y);
        setRotation(direction);
        this.invincible = false;
        this.invincibilityTimer = new Timer(INVINCIBILITY_TIME, this.controller);
        
        // Create outline
        createOutline();

        // Schedule move timer every MOVEMENT_DELAY
        this.timer = new Timer(MOVEMENT_DELAY, this.controller);
        this.timer.start();

        // Initiate boolean[] to keep track of active key controls
        this.keyControls = new boolean[] { false, false, false };
    }

    /**
     * Returns the X-coordinate of the point on the screen where the ship's nose is located.
     * 
     * @return double
     */
    public double getXNose ()
    {
        Point2D.Double point = new Point2D.Double(20, 0);
        transformPoint(point);
        return point.getX();
    }

    /**
     * Returns the Y-coordinate of the point on the screen where the ship's nose is located.
     * 
     * @return double
     */
    public double getYNose ()
    {
        Point2D.Double point = new Point2D.Double(20, 0);
        transformPoint(point);
        return point.getY();
    }

    /**
     * Creates outline of the Ship, is Public so Controller can call it when adding or removing shield 
     */
    public void createOutline ()
    {
        // Draw Ship
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(21, 0);
        poly.lineTo(-21, 12);
        poly.lineTo(-14, 10);
        poly.lineTo(-14, -10);
        poly.lineTo(-21, -12);
        poly.closePath();

        if (this.isInvincible())
        {
            // Create Shield around Ship
            Ellipse2D.Double ellipse = new Ellipse2D.Double(-29, -17, SHIP_HEIGHT + 10, SHIP_WIDTH + 10);
            poly.append(ellipse, false);
        }

        this.outline = poly;
    }

    /**
     * Returns the outline of the Ship
     */
    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    /**
     * Customizes the base move method by imposing friction
     */
    @Override
    public void move ()
    {
        applyFriction(SHIP_FRICTION);
        super.move();
    }

    /**
     * Turns right by Pi/16 radians
     */
    public void turnRight ()
    {
        this.keyControls[1] = true;
        rotate(TURN_RADIANS);
    }

    /**
     * Turns left by Pi/16 radians
     */
    public void turnLeft ()
    {
        this.keyControls[2] = true;
        rotate(-TURN_RADIANS);
    }

    /**
     * Accelerates by SHIP_ACCELERATION
     */
    public void accelerate ()
    {
        this.keyControls[0] = true;
        accelerate(SHIP_ACCELERATION);
    }

    /**
     * Make the Ship Invincible
     */
    public void setInvincible ()
    {
        this.invincible = true;
        createOutline();
        this.invincibilityTimer.start();
    }
    
    /**
     * Get InvincibilityTimer
     */
    public Timer getInvincibilityTimer ()
    {
        return this.invincibilityTimer;
    }
    
    /** 
     * End InvincibilityTimer and turn off ship invincibility
     */
    public void endInvincibilityTimer ()
    {
        this.invincibilityTimer.stop();
        this.invincible = false;
        createOutline();
    }
    /**
     * Returns whether or not the ship is invincible. If ship is invicible, it cannot shoot or expire, asteroids will
     * also not be destroyed when contact is made
     * 
     * @return boolean
     */
    public boolean isInvincible ()
    {
        return this.invincible;
    }

    /**
     * returns the timer used for turning
     * 
     * @return Timer
     */
    public Timer getTimer ()
    {
        return this.timer;
    }

    /**
     * When a Ship collides with a ShipDestroyer
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof ShipDestroyer && !this.isInvincible())
        {
            // Expire the ship from the game
            this.timer.stop();
            Participant.expire(this);

            // Tell the controller the ship was destroyed
            controller.shipDestroyed();
        }
    }
}