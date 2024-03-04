package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

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

    // Fields to manage buttons
    /** Whether the accelerate button was pressed. */
    private boolean acceleratePressed;
    /** Whether the boost button was pressed. */
    private boolean boostPressed;
    /** Whether the vacuum button was pressed. */
    private boolean vacuumPressed;
    /** Whether the rotate left button was pressed. */
    private boolean rotateLeftPressed;
    /** Whether the rotate right button was pressed. */
    private boolean rotateRightPressed;
    /** Whether the decelerate button was pressed. */
    private boolean deceleratePressed;
    /** Whether the exit button was pressed. */
    private boolean exitPressed;

    /** The player position */
    private Vector2 position;
    /** For the player. */
    private float momentum;

    /**
     * Returns true if the accelerate action button was pressed.
     *
     * This is a sustained button. It will returns true as long as the player
     * holds it down.
     *
     * @return true if the accelerate action button was pressed.
     */
    public boolean didAccelerate() {
        return acceleratePressed;
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
     * Returns true if the rotate left action button was pressed.
     *
     * This is a sustained button. It will returns true as long as the player
     * holds it down.
     *
     * @return true if the rotation action button was pressed.
     */
    public boolean didRotateLeft() {
        return rotateLeftPressed;
    }
    /**
     * Returns true if the rotate right action button was pressed.
     *
     * This is a sustained button. It will returns true as long as the player
     * holds it down.
     *
     * @return true if the rotation action button was pressed.
     */
    public boolean didRotateRight() {
        return rotateRightPressed;
    }
    /**
     * Returns true if the rotation action button was pressed.
     *
     * This is a sustained button. It will returns true as long as the player
     * holds it down.
     *
     * @return true if the rotation action button was pressed.
     */
    public boolean didRotate() {
        return rotateLeftPressed || rotateRightPressed;
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
     * Creates a new input controller
     *
     */
    public InputController() {
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
        acceleratePressed = Gdx.input.isKeyPressed(Input.Keys.W);
        boostPressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        vacuumPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
        rotateLeftPressed = Gdx.input.isKeyPressed(Input.Keys.A);
        rotateRightPressed = Gdx.input.isKeyPressed(Input.Keys.D);
        deceleratePressed = Gdx.input.isKeyPressed(Input.Keys.S);
        exitPressed = Gdx.input.isKeyPressed(Input.Keys.ESCAPE);
    }
}