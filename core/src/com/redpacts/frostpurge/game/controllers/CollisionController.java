package com.redpacts.frostpurge.game.controllers;

import java.util.Iterator;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.physics.box2d.*;

import com.redpacts.frostpurge.game.models.*;
import com.redpacts.frostpurge.game.util.GameContactListener;
import com.redpacts.frostpurge.game.util.PooledList;


public class CollisionController{
    public static class PhysicsConstants {
        public static final short CATEGORY_PLAYER = 0x0001;
        public static final short CATEGORY_ENEMY = 0x0002;
        public static final short CATEGORY_OBSTACLE = 0x0004;
        public static final short CATEGORY_SWAMP = 0x0008;
        public static final short CATEGORY_DESTRUCTIBLE = 0x0016;
    }
    /** Reference to the game board */
    public LevelModel board;
    /** Reference to the player in the game */
    public PlayerModel player;
    /** Reference to all the enemies in the game */
    public Array<EnemyModel> enemies;
    /** Width of the collision geometry */
    protected float width;
    /** Height of the collision geometry */
    protected float height;

    /** The Box2D world */
    protected World world;

    /** The boundary of the world */
    protected Rectangle bounds;
    /** The world scale */
    protected Vector2 scale;
    /** Offset of bounds */
    protected final int offset = 10;

    /** The amount of time for a physics engine step. */
    public static final float WORLD_STEP = 1/60.0f;
    /** Number of velocity iterations for the constraint solvers */
    public static final int WORLD_VELOC = 6;
    /** Number of position iterations for the constraint solvers */
    public static final int WORLD_POSIT = 2;

    /** Width of the game world in Box2d units */
    protected static final float DEFAULT_WIDTH  = 32.0f;
    /** Height of the game world in Box2d units */
    protected static final float DEFAULT_HEIGHT = 18.0f;
    /** All the objects in the world. */
    protected PooledList<GameObject> objects  = new PooledList<GameObject>();

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
     * Creates a new game world with the default values.
     *
     * The game world is scaled so that the screen coordinates do not agree
     * with the Box2d coordinates.  The bounds are in terms of the Box2d
     * world, not the screen.
     *
     * @param board   The game board
     * @param player  The player
     * @param enemies List of enemies
     */
    protected CollisionController(LevelModel board, PlayerModel player, Array<EnemyModel> enemies) {
        this(board, player, enemies, new Rectangle(0,0,DEFAULT_WIDTH,DEFAULT_HEIGHT));
    }

    /**
     * Creates a new game world
     *
     * The game world is scaled so that the screen coordinates do not agree
     * with the Box2d coordinates.  The bounds are in terms of the Box2d
     * world, not the screen.
     *
     * @param board   The game board
     * @param player  The player
     * @param enemies List of enemies
     * @param width  	The width in Box2d coordinates
     * @param height	The height in Box2d coordinates
     */
    protected CollisionController(LevelModel board, PlayerModel player, Array<EnemyModel> enemies, float width, float height) {
        this(board, player, enemies, new Rectangle(0,0,width,height));
    }

    /**
     * Creates a new game world
     *
     * The game world is scaled so that the screen coordinates do not agree
     * with the Box2d coordinates.  The bounds are in terms of the Box2d
     * world, not the screen.
     *
     * @param board   The game board
     * @param player  The player
     * @param enemies List of enemies
     * @param bounds  The game bounds in Box2d coordinates
     */
    protected CollisionController(LevelModel board, PlayerModel player, Array<EnemyModel> enemies, Rectangle bounds) {
        this.board = board;
        this.player = player;
        this.enemies = enemies;

        world = new World(new Vector2(),false);
        this.bounds = new Rectangle(bounds);
        this.scale = new Vector2(1,1);

        // TODO: Add iteration for tiles in board

        if (player != null) {
            player.createBody(world);
            addObject(player);
//            System.out.println(player.getPosition());
        }
        for (EnemyModel e : enemies) {
            if (e != null) {
                e.createBody(world);
                addObject(e);
//                System.out.println(e.getPosition());
            }
        }
        for (TileModel[] t : board.getExtraLayer()) {
            for (TileModel tile : t)
                if (tile != null){
                    tile.createBody(world);
                    addObject(tile);
//                System.out.println(t.getPosition());
//                System.out.println(t.getType());
            }
        }

        GameContactListener contactListener = new GameContactListener();
        world.setContactListener(contactListener);
    }

    /**
     * Dispose of all (non-static) resources allocated to this mode.
     */
    public void dispose() {
//        for(GameObject obj : objects) {
//            obj.deactivatePhysics(world);
//        }
        objects.clear();
        world.dispose();
        objects = null;
        bounds = null;
        scale  = null;
        world  = null;
    }


    /**
     * Immediately adds the object to the physics world
     *
     * param obj The object to add
     */
    protected void addObject(GameObject obj) {
//        assert inBounds(obj) : "Object is not in bounds";
        objects.add(obj);
//        obj.activatePhysics(world);
    }

    /**
     * Returns true if the object is in bounds.
     *
     * This assertion is useful for debugging the physics.
     *
     * @param obj The object to check.
     *
     * @return true if the object is in bounds.
     */
//    public boolean inBounds(GameObject obj) {
//        boolean horiz = (bounds.x <= obj.getX() && obj.getX() <= bounds.x+bounds.width);
//        boolean vert  = (bounds.y <= obj.getY() && obj.getY() <= bounds.y+bounds.height);
//        return horiz && vert;
//    }

    /**
     * Update all collisions
     *
     */
    public void update() {
        // TODO: Implement dt here
        // TODO: Not hard code check bound...
        pickPowerUp((PlayerModel) player);
        for (EnemyModel e : enemies){
            checkEnemyVision(e, player);
        }
        postUpdate(1/60f);
    }
    /**
     * Check if the player is on a swamp tile, and pick up the power up if true
     *
     * @param player Player to check
     */
    private void pickPowerUp(PlayerModel player){
        if(board.isSwampTile(player.getPosition().x, player.getPosition().y)){
            board.removeExtra(player.getPosition().x, player.getPosition().y);
            player.setCanBoost(true);
        }
    }
    /**
     * Processes physics
     *
     * Once the update phase is over, but before we draw, we are ready to handle
     * physics.  The primary method is the step() method in world.  This implementation
     * works for all applications and should not need to be overwritten.
     *
     * @param dt	Number of seconds since last animation frame
     */
    public void postUpdate(float dt) {
        // Turn the physics engine crank.
        world.step(WORLD_STEP,WORLD_VELOC,WORLD_POSIT);

        // TODO: Implement activate, deactivate physics
        // Garbage collect the deleted objects.
        // Note how we use the linked list nodes to delete O(1) in place.
        // This is O(n) without copying.
        Iterator<PooledList<GameObject>.Entry> iterator = objects.entryIterator();
        while (iterator.hasNext()) {
            PooledList<GameObject>.Entry entry = iterator.next();
            GameObject obj = entry.getValue();
            if (obj.isRemoved()) {
                obj.deactivatePhysics(world);
                entry.remove();
            } else {
                // Note that update is called last!
                obj.update(dt);
            }
        }
    }

    private static class VisionConeCallback implements RayCastCallback {
        public boolean hitObstacle = false;
        public boolean canSeeTarget = false;
        private final Fixture targetFixture;

        public VisionConeCallback(Fixture targetFixture) {
            this.targetFixture = targetFixture;
        }

        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            // Check if the fixture is the target
            if (fixture == targetFixture) {
                canSeeTarget = true;
            } else {
                // Assuming any other fixture is an obstacle
                hitObstacle = true;
            }

            // Returning 1 continues the ray cast to the end of its path
            // Returning fraction would terminate the ray cast here
            // Choose based on whether you want to detect obstacles behind the target
            return hitObstacle ? 0 : 1;
        }
    }
    public void checkEnemyVision(EnemyModel enemy, PlayerModel player) {
        // TODO: Not make end point fixed on players, but rather just relative to enemy
        // Calculate the end point of the vision cone based on the enemy's direction and range
        Vector2 start = enemy.getPosition().cpy();
        Vector2 end1 =  start.cpy().add(-400f, 50f);
        Vector2 end2 =  start.cpy().add(-400f, 0f);
        Vector2 end3 =  start.cpy().add(-400f, -50f);

        // Create the callback instance
        VisionConeCallback callback = new VisionConeCallback(player.getBody().getFixtureList().first());

        // Perform the ray cast
        world.rayCast(callback, start, end1);
        world.rayCast(callback, start, end2);
        world.rayCast(callback, start, end3);


        if (callback.canSeeTarget && !callback.hitObstacle) {
            // Enemy can see the target
            //System.out.println("Target spotted!");
        } else {
            // Vision blocked or target not in sight
            //System.out.println("Can't see the target.");
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



//    /**
//     * Handles collisions between enemies, causing them to bounce off one another.
//     *
//     * This method updates the velocities of both enemies: the collider and the
//     * collidee. Therefore, you should only call this method for one of the
//     * ships, not both. Otherwise, you are processing the same collisions
//     * twice.
//     *
//     * @param enemy1 The collider
//     * @param enemy2 The collidee
//     */
//    private void checkForCollision(EnemyModel enemy1, EnemyModel enemy2) {
//        // TODO: Update so that we get position of enemy
//        // Got the positions for each ship
//        float x1 = enemy1.getPosition().x;
//        float y1 = enemy1.getPosition().y;
//        float x2 = enemy2.getPosition().x;
//        float y2 = enemy2.getPosition().y;
//
//        // TODO: Update logic of guard check and location update such that it will account for enemy size.
//        // TODO: Update logic so that it will change velocity properly.
//        // If the two enemies collide
//        if (x1 == x2 && y1 == y2) {
//            float vx1 = enemy1.getVelocity().x;
//            float vy1 = enemy1.getVelocity().y;
//            float vx2 = enemy2.getVelocity().x;
//            float vy2 = enemy2.getVelocity().y;
//
//            enemy1.setVelocity(-vx1, -vy1);
//            enemy2.setVelocity(-vx2, -vy2);
//        }
//    }
//
///**
// * Check player for being out-of-bounds.
// *
// * @param player Player to check
// */
//private void processBound(PlayerModel player) {
//    // Do not let the player go off screen.
//    if (player.getPosition().x <= 0) {
//        player.setPosition(0, player.getPosition().y);
//        player.setVelocity(-player.getVelocity().x/5, player.getVelocity().y);
//    } else if (player.getPosition().y <= 0) {
//        player.setPosition(player.getPosition().x, 0);
//        player.setVelocity(player.getVelocity().x, -player.getVelocity().y/5);
//    } else if (player.getPosition().x >= getWidth()) {
//        player.setPosition(getWidth(), player.getPosition().y);
//        player.setVelocity(-player.getVelocity().x/5, player.getVelocity().y);
//    } else if (player.getPosition().y >= getHeight()){
//        player.setPosition(player.getPosition().x, getHeight());
//        player.setVelocity(player.getVelocity().x, -player.getVelocity().y/5);
//    }
//
//    if(board.isObstacleTileAtScreen(player.getPosition().x, player.getPosition().y)){
//        float tile_x = player.getPosition().x - board.screenToBoard(player.getPosition().x) * board.getTileSize();
//        float tile_y = player.getPosition().y - board.screenToBoard(player.getPosition().y) * board.getTileSize();
//        float width = board.getTileSize();
//        float half = width / 2f;
//
//        if((tile_y <= tile_x && tile_x <= half) || (tile_y <= width - tile_x && tile_x > half)){
//            player.setPosition(player.getPosition().x, board.screenToBoard(player.getPosition().y) * board.getTileSize());
//            player.setVelocity(player.getVelocity().x, -player.getVelocity().y/5);
//        }else if((tile_x > tile_y && tile_y > half) || (tile_x > width - tile_y && tile_y <= half)){
//            player.setPosition((1 + board.screenToBoard(player.getPosition().x)) * board.getTileSize(), player.getPosition().y);
//            player.setVelocity(-player.getVelocity().x / 5, player.getVelocity().y);
//        }else if((tile_y > tile_x && tile_x > half) || (tile_y > width - tile_x && tile_x <= half)){
//            player.setPosition(player.getPosition().x, (1 + board.screenToBoard(player.getPosition().y)) * board.getTileSize());
//            player.setVelocity(player.getVelocity().x, -player.getVelocity().y/5);
//        }else{
//            player.setPosition(board.screenToBoard(player.getPosition().x) * board.getTileSize(), player.getPosition().y);
//            player.setVelocity(-player.getVelocity().x/5, player.getVelocity().y);
//        }
//    }
//}
//
///**
// * Check enemy for being out-of-bounds.
// *
// * @param enemy Enemy to check
// */
//private void processBound(EnemyModel enemy) {
//    // Do not let the enemy go off screen.
//    if (enemy.getPosition().x <= 0) {
//        enemy.setPosition(0, enemy.getPosition().y);
//        enemy.setVelocity(-enemy.getVelocity().x, enemy.getVelocity().y);
//    } else if (enemy.getPosition().y <= 0) {
//        enemy.setPosition(enemy.getPosition().x, 0);
//        enemy.setVelocity(enemy.getVelocity().x, -enemy.getVelocity().y);
//    } else if (enemy.getPosition().x >= getWidth()) {
//        enemy.setPosition(getWidth(), enemy.getPosition().y);
//        enemy.setVelocity(-enemy.getVelocity().x, enemy.getVelocity().y);
//    } else if (enemy.getPosition().y >= getHeight()){
//        enemy.setPosition(enemy.getPosition().x, getHeight());
//        enemy.setVelocity(enemy.getVelocity().x, -enemy.getVelocity().y);
//    }
//}