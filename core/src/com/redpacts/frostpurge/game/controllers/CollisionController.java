package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.math.Vector2;

public class CollisionController {
    /** Width of the collision geometry */
    private float width;
    /** Height of the collision geometry */
    private float height;
    // Cache objects for collision calculations
    private Vector2 temp1;
    private Vector2 temp2;

    /**
     * Creates a CollisionController for the given screen dimensions.
     *
     * @param width   Width of the screen
     * @param height  Height of the screen
     */
    public CollisionController(float width, float height) {
        this.width = width;
        this.height = height;

        // Initialize cache objects
        temp1 = new Vector2();
        temp2 = new Vector2();
    }
}
