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

    /** The base Shape of the ship without thrust */
    private Path2D.Double baseShape;
    
    /** The Shape of the ships thrust */
    private Path2D.Double thrustShape;

    /** Game controller */
    private Controller controller;

    /** Keeps track of which ship key controls are active { thrust, turnRight, turnLeft } */
    public boolean[] keyControls = new boolean[] { false, false, false };

    /** Thrust boolean to have thrust shape flash on and off */
    private boolean drawThrust = false;

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
        createBaseOutline();
        createThrustOutline();
        createOutline();

        // Create countdown timer for when its time to turn ship
        new ParticipantCountdownTimer(this, "updateTurning", MOVEMENT_DELAY);
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
     * Creates base outline of the ship
     */
    private void createBaseOutline ()
    {
        this.baseShape = new Path2D.Double();
        baseShape.moveTo(21, 0);
        baseShape.lineTo(-21, 12);
        baseShape.lineTo(-14, 10);
        baseShape.lineTo(-14, -10);
        baseShape.lineTo(-21, -12);
        baseShape.closePath();
    }
    
    /**
     * Creates the outline of the thrust
     */
    private void createThrustOutline ()
    {
        this.thrustShape = new Path2D.Double();
        thrustShape.moveTo(-14, 5);
        thrustShape.lineTo(-30, 0);
        thrustShape.lineTo(-14, -5);
        thrustShape.closePath();
    }

    /**
     * Creates outline of the Ship
     */
    private void createOutline ()
    {
        // Temperary holder for baseOutline so it is never changed
        Path2D.Double baseShapeHold = new Path2D.Double();
        baseShapeHold.append(this.baseShape, false);
        
        // If thrust is being applied - append thrust image
        if (this.drawThrust)
        {
            baseShapeHold.append(this.thrustShape, false);
        }

        this.outline = baseShapeHold;
    }

    /**
     * Turns this.drawThrust off and redraws ship image
     */
    public void turnDrawThrustOff ()
    {
        this.drawThrust = false;
        this.createOutline();
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
        rotate(TURN_RADIANS);
    }

    /**
     * Turns left by Pi/16 radians
     */
    public void turnLeft ()
    {
        rotate(-TURN_RADIANS);
    }

    /**
     * Accelerates by SHIP_ACCELERATION
     */
    public void accelerate ()
    {
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
            // Create Debris
            this.controller.createShipDebris(this.getX(), this.getY());

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
                this.drawThrust = this.drawThrust ? false : true;
                this.createOutline();
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
