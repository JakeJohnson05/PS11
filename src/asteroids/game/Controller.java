package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;
import asteroids.participants.*;

/**
 * Controls a game of Asteroids.
 */
public class Controller implements KeyListener, ActionListener
{
    /** The state of all the Participants */
    private ParticipantState pstate;

    /** The ship (if one is active) or null (otherwise) */
    private Ship ship;

    /** The AlienShip */
    private AlienShip alienShip;

    /** When this timer goes off, it is time to refresh the animation */
    private Timer refreshTimer;

    /** AlienShip Spawn Timer */
    private Timer alienShipSpawnTimer;

    /** Number of lives left */
    private int lives;

    /** List of the life objects */
    private ArrayList<Lives> lifeList = new ArrayList<Lives>();

    /** Keeps track of active bullets */
    private int numBullets;

    /** The current level */
    private int level;

    /** Players current score */
    private int score;

    /**
     * The time at which a transition to a new stage of the game should be made. A transition is scheduled a few seconds
     * in the future to give the user time to see what has happened before doing something like going to a new level or
     * resetting the current level.
     */
    private long transitionTime;

    /** The game display */
    private Display display;

    /**
     * Constructs a controller to coordinate the game and screen
     */
    public Controller ()
    {
        // Initialize the ParticipantState
        pstate = new ParticipantState();

        // Set up the refresh timer.
        refreshTimer = new Timer(FRAME_INTERVAL, this);

        // Set up AlienShipSpawn timer
        this.alienShipSpawnTimer = new Timer(ALIEN_DELAY, this);

        // Clear the transitionTime
        transitionTime = Long.MAX_VALUE;

        // Record the display object
        display = new Display(this);

        // Bring up the splash screen and start the refresh timer
        splashScreen();
        display.setVisible(true);
        refreshTimer.start();
    }

    /**
     * Returns the ship, or null if there isn't one
     * 
     * @return Current asteroids.participants.Ship
     */
    public Ship getShip ()
    {
        return ship;
    }

    /**
     * Returns the AlienShip
     * 
     * @return Current participants.AlienShip
     */
    public AlienShip getAlienShip ()
    {
        return this.alienShip;
    }

    /**
     * Returns the AlienShipSpawnTimer
     * 
     * @return AlienShipSpawnTimer
     */
    private Timer getAlienShipSpawnTimer ()
    {
        return this.alienShipSpawnTimer;
    }

    /** Add to the score */
    public void addScore (int points)
    {
        this.score += points;
    }

    /**
     * Get the current score
     * 
     * @return score
     */
    private int getScore ()
    {
        return this.score;
    }

    /**
     * Get the current level
     * 
     * @return level
     */
    private int getLevel ()
    {
        return this.level;
    }

    /**
     * Configures the game screen to display the splash screen
     */
    private void splashScreen ()
    {
        // Clear the screen, reset the level, and display the legend
        clear();
        display.setLegend("Asteroids");

        // Place four asteroids near the corners of the screen.
        placeAsteroids();
    }

    /**
     * The game is over. Displays a message to that effect.
     */
    private void finalScreen ()
    {
        display.removeKeyListener(this);
        display.setLegend(GAME_OVER);
    }

    /**
     * Place a new ship in the center of the screen. Remove any existing ship first.
     */
    private void placeShip ()
    {
        // Place a new ship
        Participant.expire(ship);
        ship = new Ship(SIZE / 2, SIZE / 2, -Math.PI / 2, this);
        addParticipant(ship);
        display.setLegend("");
    }

    /**
     * Creates the lives remaining visual and assigns them to lifeList
     */
    public void drawLives ()
    {
        for (int life = 0; life < this.lives; life++)
        {
            this.lifeList.add(new Lives(life));
            addParticipant(this.lifeList.get(life));
        }
    }

    /**
     * Places an asteroid near one corner of the screen. Gives it a random velocity and rotation.
     */
    private void placeAsteroids ()
    {
        // distance from top left of the screen [ 0, 0 ]
        int xOffSet;
        int yOffSet;

        for (int i = 0; i < 3 + this.level; i++)
        {
            // Depending on value from RANDOM, Asteroids are randomly placed in the 4 corners of the screen.
            // Asteroid will always be placed EDGE_OFFSET away from the edge of the screen
            xOffSet = EDGE_OFFSET + ((SIZE - (EDGE_OFFSET * 2)) * RANDOM.nextInt(2));
            yOffSet = EDGE_OFFSET + ((SIZE - (EDGE_OFFSET * 2)) * RANDOM.nextInt(2));

            addParticipant(new Asteroid(RANDOM.nextInt(4), 2, xOffSet, yOffSet, this));
        }
    }

    /**
     * Clears the screen so that nothing is displayed
     */
    private void clear ()
    {
        pstate.clear();
        display.setLegend("");
        ship = null;
    }

    /**
     * Sets things up and begins a new game.
     */
    private void initialScreen ()
    {
        // Reset statistics
        this.lives = 3;
        this.score = 0;
        this.level = 1;

        // Update display for stats
        this.display.setScore(this.getScore());
        this.display.setLevel(this.getLevel());

        // Clear the screen
        clear();

        // Place asteroids
        placeAsteroids();

        // Place the ship
        placeShip();

        // Place lives remaining
        drawLives();

        // Start listening to events (but don't listen twice)
        display.removeKeyListener(this);
        display.addKeyListener(this);

        // Give focus to the game screen
        display.requestFocusInWindow();
    }

    /**
     * Go to the next level
     */
    public void nextLevel ()
    {
        // Clear Screen
        clear();

        // Update Level
        this.level++;
        this.display.setLevel(this.getLevel());

        // Place asteroids
        placeAsteroids();

        // Place the ship
        placeShip();

        // Place lives remaining
        drawLives();

        // Start countDownTimer for alienShip arrival
        this.alienShipSpawnTimer.start();

        // Start listening to events (but don't listen twice)
        display.removeKeyListener(this);
        display.addKeyListener(this);

        // Give focus to the game screen
        display.requestFocusInWindow();
    }

    /**
     * Adds a new Participant
     */
    public void addParticipant (Participant p)
    {
        pstate.addParticipant(p);
    }

    /**
     * The ship has been destroyed
     */
    public void shipDestroyed ()
    {
        // Null out the ship
        ship = null;

        // Display a graphic

        // Decrement lives
        lives--;

        // Update lifeList
        Participant.expire(lifeList.get(lives));
        lifeList.remove(lives);

        // Since the ship was destroyed, schedule a transition
        scheduleTransition(END_DELAY);
    }
    
    /**
     * AlienShip has been destroyed
     */
    public void alienShipDestroyed ()
    {
        // Null out the AlienShip
        this.alienShip = null;
        
        // Display a graphic
        
        // Restart SpawnTimer
        this.alienShipSpawnTimer.start();
    }

    /**
     * An asteroid has been destroyed
     */
    public void asteroidDestroyed ()
    {
        this.display.setScore(this.getScore());

        // If all the asteroids are gone, schedule a transition
        if (pstate.countAsteroids() == 0)
        {
            if (this.alienShipSpawnTimer.isRunning())
            {
                this.alienShipSpawnTimer.stop();
            }
            scheduleTransition(END_DELAY);
        }
    }

    /**
     * A bullet has been destroyed
     */
    public void bulletDestroyed ()
    {
        numBullets--;
    }

    /**
     * Schedules a transition m msecs in the future
     */
    private void scheduleTransition (int m)
    {
        transitionTime = System.currentTimeMillis() + m;
    }

    /**
     * This method will be invoked because of button presses and timer events.
     */
    @Override
    public void actionPerformed (ActionEvent e)
    {
        // The start button has been pressed. Stop whatever we're doing
        // and bring up the initial screen
        if (e.getSource() instanceof JButton)
        {
            initialScreen();
        }

        // Time to refresh the screen and deal with keyboard input
        else if (e.getSource() == refreshTimer)
        {
            // It may be time to make a game transition
            performTransition();

            // Move the participants to their new locations
            pstate.moveParticipants();

            // Refresh screen
            display.refresh();
        }

        // If it's time to start UFO encounters
        else if (this.alienShip == null && e.getSource() == this.getAlienShipSpawnTimer())
        {
            // Stop AlienSpawnTimer
            this.getAlienShipSpawnTimer().stop();

            // Create AlienShip with size in respect to current level
            this.alienShip = this.getLevel() == 2 ? new AlienShip(1, this) : new AlienShip(0, this);
            addParticipant(this.alienShip);
        }
    }

    /**
     * Returns an iterator over the active participants
     */
    public Iterator<Participant> getParticipants ()
    {
        return pstate.getParticipants();
    }

    /**
     * If the transition time has been reached, transition to a new state
     */
    private void performTransition ()
    {
        // Do something only if the time has been reached
        if (transitionTime <= System.currentTimeMillis())
        {
            // Clear the transition time
            transitionTime = Long.MAX_VALUE;

            // If there are no lives left, the game is over. Show the final screen.
            if (lives <= 0)
            {
                finalScreen();
            }
            else if (pstate.countAsteroids() == 0)
            {
                this.nextLevel();
            }
            else
            {
                placeShip();
            }
        }
    }

    /**
     * If a key of interest is pressed, record it.
     */
    @Override
    public void keyPressed (KeyEvent e)
    {
        if (this.getShip() != null)
        {
            int keyCode = e.getKeyCode();
            
            // Destroy all asteroids and advance
            if (keyCode == KeyEvent.VK_B)
            {
                pstate.clearAsteroids();
                this.asteroidDestroyed();
            }
            
            // Acclerating - UP_ARROW
            if (keyCode == KeyEvent.VK_UP && !ship.keyControls[0])
            {
                ship.keyControls[0] = true;
            }
    
            // Turn Right - RIGHT_ARROW
            else if (keyCode == KeyEvent.VK_RIGHT && !ship.keyControls[1])
            {
                ship.keyControls[1] = true;
            }
    
            // Turn Left - LEFT_ARROW
            else if (keyCode == KeyEvent.VK_LEFT && !ship.keyControls[2])
            {
                ship.keyControls[2] = true;
            }
    
            // Bullet Fired - SPACE_BAR
            else if (keyCode == KeyEvent.VK_SPACE && numBullets < BULLET_LIMIT)
            {
                numBullets++;
                addParticipant(new Bullet(ship.getXNose(), ship.getYNose(), ship.getRotation(), this));
            }
        }
    }

    /**
     * If a key of interest is pressed, record it
     */
    @Override
    public void keyReleased (KeyEvent e)
    {
        int keyCode = e.getKeyCode();

        // Acclerating - UP_ARROW
        if (keyCode == KeyEvent.VK_UP && ship != null)
        {
            ship.keyControls[0] = false;
        }

        // Turn Right - RIGHT_ARROW
        if (keyCode == KeyEvent.VK_RIGHT && ship != null)
        {
            ship.keyControls[1] = false;
        }

        // Turn Left - LEFT_ARROW
        if (keyCode == KeyEvent.VK_LEFT && ship != null)
        {
            ship.keyControls[2] = false;
        }
    }

    /**
     * These events are ignored.
     */
    @Override
    public void keyTyped (KeyEvent e)
    {
    }
}
