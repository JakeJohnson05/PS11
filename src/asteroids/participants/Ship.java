package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.*;
import asteroids.destroyers.*;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

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

    /** Keeps track of which ship key controls are active { thrust, turnRight, turnLeft } */
    public boolean[] keyControls;

    /**
     * Constructs a ship at the specified coordinates that is pointed in the given direction.
     */
    public Ship (int x, int y, double direction, Controller controller)
    {
        // starting stuff like assigning var and Particpant controls
        this.controller = controller;
        setPosition(x, y);
        setRotation(direction);

        // Create outline
        createOutline();

        // Create countdown timer for when its time to turn ship
        new ParticipantCountdownTimer(this, "updateTurning", MOVEMENT_DELAY);

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
        // this.keyControls[1] = true;
        rotate(TURN_RADIANS);
    }

    /**
     * Turns left by Pi/16 radians
     */
    public void turnLeft ()
    {
        // this.keyControls[2] = true;
        rotate(-TURN_RADIANS);
    }

    /**
     * Accelerates by SHIP_ACCELERATION
     */
    public void accelerate ()
    {
        // this.keyControls[0] = true;
        accelerate(SHIP_ACCELERATION);
    }

    /**
     * When a Ship collides with a ShipDestroyer
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof ShipDestroyer)
        {
            // Expire the ship from the game
            Participant.expire(this);

            // Tell the controller the ship was destroyed
            controller.shipDestroyed();
        }
    }

    /**
     * This method is invoked when a ParticipantCountdownTimer completes its countdown.
     */
    @Override
    public void countdownComplete (Object payload)
    {
        // When movementTimer delay is reached, ship movements are activated
        if (payload.equals("updateTurning") && controller.getShip() != null)
        {
            if (this.keyControls[0])
            {
                this.accelerate();
            }
            if (this.keyControls[1])
            {
                this.turnRight();
            }
            else if (this.keyControls[2])
            {
                this.turnLeft();
            }

            // Create countdown timer for when its time to turn ship
            new ParticipantCountdownTimer(this, "updateTurning", MOVEMENT_DELAY);
        }
    }
}
