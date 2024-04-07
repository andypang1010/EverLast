package com.redpacts.frostpurge.game.util;

import com.badlogic.gdx.physics.box2d.*;
import com.redpacts.frostpurge.game.models.*;

import java.util.logging.Level;

public class GameContactListener implements ContactListener {
    private World world;
    private LevelModel board;

    public GameContactListener(World world, LevelModel board){
        this.world = world;
        this.board = board;
    }

    /**
     * Callback method for the start of a collision
     *
     * This method is called when we first get a collision between two objects.  We use
     * this method to test if it is the "right" kind of collision.  In particular, we
     * use it to test if we made it to the win door.
     *
     * @param contact The two bodies that collided
     */
    @Override
    public void beginContact(Contact contact) {
        //System.out.println("CONTACTED");
        Body body1 = contact.getFixtureA().getBody();
        Body body2 = contact.getFixtureB().getBody();
        GameObject obj1 = (GameObject)body1.getUserData();
        GameObject obj2 = (GameObject)body2.getUserData();

        // If either object is the avatar, change color
        if (obj1 != null && obj2 != null) {
            processCollision(obj1, obj2);
        }
    }

    /**
     * Callback method for the start of a collision
     *
     * This method is called when two objects cease to touch.
     */
    @Override
    public void endContact(Contact contact) {
        System.out.println("NOT CONTACTED");
    }

    /** Unused ContactListener method */
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
    /** Unused ContactListener method */
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    protected void processCollision(GameObject obj1, GameObject obj2) {
        if (obj1 instanceof PlayerModel && obj2 instanceof PlayerModel) {
            handleCollision((PlayerModel) obj1, (PlayerModel) obj2);
        } else if (obj1 instanceof PlayerModel && obj2 instanceof EnemyModel) {
            handleCollision((PlayerModel) obj1, (EnemyModel) obj2);
        } else if (obj1 instanceof PlayerModel && obj2 instanceof ObstacleTile) {
            handleCollision((PlayerModel) obj1, (ObstacleTile) obj2);
        } else if (obj1 instanceof PlayerModel && obj2 instanceof SwampTile) {
            handleCollision((PlayerModel) obj1, (SwampTile) obj2);
        }

        else if (obj1 instanceof EnemyModel && obj2 instanceof PlayerModel) {
            handleCollision((PlayerModel) obj2, (EnemyModel) obj1);
        } else if (obj1 instanceof EnemyModel && obj2 instanceof EnemyModel) {
            handleCollision((EnemyModel) obj1, (EnemyModel) obj2);
        } else if (obj1 instanceof EnemyModel && obj2 instanceof ObstacleTile) {
            handleCollision((EnemyModel) obj1, (ObstacleTile) obj2);
        }
    }

    /**
     * Handles collisions between a player and an enemy
     *
     * @param player    The player
     * @param player2   Other player
     */
    private void handleCollision(PlayerModel player, PlayerModel player2) {}
    /**
     * Handles collisions between a player and an enemy
     *
     * @param player The player
     * @param enemy  The enemy
     */
    private void handleCollision(PlayerModel player, EnemyModel enemy) {
//        float vx1 = player.getVelocity().x;
//        float vy1 = player.getVelocity().y;
//        float vx2 = enemy.getVelocity().x;
//        float vy2 = enemy.getVelocity().y;
//
//        player.setVelocity(-vx1, -vy1);
//        enemy.setVelocity(-vx2, -vy2);
    }

    /**
     * Handles collisions between a player and an enemy
     *
     * @param player The player
     * @param tile   The tile
     */
    private void handleCollision(PlayerModel player, ObstacleTile tile) {
        // TODO: Update so that we dampen the velocity of player
//        System.out.println("Contact with obstacle");
//        System.out.println();

//        float vx1 = player.getVelocity().x;
//        float vy1 = player.getVelocity().y;
//
//        player.setVelocity(-vx1, -vy1);
    }

    /**
     * Handles collisions between a player and an enemy
     *
     * @param player The player
     * @param tile   The tile
     */
    private void handleCollision(PlayerModel player, SwampTile tile) {
        // TODO: Update so that we dampen the velocity of player
//        System.out.println("Contact with swamp");
//        System.out.println("player position:" + player.getPosition());
//        System.out.println("tile position:" + tile.getPosition());
//        world.destroyBody(tile.getBody());
//        board.removeExtra(tile.getPosition().x, tile.getPosition().y);
//        player.setCanBoost(true);
    }

    /**
     * Handles collisions between a player and an enemy
     *
     * @param enemy    The enemy
     * @param enemy2   Other enemy
     */
    private void handleCollision(EnemyModel enemy, EnemyModel enemy2) {
        // TODO: Update so that we handle bounce correctly
//        float vx1 = enemy.getVelocity().x;
//        float vy1 = enemy.getVelocity().y;
//        float vx2 = enemy2.getVelocity().x;
//        float vy2 = enemy2.getVelocity().y;
//
//        enemy.setVelocity(-vx1, -vy1);
//        enemy2.setVelocity(-vx2, -vy2);
    }

    /**
     * Handles collisions between a player and an enemy
     *
     * @param enemy The enemy
     * @param tile   The tile
     */
    private void handleCollision(EnemyModel enemy, ObstacleTile tile) {
        // TODO: Update so that we dampen the velocity of enemy
//        float vx1 = enemy.getVelocity().x;
//        float vy1 = enemy.getVelocity().y;
//
//        enemy.setVelocity(-vx1, -vy1);
    }
}