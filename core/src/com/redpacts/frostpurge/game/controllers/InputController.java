package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redpacts.frostpurge.game.util.Controllers;
import com.redpacts.frostpurge.game.util.XBoxController;


public class InputController {

    /** The singleton instance of the input controller */
    private static InputController theController = null;

    /**
     * Return the singleton instance of the input controller
     *
     * @return the singleton instance of the input controller
     */
    public InputController getInstance() {
        if (theController == null) {
            theController = new InputController();
        }
        return theController;
    }

    /** X-Box controller associated with this player (if any) */
    protected XBoxController xbox;
    /** array used to normalize vectors*/
    protected float [] normalization;
    // Fields to manage buttons
    /** How much the player should move left or right */
    private float horizontal;
    /** How much the player should move up or down */
    private float vertical;
    /** Whether the boost button was pressed. */
    private boolean boostPressed;
    /** Whether the vacuum button was pressed. */
    private boolean vacuumPressed;
    /** Whether the decelerate button was pressed. */
    private boolean deceleratePressed;
    /** Whether the debug button was pressed. */
    private boolean debugPressed;
    /** Whether the exit button was pressed. */
    private boolean exitPressed;
    private boolean replayPressed;
    private boolean pausePressed;
    private boolean previousPausePressed = false;

    /** The player position */
    private Vector2 position;
    /** For the player. */
    private float momentum;

    /**
     * Returns the amount to move in the horizontal direction
     *
     * This is a sustained button. It will return the same as long as the player
     * holds it down.
     */
    public float getHorizontal() {
        return horizontal;
    }
    /**
     * Returns the amount to move in the vertical direction
     *
     * This is a sustained button. It will return the same as long as the player
     * holds it down.
     */
    public float getVertical() {
        return vertical;
    }
    /**
     * Returns true if the boost action button was pressed.
     *
     * This is a sustained button. It will returns true as long as the player
     * holds it down.
     *
     * @return true if the boost action button was pressed.
     */
    public boolean didBoost() {
        return boostPressed;
    }

    /**
     * Returns true if the vacuum action button was pressed.
     *
     * This is a sustained button. It will returns true as long as the player
     * holds it down.
     *
     * @return true if the vacuum action button was pressed.
     */
    public boolean didVacuum() {
        return vacuumPressed;
    }

    /**
     * Returns true if the decelerate action button was pressed.
     *
     * This is a sustained button. It will returns true as long as the player
     * holds it down.
     *
     * @return true if the decelerate action button was pressed.
     */
    public boolean didDecelerate() {
        return deceleratePressed;
    }

    /**
     * Returns true if the decelerate action button was pressed.
     *
     * @return true if the debug action button was pressed.
     */
    public boolean didDebug() {
        return debugPressed;
    }
    public boolean didExit(){return exitPressed;}
    public boolean didReplay(){return replayPressed;}
    /**
     * Return pausePressed. Pause must be cleared using InputController.clearPausePressed() */
    public boolean didPause(){return pausePressed;}
    public void clearPausePressed() {
        pausePressed = false;
        previousPausePressed = true; // Prevents pause from being triggered again immediately
    }

    /**
     * Creates a new input controller for the specified player.
     *
     * The game supports two players working against each other in hot seat mode.
     * We need a separate input controller for each player. In keyboard, this is
     * WASD vs. Arrow keys.  We also support multiple X-Box game controllers.
     *
     */
    public InputController() {
        // If we have a game-pad for id, then use it.
        Array<XBoxController> controllers = Controllers.get().getXBoxControllers();
        if (controllers.size > 0) {
            xbox = controllers.get(0);
        } else {
            xbox = null;
            normalization = new float[2];
        }
    }

    /**
     * Reads the input for the player and converts the result into game logic.
     *
     * The method provides both the input bounds and the drawing scale.  It needs
     * the drawing scale to convert screen coordinates to world coordinates.  The
     * bounds are for the player.  They cannot go outside of this zone.
     *
     * @param bounds The input bounds for the player.
     * @param scale  The drawing scale
     */
    public void readInput(Rectangle bounds, Vector2 scale) {
        if (xbox != null){
            float x = xbox.getLeftX();
            float y = xbox.getLeftY();
            if (Math.abs(x) < xbox.getDeadZone()){
                x = 0;
            }
            if (Math.abs(y) < xbox.getDeadZone()){
                y = 0;
            }
            horizontal = x;
            vertical = y;
            exitPressed = xbox.getBack();
            deceleratePressed = xbox.getA();
            boostPressed = xbox.getRBumper();
            vacuumPressed = xbox.getLBumper();
            debugPressed = xbox.getDPadDown();
            replayPressed = xbox.getB();
            boolean currentPausePressed = xbox.getStart();
            if (currentPausePressed && !previousPausePressed) {
                pausePressed = true;
            } else {
                pausePressed = false;
            }
            // Remember the current state for the next frame
            previousPausePressed = currentPausePressed;
        }
        else {
            float x = Gdx.input.getX() - (float) Gdx.graphics.getWidth() / 2;
            float y = Gdx.input.getY() - (float) Gdx.graphics.getHeight() / 2;
            if (Math.abs(x) < 10f){
                x = 0;
            }
            if (Math.abs(y) < 10f){
                y = 0;
            }
            normalize(x,y);
            if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)){
                horizontal = normalization[0];
                vertical = normalization[1];
            }else{
                horizontal = 0;
                vertical = 0;
            }
            boostPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
            vacuumPressed = Gdx.input.isButtonPressed((Input.Buttons.MIDDLE));
            deceleratePressed = !Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
            exitPressed = Gdx.input.isKeyPressed(Input.Keys.BACKSPACE);
            debugPressed = Gdx.input.isKeyPressed(Input.Keys.D);
            replayPressed = Gdx.input.isKeyPressed(Input.Keys.R);
            boolean currentPausePressed = Gdx.input.isKeyPressed(Input.Keys.ESCAPE);
            if (currentPausePressed && !previousPausePressed) {
                pausePressed = true;
            } else {
                pausePressed = false;
            }
            // Remember the current state for the next frame
            previousPausePressed = currentPausePressed;
        }
    }

    /**
     * normalizes the vector created by x and y so that it can fit on the unit circle
     * @param x amount in x direction
     * @param y amount in y direction
     */
    private void normalize(float x, float y){
        if (x == 0 && y == 0) {
            normalization[0] = 0;
            normalization[1] = 0;
            return;
        }
        else{
            float c = (float) Math.sqrt((x*x+y*y));
            x /= c;
            y /= c;
        }
        normalization[0] = x;
        normalization[1] = y;
    }
}