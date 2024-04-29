package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.redpacts.frostpurge.game.controllers.CollisionController;
import com.redpacts.frostpurge.game.util.FilmStrip;
import com.redpacts.frostpurge.game.views.GameCanvas;

public class PlayerModel extends CharactersModel {
    public enum Actions {
        ACCELERATE,
        BOOST
    }

    private static final int MAX_BOOST = 4;
    private static final int BOOST_COOL_DOWN = 60;
    private float hp;

    private Texture fire;
    private Texture fireBoost;

    private Sound accelerateSound;
    private long accelerateId;
    private Sound boostSound;
    private long boostId;
    private boolean alive;
    private int boostNum;
    private int boostCoolDown;
    private int invincibility;
    boolean won = false;

    @Override
    public void activatePhysics(World world) {
        // Create and configure the player's physics body and fixtures
    }

    @Override
    public void deactivatePhysics(World world) {
        // Destroy the player's physics body from the world
    }
    /**
     * Instantiates the player with their starting location and angle and with their texture
     *
     * @param position vector2 representing the starting location
     * @param rotation float representing angle the player is facing
     */
    public PlayerModel(Vector2 position, float rotation, AssetDirectory directory) {
        this.position = position;
        this.rotation = rotation;
        this.velocity = new Vector2(0, 0);
        this.hp = 100;
        this.invincibility = 0;
        this.alive = true;
        this.radius = 3.19f;

        Texture idle_right = new TextureRegion(directory.getEntry("Liv_Idle_Right", Texture.class)).getTexture();
        idleright = new FilmStrip(idle_right, 1, 3, 3);
        Texture idle_left = new TextureRegion(directory.getEntry("Liv_Idle_Left", Texture.class)).getTexture();
        idleleft = new FilmStrip(idle_left, 1, 3, 3);
        Texture idle_up = new TextureRegion(directory.getEntry("Liv_Idle_Up", Texture.class)).getTexture();
        idleup = new FilmStrip(idle_up, 1, 3, 3);

        Texture run_left_text = new TextureRegion(directory.getEntry("Liv_Run_Left", Texture.class)).getTexture();
        run_left = new FilmStrip(run_left_text, 1, 8, 8);
        Texture run_right_text = new TextureRegion(directory.getEntry("Liv_Run_Right", Texture.class)).getTexture();
        run_right = new FilmStrip(run_right_text, 1, 8, 8);
        Texture run_down_text = new TextureRegion(directory.getEntry("Liv_Run_Left", Texture.class)).getTexture(); // NOT THE CORRECT ONE YET
        run_down = new FilmStrip(run_down_text, 1, 8, 8);
        Texture run_up_text = new TextureRegion(directory.getEntry("Liv_Run_Up", Texture.class)).getTexture();
        run_up = new FilmStrip(run_up_text, 1, 8, 8);

        // TODO: Import actual audio assets
        accelerateSound = directory.getEntry("Accelerate", Sound.class);
        accelerateId = -1;
        setActionSound(Actions.ACCELERATE, accelerateSound);

        boostSound = directory.getEntry("Boost", Sound.class);
        boostId = -1;
        setActionSound(Actions.BOOST, boostSound);

        fire = new TextureRegion(directory.getEntry("Fire", Texture.class)).getTexture();
        fireBoost = new TextureRegion(directory.getEntry("FireBoost", Texture.class)).getTexture();
        alive = true;
        this.boostNum = 0;
        this.boostCoolDown = 0;
        type = "player";
    }

    public float getHp(){
        return hp;
    }
    public void setHp(float hp){
        this.hp = hp;
        if(this.hp < 0){
            this.hp = 0;
        }
    }
    public void addHp(float hp){
        this.hp += hp;
        if(this.hp < 0){
            this.hp = 0;
        }
    }
    public float getInvincibility(){
        return invincibility;
    }
    public void setInvincibility(int i){
        this.invincibility = i;
        if(this.invincibility < 0){
            this.invincibility = 0;
        }
    }
    public void addInvincibility(int i){
        this.invincibility += i;
        if(this.invincibility < 0){
            this.invincibility = 0;
        }
    }

    public Sound getActionSound(Actions action) {
        switch (action) {
            case ACCELERATE:
                return accelerateSound;
            case BOOST:
                return boostSound;
        }
        assert false : "Invalid action enumeration";
        return null;
    }

    public void setActionSound(Actions action, Sound sound) {
        switch (action) {
            case ACCELERATE:
                accelerateSound = sound;
                break;
            case BOOST:
                boostSound = sound;
                break;
            default:
                assert false : "Invalid action enumeration";
                break;
        }
    }

    public long getActionId(Actions action) {
        switch (action) {
            case ACCELERATE:
                return accelerateId;
            case BOOST:
                return boostId;
        }
        assert false : "Invalid action enumeration";
        return -1;
    }

    public void setActionId(Actions action, long id) {
        switch (action) {
            case ACCELERATE:
                accelerateId = id;
                break;
            case BOOST:
                boostId = id;
                break;
            default:
                assert false : "Invalid action enumeration";
        }
    }

    public boolean isAlive(){return alive && this.hp > 0;}
    public void die(){alive = false;}
    public void win(){won = true;}
    public boolean didwin(){
        return won;
    }

    public float getPositionY(){
        return this.position.y - 250;
    }

    public int getBoostNum() {
        return boostNum;
    }

    public void addCanBoost(int b) {
        boostNum += b;
        if(boostNum > MAX_BOOST){
            boostNum = MAX_BOOST;
        }else if(boostNum < 0){
            boostNum = 0;
        }
    }

    public int getBoostCoolDown(){
        return boostCoolDown;
    }

    public void addBoostCoolDown(int t){
        boostCoolDown += t;
        if(boostCoolDown > BOOST_COOL_DOWN){
            boostCoolDown = BOOST_COOL_DOWN;
        }else if(boostCoolDown < 0){
            boostCoolDown = 0;
        }
    }

    public void resetBoostCoolDown(){
        boostCoolDown = BOOST_COOL_DOWN;
    }

    public void createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set the position of the player body
        bodyDef.position.set(this.getPosition().cpy().scl(0.1f));

        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.setSleepingAllowed(false);
        body.setFixedRotation(true);
        body.setLinearDamping(0);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        this.shape = shape;

        // TODO: Adjust parameters as necessary
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.015f / (radius * radius * 3.14f);
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.25f;

        // Setting category and mask bits for the player
        fixtureDef.filter.categoryBits = CollisionController.PhysicsConstants.CATEGORY_PLAYER;
        fixtureDef.filter.maskBits = (CollisionController.PhysicsConstants.CATEGORY_ENEMY |
                CollisionController.PhysicsConstants.CATEGORY_OBSTACLE |
                CollisionController.PhysicsConstants.CATEGORY_SWAMP |
                CollisionController.PhysicsConstants.CATEGORY_DESTRUCTIBLE);

        body.createFixture(fixtureDef);
        shape.dispose(); // Always dispose shapes after use
    }

    public void movePlayer(Vector2 strength){
        body.applyForceToCenter(strength, true);
    }

    public void drawFire(GameCanvas canvas){
        if (body.getLinearVelocity().len() > 65){
            canvas.draw(fireBoost, Color.WHITE, (float) fireBoost.getWidth() / 2 + 275 , (float) fireBoost.getHeight() / 2, position.x , position.y+ 125, getRotation(),.5f,.5f,false);
        }else{
            canvas.draw(fire, Color.WHITE, (float) fire.getWidth() / 2 + 250 , (float) fire.getHeight() / 2, position.x , position.y+ 125, getRotation(),.5f,.5f,false);
        }

    }
}

