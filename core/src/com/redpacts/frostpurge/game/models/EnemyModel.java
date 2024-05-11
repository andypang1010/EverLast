package com.redpacts.frostpurge.game.models;


import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.redpacts.frostpurge.game.assets.AssetDirectory;

import com.redpacts.frostpurge.game.controllers.CollisionController;
import com.redpacts.frostpurge.game.util.EnemyStates;
import com.redpacts.frostpurge.game.util.FilmStrip;
import com.redpacts.frostpurge.game.util.TileGraph;
import com.redpacts.frostpurge.game.views.GameCanvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnemyModel extends CharactersModel{

    /*
    FSM
     */
    EnemyStates initState;
    EnemyStates currentState;
    private String enemyType;
    private int[] startpatrol;
    private int[] endpatrol;
    private int enemyID;
    private ArrayList<int[]> waypoints;
    public int getID(){
        return enemyID;
    }

    private Sound quackSound;
    private long quackId;

    /**
     * Instantiates the player with their starting location and angle and with their texture
     * @param position vector2 representing the starting location
     * @param rotation float representing angle the player is facing
     */
    public EnemyModel(Vector2 position, float rotation, AssetDirectory directory, int[] startpatrol, EnemyStates initState, int id){
        this.position = position;
        this.rotation = rotation;
        this.velocity = new Vector2(0,0);
        this.radius = 3.19f;
        this.enemyType = "duck";

        //texture = new TextureRegion(directory.getEntry( "EnemyLR", Texture.class )).getTexture();

        Texture duck = new TextureRegion(directory.getEntry("EnemyLR", Texture.class)).getTexture();
        idleright = new FilmStrip(duck, 1, 8, 8);
        idleleft = idleright;
        idleup = idleright;

        run_right = new FilmStrip(duck, 1, 8, 8);
//        run_right.setFrame(4);

        TextureRegion left = new TextureRegion(directory.getEntry( "EnemyLR", Texture.class ));
        left.flip(false,true);

        run_left = new FilmStrip(left.getTexture(),1,8,8);
//        run_left.setFrame(4);

        Texture up= new TextureRegion(directory.getEntry( "EnemyUp", Texture.class )).getTexture();
        run_up = new FilmStrip(up, 1, 7, 7);

        Texture down = new TextureRegion(directory.getEntry( "EnemyDown", Texture.class )).getTexture();
        run_down = new FilmStrip(down, 1, 8, 8);

        quackSound = directory.getEntry("Quack", Sound.class);
        quackId = -1;

        this.startpatrol = startpatrol;
        type = "enemy";

        this.initState = initState;
        currentState = initState;
        waypoints = new ArrayList<int[]>(6);
        addWaypoint(this.startpatrol,0);
        enemyID = id;
    }

    /**
     * Instantiates the player with their starting location and angle and with their texture
     * @param position vector2 representing the starting location
     * @param rotation float representing angle the player is facing
     */
    public EnemyModel(Vector2 position, float rotation, AssetDirectory directory, int[] startpatrol, EnemyStates initState, int id, String enemyType){
        this.position = position;
        this.rotation = rotation;
        this.velocity = new Vector2(0,0);
        this.enemyType = enemyType;
        if(Objects.equals(enemyType, "flies")){
            this.radius = 10f;
        }else{
            this.radius = 3.19f;
        }

        if(Objects.equals(this.enemyType, "duck")){
            Texture duck = new TextureRegion(directory.getEntry("EnemyLR", Texture.class)).getTexture();
            idleright = new FilmStrip(duck, 1, 8, 8);
            idleleft = idleright;
            idleup = idleright;

            run_right = new FilmStrip(duck, 1, 8, 8);
//        run_right.setFrame(4);

            TextureRegion left = new TextureRegion(directory.getEntry( "EnemyLR", Texture.class ));
            left.flip(false,true);

            run_left = new FilmStrip(left.getTexture(),1,8,8);
//        run_left.setFrame(4);

            Texture up= new TextureRegion(directory.getEntry( "EnemyUp", Texture.class )).getTexture();
            run_up = new FilmStrip(up, 1, 7, 7);

            Texture down = new TextureRegion(directory.getEntry( "EnemyDown", Texture.class )).getTexture();
            run_down = new FilmStrip(down, 1, 8, 8);

            quackSound = directory.getEntry("Quack", Sound.class);
            quackId = -1;
        }else if(Objects.equals(this.enemyType, "bat")){
            Texture bat = new TextureRegion(directory.getEntry("EnemyBat", Texture.class)).getTexture();
            idleright = new FilmStrip(bat, 1, 5, 5);
            idleleft = idleright;
            idleup = idleright;
            run_right = new FilmStrip(bat, 1, 5, 5);

            TextureRegion left = new TextureRegion(directory.getEntry( "EnemyBat", Texture.class ));
            left.flip(false,true);

            run_left = new FilmStrip(left.getTexture(),1,5,5);
            run_up = new FilmStrip(bat, 1, 5, 5);
            run_down = new FilmStrip(bat, 1, 5, 5);

            quackSound = directory.getEntry("Quack", Sound.class);
            quackId = -1;
        }else if(Objects.equals(this.enemyType, "flies")){
            Texture flies = new TextureRegion(directory.getEntry("EnemyFly", Texture.class)).getTexture();
            idleright = new FilmStrip(flies, 1, 6, 6);
            idleleft = idleright;
            idleup = idleright;
            run_right = new FilmStrip(flies, 1, 6, 6);

            TextureRegion left = new TextureRegion(directory.getEntry( "EnemyFly", Texture.class ));
            left.flip(false,true);

            run_left = new FilmStrip(left.getTexture(),1,6,6);
            run_up = new FilmStrip(flies, 1, 6, 6);
            run_down = new FilmStrip(flies, 1, 6, 6);

            quackSound = directory.getEntry("Quack", Sound.class);
            quackId = -1;
        }

        this.startpatrol = startpatrol;
        type = "enemy";

        this.initState = initState;
        currentState = initState;
        waypoints = new ArrayList<int[]>(6);
        addWaypoint(this.startpatrol,0);
        enemyID = id;
    }

    public void addWaypoint(int[] newPoint, int id){
        this.waypoints.add(id,newPoint);
    }
    public ArrayList<int[]> getWaypoints(){
        waypoints.trimToSize();
        System.out.println("Waypoints");
        for (int i = 0; i<waypoints.size();i++){
            System.out.println(waypoints.get(i)[0]);
            System.out.println(waypoints.get(i)[1]);
        }
        return waypoints;
    }
    public EnemyStates getInitState() {return initState;}
    public void setInitState(EnemyStates initState) {this.initState = initState;}
    public EnemyStates getCurrentState() {return currentState;}
    public void setCurrentState(EnemyStates currentState) {
        this.currentState = currentState;
    }

    public float getPositionY() {
        if(Objects.equals(this.enemyType, "bat")){
            return position.y - 256;
        }else{
            return position.y;
        }
    }

    public String getEnemyType(){
        return this.enemyType;
    }

    /*
    VISION CONE
     */
    public class Vector2Triple {
        public Vector2 first;
        public Vector2 second;
        public Vector2 third;

        public Vector2Triple(Vector2 first, Vector2 second, Vector2 third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
    }
    List<Vector2Triple> triangles = new ArrayList<>();
    public void setTriangle(Vector2 v1, Vector2 v2, Vector2 v3) {
//        triangles.add(new Vector2Triple(v1.cpy(), v2.cpy(), v3.cpy()));
    }
    public List<Vector2Triple> getTriangles() {return triangles;}

    /*
    PHYSICS
     */
    @Override
    public void activatePhysics(World world) {
        // Create and configure the enemy's physics body and fixtures
    }

    @Override
    public void deactivatePhysics(World world) {
        // Destroy the enemy's physics body from the world
    }

    public void createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set the position of the enemy body
        bodyDef.position.set(this.getPosition().cpy().scl(0.1f).add(0, 16f));

        body = world.createBody(bodyDef);
        body.setUserData(this);

        // TODO: getTexture is not scaled...
//        shape.setAsBox((float) this.getTexture().getWidth() / 2,
//                (float) this.getTexture().getHeight() / 2);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        this.shape = shape;

        // TODO: Adjust parameters as necessary
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.015f / (radius * radius * 3.14f);
        fixtureDef.friction = 0.3f;

        fixtureDef.filter.categoryBits = CollisionController.PhysicsConstants.CATEGORY_ENEMY;
        if(Objects.equals(this.enemyType, "bat")){
            fixtureDef.filter.maskBits = (short)(CollisionController.PhysicsConstants.CATEGORY_ENEMY);
        }else if(Objects.equals(this.enemyType, "flies")){
            fixtureDef.filter.maskBits = (short)(CollisionController.PhysicsConstants.CATEGORY_PLAYER |
                    CollisionController.PhysicsConstants.CATEGORY_ENEMY);
            fixtureDef.isSensor = true;
        }else{
            fixtureDef.filter.maskBits = (short)(CollisionController.PhysicsConstants.CATEGORY_PLAYER |
                    CollisionController.PhysicsConstants.CATEGORY_ENEMY |
                    CollisionController.PhysicsConstants.CATEGORY_OBSTACLE |
                    CollisionController.PhysicsConstants.CATEGORY_DESTRUCTIBLE |
                    CollisionController.PhysicsConstants.CATEGORY_BOUNCY);
        }
        // Setting category and mask bits for the enemy


        body.createFixture(fixtureDef);
        shape.dispose(); // Always dispose shapes after use
    }

    public Sound getQuack() {
        return quackSound;
    }

    public void setQuack(Sound sound) {
        quackSound = sound;
    }

    public long getQuackId() {
        return quackId;
    }

    public void setQuackId(long id) {
        quackId = id;
    }
}