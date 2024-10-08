package com.redpacts.frostpurge.game.controllers;

import java.util.Iterator;
import java.util.Objects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.physics.box2d.*;

import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.redpacts.frostpurge.game.models.*;
import com.redpacts.frostpurge.game.util.EnemyStates;
import com.redpacts.frostpurge.game.util.GameContactListener;
import com.redpacts.frostpurge.game.util.PooledList;


public class CollisionController{
    public static class PhysicsConstants {
        public static final short CATEGORY_EMPTY = 0x0001;           // 1
        public static final short CATEGORY_PLAYER = 0x0002;          // 2
        public static final short CATEGORY_ENEMY_DUCK = 0x0004;      // 4
        public static final short CATEGORY_ENEMY_BAT = 0x0008;       // 8
        public static final short CATEGORY_ENEMY_FLIES = 0x0010;     // 16
        public static final short CATEGORY_OBSTACLE = 0x0020;        // 32
        public static final short CATEGORY_DESTRUCTIBLE = 0x0040;    // 64
        public static final short CATEGORY_BOUNCY = 0x0080;          // 128
        public static final short CATEGORY_SWAMP = 0x0100;           // 256

    }
    /** Reference to the game board */
    public LevelModel board;
    /** Reference to the player in the game */
    public PlayerModel player;
    /** Reference to all the enemies in the game */
    public Array<EnemyModel> enemies;
    /** Reference to all the bouncies in the game */
    public Array<BouncyTile> bouncy;
    /** Reference to all the breakables in the game */
    public Array<BreakableTile> breakables;
    public GoalTile goal;
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
    /** Offset of vision cone */
    protected Vector2 offsetVisionCone = new Vector2();

    /** The amount of time for a physics engine step. */
    public static final float WORLD_STEP = 1/60.0f;
    /** Number of velocity iterations for the constraint solvers */
    public static final int WORLD_VELOC = 14;
    /** Number of position iterations for the constraint solvers */
    public static final int WORLD_POSIT = 12;

    /** Width of the game world in Box2d units */
    protected static final float DEFAULT_WIDTH  = 32.0f;
    /** Height of the game world in Box2d units */
    protected static final float DEFAULT_HEIGHT = 18.0f;
    /** All the objects in the world. */
    protected PooledList<GameObject> objects  = new PooledList<GameObject>();
    private float spillageTimer;
    private float swampx;
    private float swampy;
    private boolean vacuuming = false;
    private GameContactListener contactListener;

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
    protected CollisionController(LevelModel board, PlayerModel player, Array<EnemyModel> enemies, Array<BouncyTile> bouncy,
                                  Array<BreakableTile> breakables, GoalTile goal, float width, float height,AssetDirectory assets, float volume) {
        this(board, player, enemies, bouncy, breakables, goal, new Rectangle(0,0,width,height),assets, volume);
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
    protected CollisionController(LevelModel board, PlayerModel player, Array<EnemyModel> enemies, Array<BouncyTile> bouncy, Array<BreakableTile> breakables, GoalTile goal, Rectangle bounds, AssetDirectory assets, float volume) {
        this.board = board;
        this.player = player;
        this.enemies = enemies;
        this.bouncy = bouncy;
        this.breakables = breakables;
        this.goal = goal;

        world = new World(new Vector2(),false);
        this.bounds = new Rectangle(bounds);
        this.scale = new Vector2(1,1);

        if (player != null) {
            player.createBody(world);
            System.out.println("PLAYER CREATED");
            addObject(player);
        }
        for (EnemyModel e : enemies) {
            if (e != null) {
                e.createBody(world);
                addObject(e);
            }
        }
        if (goal != null) {
            goal.createBody(world);
            System.out.println("GOAL CREATED");
            addObject(goal);
        }
        for (TileModel[] t : board.getExtraLayer()) {
            for (TileModel tile : t){
                if (tile != null) {
                    tile.createBody(world);
                    addObject(tile);
                }
            }
        }
        for (BouncyTile b: bouncy) {
            System.out.println("BOUNCE");
            if (b != null) {
                b.createBody(world);
                addObject(b);
            }
        }
        for (BreakableTile b: breakables) {
            System.out.println("BREAK");
            if (b != null) {
                b.createBody(world);
                addObject(b);
            }
        }
        contactListener = new GameContactListener(world, board, assets, volume);
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
        contactListener.updateTime(Gdx.graphics.getDeltaTime());
        pickPowerUp((PlayerModel) player);
        for (EnemyModel e : enemies){
            if(!Objects.equals(e.getEnemyType(), "bat")){
                checkEnemyVision(e);
            }
        }
        postUpdate(1/60f);
    }
    /**
     * Check if the player is on a swamp tile, and pick up the power up if true
     *
     * @param player Player to check
     */
    private void pickPowerUp(PlayerModel player){
        spillageTimer += Gdx.graphics.getDeltaTime();
        if(board.isSwampTile(player.getPosition().x, player.getPosition().y)) {
            if (player.getVacuumingProgression() == 0) {
                spillageTimer = 0;
                player.setVacuumingProgression(1);
                swampx = player.getPosition().x;
                swampy = player.getPosition().y;
                vacuuming = true;
            }
        }
        if (vacuuming) {
            if (spillageTimer > .3) {

                board.removeSwamp(swampx, swampy);
                player.addCanBoost(2);
                spillageTimer = 0;
                vacuuming = false;
            }
        }
    }
//    /**
//     * Check if the player is on a swamp tile, and pick up the power up if true
//     *
//     * @param player Player to check
//     */
//    private void win(PlayerModel player){
//        if(board.isGoalTile(player.getPosition().x, player.getPosition().y)){
//            player.setWin(true);
//        }
//    }
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

    // TODO: Fix logic check of vision cone
    private static class VisionConeCallback implements RayCastCallback {
        public boolean hitObstacle = false;
        public boolean canSeeTarget = false;
        public Vector2 hitPoint = null;

        public void clearHitPoint() {
            hitPoint = null;
        }
        public Vector2 getHitPoint() {
            if (hitPoint != null) return hitPoint.cpy();
            else return null;
        }
        // Returning 1 continues the ray cast to the end of its path
        // Returning fraction or 0 would terminate the ray cast here
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            GameObject userData = (GameObject) fixture.getBody().getUserData();
            if (userData instanceof EnemyModel || userData instanceof SwampTile){
                return 1; // Ray cast continues
            } else if (userData instanceof ObstacleTile || userData instanceof BouncyTile ||
                    (userData instanceof BreakableTile && ((BreakableTile) userData).isActive())) {
                hitObstacle = true;
                hitPoint = point.cpy();
                return fraction; // Ray cast ends here
            } else if (userData instanceof PlayerModel) {
                canSeeTarget = true;
                return 1;
            }
            return 1;
        }
    }

    /**
     * Create vision cone for enemy, where it will check for player.
     *
     * The vision cone will scan every object, and flags when it hits obstacle or player.
     *
     * @param enemy	    The vision cone of enemy
     */
    // TODO: Perhaps change parameters so that we can customize vision cone (length/fov).
    public void checkEnemyVision(EnemyModel enemy) {
        // Create the callback instance
        VisionConeCallback callback = new VisionConeCallback();

        // Calculating offset

        switch(getDirection(enemy.getBody().getLinearVelocity())) {
            case "right":
                offsetVisionCone = new Vector2(3f, 6f);
                break;
            case "left":
                offsetVisionCone = new Vector2(-3f, 6f);
                break;
            case "up":
            case "down":
                offsetVisionCone = new Vector2(0, 3f);
                break;
            default:
                offsetVisionCone = new Vector2();
                break;
        }

        // Calculate the end point of the vision cone based on the enemy's direction and range
        Vector2 rayStart = enemy.getBody().getPosition().cpy().add(offsetVisionCone);
//        System.out.println(rayStart);
        float fov = 45f; // Field of view angle in degrees
        int numRays = 20; // Number of rays to cast within the fov
        float deltaAngle = fov / (numRays - 1); // Angle between each ray

        // Calculate the direction vector based on enemy's rotation
        Vector2 direction = enemy.getBody().getLinearVelocity().cpy().nor(); // Normalize the direction vector

        float angle = -fov / 2; // Calculate current angle
        Vector2 rayDirection = direction.cpy().rotateDeg(angle); // Rotate direction vector by current angle
        Vector2 rayEnd = rayStart.cpy().add(rayDirection.scl(40f)); // Calculate end point of the ray
        world.rayCast(callback, rayStart, rayEnd);

        if (callback.getHitPoint() != null) { // Record where the ray cast end/collided with obstacle
            rayEnd = callback.getHitPoint();
            callback.clearHitPoint();
        }
        Vector2 rayPrevious = rayEnd.cpy();

        for (int i = 1; i < numRays; i++) {
            angle = -fov / 2 + deltaAngle * i;
            rayDirection = direction.cpy().rotateDeg(angle);
            rayEnd = rayStart.cpy().add(rayDirection.scl(40f));
            world.rayCast(callback, rayStart, rayEnd);

            if (callback.getHitPoint() != null) {
                rayEnd = callback.getHitPoint();
                callback.clearHitPoint();
            }
            enemy.setTriangle(rayStart.cpy().scl(10).add(-100, -100),
                    rayPrevious.cpy().scl(10).add(-100, -100),
                    rayEnd.cpy().scl(10).add(-100, -100)); // Add triangle to draw vision cone.
            rayPrevious = rayEnd.cpy();
        }

        if (callback.canSeeTarget) {
            // Enemy can see the target
//            System.out.println("Target spotted!");
            enemy.setCurrentState(EnemyStates.CHASE);
        } else if (callback.hitObstacle){
            // Vision blocked or target not in sight
//            System.out.println("Obstacle hit.");
        } else{
//            System.out.println("Target not spotted :(");
            enemy.setCurrentState(enemy.getCurrentState());
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

    /**
     * helper to find which animation to draw
     * @param v
     * @return the direction that the animtion should face
     */
    public String getDirection(Vector2 v) {
        float x = v.x;
        float y = v.y;
        float angle = (float) Math.toDegrees(Math.atan2(y,x));
        if (angle <= 45 && angle >= -45){
            return "right";
        } else if (angle>=45 && angle<=135) {
            return "up";
        } else if (angle >= 135 || angle <=-135) {
            return "left";
        }else{
            return "down";
        }
    }
}