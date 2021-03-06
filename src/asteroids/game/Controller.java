package asteroids.game;

import static asteroids.game.Constants.*;
import asteroids.participants.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import javax.sound.sampled.*;
import javax.swing.*;
import java.util.Collections;

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

    /** beat audio Timer */
    private Timer beatTimer;

    /** Delay to display highScores Timer */
    private Timer highScoresTimer = new Timer(1000, null);

    /** Interval between Timer beats */
    private int beatInterval;

    /** If beat1 was played last */
    private boolean beat1Last;

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

    /** Top 3 highest scores */
    private ArrayList<Integer> highScores = new ArrayList<Integer>();

    /** Clip used for when alienShip blows up */
    private Clip bangAlienShipClip = createClip("/sounds/bangAlienShip.wav");

    /** Clip used for when Large asteroid blows up */
    public Clip bangLargeClip = createClip("/sounds/bangLarge.wav");

    /** Clip used for when Medium asteroid blows up */
    public Clip bangMediumClip = createClip("/sounds/bangMedium.wav");

    /** Clip used for when Small asteroid blows up */
    public Clip bangSmallClip = createClip("/sounds/bangSmall.wav");

    /** Clip used for when Ship blows up */
    private Clip bangShipClip = createClip("/sounds/bangShip.wav");

    /** Clip used when shot is fired */
    private Clip fireClip = createClip("/sounds/fire.wav");

    /** List of Clipse for gamePlay beat audio: { beat1, beat2 } */
    private Clip[] beatClips = new Clip[] { createClip("/sounds/beat1.wav"), createClip("/sounds/beat2.wav") };

    /** List of Clips for alienShipAudio: { saucerBig, saucerSmall } */
    private Clip[] saucerClips = new Clip[] { createClip("/sounds/saucerSmall.wav"),
            createClip("/sounds/saucerBig.wav") };

    /** Clip used when Ship is accelerating */
    private Clip thrustClip = createClip("/sounds/thrust.wav");

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

    /**
     * Add to the score
     */
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
        display.refresh();

        displayHighScores();
    }

    /**
     * Does everything needed to be done with the highScores
     */
    private void displayHighScores ()
    {
        // Display highScores
        try
        {
            File file = new File(
                    "C:/Users/Jake Johnson/eclipse-workspace/PS11_wastedPotential/src/asteroids/scores/HighScores.txt");
            Scanner scnr = new Scanner(file);

            while (scnr.hasNext() && scnr.hasNextLine())
            {
                try
                {
                    this.highScores.add(Integer.parseInt(scnr.nextLine()));
                }
                catch (Exception e)
                {
                    System.out.println("An error occured when parsing HighScores.txt" + e.getMessage());
                }
            }
            scnr.close();
        }
        catch (Exception e)
        {
            System.out.println("Error occured with HighScores.text: " + e.getMessage());
        }

        correctHighScores();
        display.setHighScores(this.highScores);
    }

    /**
     * Corrects highScores with new score and overwrites HighScores.txt
     */
    private void correctHighScores ()
    {
        // Add newest score && remove the lowest
        this.highScores.add(this.score);
        Collections.sort(this.highScores);
        this.highScores.remove(0);

        // Overwrite HighScores.txt with new scores
        try (FileWriter fileWriter = new FileWriter(
                "C:/Users/Jake Johnson/eclipse-workspace/PS11_wastedPotential/src/asteroids/scores/HighScores.txt"))
        {
            
            fileWriter.write(highScores.get(2) + "\n" + highScores.get(1) + "\n" + highScores.get(0));
        }
        catch (Exception e)
        {
            System.out.println("An error occured with the FileWriter: " + e.getMessage());
        }
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

        // Reset all beatTimer related vars, and start the timer
        this.beatInterval = INITIAL_BEAT;
        this.beat1Last = false;
        this.beatTimer = new Timer(beatInterval, this);
        this.beatTimer.start();
    }

    /**
     * Creates the lives remaining visual and assigns them to lifeList
     */
    public void drawLives ()
    {
        for (Lives life : lifeList)
        {
            Participant.expire(life);
        }
        lifeList.clear();

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
        alienShip = null;
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

        // Decrement lives
        lives--;

        // Update lifeList
        Participant.expire(lifeList.get(lives));
        lifeList.remove(lives);

        // Since the ship was destroyed, schedule a transition
        scheduleTransition(END_DELAY);

        // Play ship destroyed clip
        playClip(bangShipClip);

        // Stop beat Timer
        this.beatTimer.stop();
    }

    /**
     * AlienShip has been destroyed
     */
    public void alienShipDestroyed ()
    {
        // Stop alienShip audio clip
        this.saucerClips[alienShip.getAlienShipSize()].stop();

        // Null out the AlienShip
        this.alienShip = null;

        // Restart SpawnTimer
        this.alienShipSpawnTimer.start();

        // Play AlienShip destroyed Clip
        playClip(bangAlienShipClip);
    }

    /**
     * An asteroid has been destroyed
     */
    public void asteroidDestroyed ()
    {
        // Update Score
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
     * Creates Debris when a Ship (Ship or AlienShip) is destroyed
     * 
     * @param x, y
     */
    public void createShipDebris (double x, double y)
    {
        createAsteroidDebris(x, y);

        for (int i = 0; i < 2; i++)
        {
            addParticipant(new Debris(x, y, "line"));
        }
    }

    /**
     * Creates Debris when an Asteroid is destroyed
     * 
     * @param x, y
     */
    public void createAsteroidDebris (double x, double y)
    {
        for (int i = 0; i < 5; i++)
        {
            addParticipant(new Debris(x, y, "Circle"));
        }
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

        // Time for a beat
        else if (this.ship != null && e.getSource() == this.beatTimer)
        {
            if (beat1Last)
            {
                playClip(this.beatClips[1]);
            }
            else
            {
                playClip(this.beatClips[0]);
            }

            beat1Last = beat1Last ? false : true;

            this.beatInterval -= BEAT_DELTA;

            if (this.beatInterval < BEAT_DELTA)
            {
                this.beatInterval = BEAT_DELTA;
            }

            this.beatTimer.setDelay(beatInterval);
        }

        // If it's time to start UFO encounters
        else if (this.alienShip == null && this.level > 1 && e.getSource() == this.getAlienShipSpawnTimer())
        {
            // Stop AlienSpawnTimer
            this.getAlienShipSpawnTimer().stop();

            // Create AlienShip with size in respect to current level
            this.alienShip = this.getLevel() == 2 ? new AlienShip(1, this) : new AlienShip(0, this);
            addParticipant(this.alienShip);

            // Loop AlienShip audio
            this.saucerClips[alienShip.getAlienShipSize()].loop(Clip.LOOP_CONTINUOUSLY);
        }

        // Time to display highScores
        else if (e.getSource() == this.highScoresTimer)
        {
            this.highScoresTimer.stop();
            displayHighScores();
            display.refresh();
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
                display.removeKeyListener(this);
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

            // Add more lives
            else if (keyCode == KeyEvent.VK_N)
            {
                this.lives++;
                this.drawLives();
            }

            // Accelerating - UP_ARROW
            else if (keyCode == KeyEvent.VK_UP && !ship.keyControls[0])
            {
                ship.keyControls[0] = true;
                playClip(thrustClip);
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
                // Increase bullets, add Bullet participant, play Bullet fired clip
                numBullets++;
                addParticipant(new Bullet(ship.getXNose(), ship.getYNose(), ship.getRotation(), this));
                playClip(fireClip);
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
            ship.turnDrawThrustOff();
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

    /**
     * Creates an audio clip from a sound file.
     */
    public Clip createClip (String soundFile)
    {
        // Opening the sound file this way will work no matter how the
        // project is exported. The only restriction is that the
        // sound files must be stored in a package.
        try (BufferedInputStream sound = new BufferedInputStream(getClass().getResourceAsStream(soundFile)))
        {
            // Create and return a Clip that will play a sound file. There are
            // various reasons that the creation attempt could fail. If it
            // fails, return null.
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(sound));
            return clip;
        }
        catch (LineUnavailableException e)
        {
            return null;
        }
        catch (IOException e)
        {
            return null;
        }
        catch (UnsupportedAudioFileException e)
        {
            return null;
        }
    }

    /**
     * Plays a Clip
     */
    public void playClip (Clip clip)
    {
        if (clip.isRunning())
        {
            clip.stop();
        }
        clip.setFramePosition(0);
        clip.start();
    }
}
