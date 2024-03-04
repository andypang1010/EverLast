package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redpacts.frostpurge.game.models.EnemyModel;
import com.redpacts.frostpurge.game.models.MapModel;
import com.redpacts.frostpurge.game.models.PlayerModel;

public class CollisionController {
    /** Reference to the game board */
    public MapModel board;
    /** Reference to the player in the game */
    public PlayerModel player;
    /** Reference to all the enemies in the game */
    public Array<EnemyModel> enemies;
    /** Width of the collision geometry */
    private float width;
    /** Height of the collision geometry */
    private float height;
    /** Offset of bounds */
    private int offset = 10;



    // Cache objects for collision calculations
    private Vector2 temp1;
    private Vector2 temp2;

    /// ACCESSORS

    /**
     * Returns width of the game window (necessary to detect out of bounds)
     *
     * @return width of the game window
     */
    public float getWidth() {
        return width;
    }

    /**
     * Returns height of the game window (necessary to detect out of bounds)
     *
     * @return height of the game window
     */
    public float getHeight() {
        return height;
    }

    /// COLLISION CHECK

    /**
     * Creates a CollisionController for the given screen dimensions.
     *
     * @param board   The game board
     * @param enemies List of enemies
     * @param width   Width of the screen
     * @param height  Height of the screen
     */
    public CollisionController(MapModel board, PlayerModel player, Array<EnemyModel> enemies, float width, float height) {
        this.board = board;
        this.player = player;
        this.enemies = enemies;

        this.width = width;
        this.height = height;

        // Initialize cache objects
        temp1 = new Vector2();
        temp2 = new Vector2();
    }

    /**
     * Updates all the collisions.
     *
     */
    public void update() {
//        int length = enemies.size;

        // Process bounds for player
        processBound((PlayerModel) player);

//        // Test collisions between player and enemies. Process enemies bounds
//        for (int ii = 0; ii <= length - 1; ii++) {
//            checkForCollision(player, enemies.get(ii));
//            processBound((EnemyModel) enemies.get(ii));
//        }
//
//        for (int ii = 0; ii < length - 1; ii++) {
//            for (int jj = ii + 1; jj < length; jj++) {
//                checkForCollision(enemies.get(ii), enemies.get(jj));
//            }
//        }
    }
    /**
     * Handles collisions between enemies, causing them to bounce off one another.
     *
     * This method updates the velocities of both enemies: the collider and the
     * collidee. Therefore, you should only call this method for one of the
     * ships, not both. Otherwise, you are processing the same collisions
     * twice.
     *
     * @param enemy1 The collider
     * @param enemy2 The collidee
     */
    private void checkForCollision(EnemyModel enemy1, EnemyModel enemy2) {
        // TODO: Update so that we get position of enemy
        // Got the positions for each ship
        float x1 = enemy1.getLocation().x;
        float y1 = enemy1.getLocation().y;
        float x2 = enemy2.getLocation().x;
        float y2 = enemy2.getLocation().y;

        // TODO: Update logic of guard check and location update such that it will account for enemy size.
        // TODO: Update logic so that it will change velocity properly.
        // If the two enemies collide
        if (x1 == x2 && y1 == y2) {
            float vx1 = enemy1.getVelocity().x;
            float vy1 = enemy1.getVelocity().y;
            float vx2 = enemy2.getVelocity().x;
            float vy2 = enemy2.getVelocity().y;

            enemy1.setVelocity(-vx1, -vy1);
            enemy2.setVelocity(-vx2, -vy2);
        }
    }

    /**
     * Handles collisions between a ship and a photon
     *
     *
     * Recall that when a photon collides with a ship, the tile at that position
     * is destroyed.
     *
     * @param player The player
     * @param enemy  The enemy
     */
    private void checkForCollision(PlayerModel player, EnemyModel enemy) {
        // TODO: Update so that we get position of enemy
        // Got the positions for each ship
        float x1 = player.getLocation().x;
        float y1 = player.getLocation().y;
        float x2 = enemy.getLocation().x;
        float y2 = enemy.getLocation().y;

        // At the moment the collision is resolved by the same method as enemy-enemy collision
        // If player collides with enemy
        if (x1 == x2 && y1 == y2) {
            float vx1 = player.getVelocity().x;
            float vy1 = player.getVelocity().y;
            float vx2 = enemy.getVelocity().x;
            float vy2 = enemy.getVelocity().y;

            player.setVelocity(-vx1, -vy1);
            enemy.setVelocity(-vx2, -vy2);
        }
    }

    /**
     * Check player for being out-of-bounds.
     *
     * @param player Player to check
     */
    private void processBound(PlayerModel player) {
        // Do not let the player go off screen.
        if (player.getLocation().x <= 0) {
            player.setLocation(0, player.getLocation().y);
            player.setVelocity(-player.getVelocity().x, player.getVelocity().y);
        } else if (player.getLocation().y <= 0) {
            player.setLocation(player.getLocation().x, 0);
            player.setVelocity(player.getVelocity().x, -player.getVelocity().y);
        } else if (player.getLocation().x >= getWidth()) {
            player.setLocation(getWidth(), player.getLocation().y);
            player.setVelocity(-player.getVelocity().x, player.getVelocity().y);
        } else if (player.getLocation().y >= getHeight()){
            player.setLocation(player.getLocation().x, getHeight());
            player.setVelocity(player.getVelocity().x, -player.getVelocity().y);
        }
    }

    /**
     * Check enemy for being out-of-bounds.
     *
     * @param enemy Enemy to check
     */
    private void processBound(EnemyModel enemy) {
        // Do not let the enemy go off screen.
        if (enemy.getLocation().x <= 0) {
            enemy.setLocation(0, enemy.getLocation().y);
            enemy.setVelocity(-enemy.getVelocity().x, enemy.getVelocity().y);
        } else if (enemy.getLocation().y <= 0) {
            enemy.setLocation(enemy.getLocation().x, 0);
            enemy.setVelocity(enemy.getVelocity().x, -enemy.getVelocity().y);
        } else if (enemy.getLocation().x >= getWidth()) {
            enemy.setLocation(getWidth(), enemy.getLocation().y);
            enemy.setVelocity(-enemy.getVelocity().x, enemy.getVelocity().y);
        } else if (enemy.getLocation().y >= getHeight()){
            enemy.setLocation(enemy.getLocation().x, getHeight());
            enemy.setVelocity(enemy.getVelocity().x, -enemy.getVelocity().y);
        }
    }

    /**
     * Returns the manhattan distance between two points
     *
     * @return the manhattan distance between two points
     */
    private float manhattan(float x0, float y0, float x1, float y1) {
        return Math.abs(x1 - x0) + Math.abs(y1 - y0);
    }
}
