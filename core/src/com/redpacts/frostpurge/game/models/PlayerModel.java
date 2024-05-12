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


    public enum VacuumingState{
        NONE,
        START,
        VACUUM,
        END
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
    private int vacuumingProgression;
    private VacuumingState vacuumingState;
    private int boostNum;
    private int boostCoolDown;
    private int invincibility;
    private boolean shake;
    private int INVINCIBILITY_COOLDOWN = 120;
    private int gameOver;
    /**
     * -1 means lose, 1 means win
     */
    private int gameOverState;
    private int GAMEOVER_COOLDOWN = 120;
    private FilmStrip runLeftNormal;
    private FilmStrip runRightNormal;
    private FilmStrip runDownNormal;
    private FilmStrip runUpNormal;
    private FilmStrip idleRightNormal;
    private FilmStrip idleLeftNormal;
    private FilmStrip idleUpNormal;

    private FilmStrip runLeftDamaged;
    private FilmStrip runRightDamaged;
    private FilmStrip runDownDamaged;
    private FilmStrip runUpDamaged;
    private FilmStrip idleRightDamaged;
    private FilmStrip idleLeftDamaged;
    private FilmStrip idleUpDamaged;


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
        this.shake = false;
        this.gameOver = 0;
        this.gameOverState = 0;

        this.vacuumingProgression = 0;
        this.vacuumingState = VacuumingState.NONE;

        this.alive = true;
        this.radius = 3.19f;

        Texture idle_right = new TextureRegion(directory.getEntry("Liv_Idle_Right", Texture.class)).getTexture();
        idleRightNormal = new FilmStrip(idle_right, 1, 3, 3);
        idleright = idleRightNormal;
        idle_right = new TextureRegion(directory.getEntry("Liv_Idle_Right_Damaged", Texture.class)).getTexture();
        idleRightDamaged = new FilmStrip(idle_right, 1, 3, 3);

        Texture idle_left = new TextureRegion(directory.getEntry("Liv_Idle_Left", Texture.class)).getTexture();
        idleLeftNormal = new FilmStrip(idle_left, 1, 3, 3);
        idleleft = idleLeftNormal;
        idle_left = new TextureRegion(directory.getEntry("Liv_Idle_Left_Damaged", Texture.class)).getTexture();
        idleLeftDamaged = new FilmStrip(idle_left, 1, 3, 3);

        Texture idle_up = new TextureRegion(directory.getEntry("Liv_Idle_Up", Texture.class)).getTexture();
        idleUpNormal = new FilmStrip(idle_up, 1, 3, 3);
        idleup = idleUpNormal;
        idle_up = new TextureRegion(directory.getEntry("Liv_Idle_Up_Damaged", Texture.class)).getTexture();
        idleUpDamaged = new FilmStrip(idle_up, 1, 3, 3);

        Texture run_left_text = new TextureRegion(directory.getEntry("Liv_Run_Left", Texture.class)).getTexture();
        runLeftNormal = new FilmStrip(run_left_text, 1, 8, 8);
        run_left = runLeftNormal;
        run_left_text = new TextureRegion(directory.getEntry("Liv_Run_Left_Damaged", Texture.class)).getTexture();
        runLeftDamaged = new FilmStrip(run_left_text, 1, 8, 8);

        Texture run_right_text = new TextureRegion(directory.getEntry("Liv_Run_Right", Texture.class)).getTexture();
        runRightNormal = new FilmStrip(run_right_text, 1, 8, 8);
        run_right = runRightNormal;
        run_right_text = new TextureRegion(directory.getEntry("Liv_Run_Right_Damaged", Texture.class)).getTexture();
        runRightDamaged = new FilmStrip(run_right_text, 1, 8, 8);

        // TODO: CHANGE TO RIGHT TEXTURE
        Texture run_down_text = new TextureRegion(directory.getEntry("Liv_Run_Left", Texture.class)).getTexture();
        runDownNormal = new FilmStrip(run_down_text, 1, 8, 8);
        run_down = runDownNormal;
        run_down_text = new TextureRegion(directory.getEntry("Liv_Run_Left_Damaged", Texture.class)).getTexture();
        runDownDamaged = new FilmStrip(run_down_text, 1, 8, 8);

        Texture run_up_text = new TextureRegion(directory.getEntry("Liv_Run_Up", Texture.class)).getTexture();
        runUpNormal = new FilmStrip(run_up_text, 1, 8, 8);
        run_up = runUpNormal;
        run_up_text = new TextureRegion(directory.getEntry("Liv_Run_Up_Damaged", Texture.class)).getTexture();
        runUpDamaged = new FilmStrip(run_up_text, 1, 8, 8);

        death = new FilmStrip(new TextureRegion(directory.getEntry("Liv_Death", Texture.class)).getTexture(), 1, 7, 7);
        win = new FilmStrip(new TextureRegion(directory.getEntry("Liv_Win", Texture.class)).getTexture(), 1, 8, 8);

        Texture vacuum_start_left_text = new TextureRegion(directory.getEntry("Liv_Vacuum_Start_Left", Texture.class)).getTexture();
        vacuum_start_left = new FilmStrip(vacuum_start_left_text, 1, 4, 4);
        Texture vacuum_left_text = new TextureRegion(directory.getEntry("Liv_Vacuum_Left", Texture.class)).getTexture();
        vacuum_left = new FilmStrip(vacuum_left_text, 1, 3, 3);
        Texture vacuum_end_left_text = new TextureRegion(directory.getEntry("Liv_Vacuum_End_Left", Texture.class)).getTexture();
        vacuum_end_left = new FilmStrip(vacuum_end_left_text, 1, 4, 4);

        Texture vacuum_start_right_text = new TextureRegion(directory.getEntry("Liv_Vacuum_Start_Right", Texture.class)).getTexture();
        vacuum_start_right = new FilmStrip(vacuum_start_right_text, 1, 4, 4);
        Texture vacuum_right_text = new TextureRegion(directory.getEntry("Liv_Vacuum_Right", Texture.class)).getTexture();
        vacuum_right = new FilmStrip(vacuum_right_text, 1, 3, 3);
        Texture vacuum_end_right_text = new TextureRegion(directory.getEntry("Liv_Vacuum_End_Right", Texture.class)).getTexture();
        vacuum_end_right = new FilmStrip(vacuum_end_right_text, 1, 4, 4);

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

    private void setFilmStripNormal(){
        run_left = runLeftNormal;
        run_right = runRightNormal;
        run_down = runDownNormal;
        run_up = runUpNormal;
        idleright = idleRightNormal;
        idleleft = idleLeftNormal;
        idleup = idleUpNormal;
    }
    private void setFilmStripDamaged(){
        run_left = runLeftDamaged;
        run_right = runRightDamaged;
        run_down = runDownDamaged;
        run_up = runUpDamaged;
        idleright = idleRightDamaged;
        idleleft = idleLeftDamaged;
        idleup = idleUpDamaged;
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

    public boolean getShake(){
        return this.shake;
    }

    public void setShake(boolean s){
        this.shake = s;
    }

    /**
     * Return true if invincibility frame is in range [1; INVINCIBILITY_COOLDOWN], and false otherwise
     */
    public boolean getInvincibility(){
        return 1 <= invincibility && invincibility <= INVINCIBILITY_COOLDOWN;
    }

    /**
     * Start invincibility frame at 1 to start it.
     * Effect: Set invincibility frame, and change current filmstrip to appropriate filmstrip.
     */

    public void startInvincibility(){
        this.invincibility = 1;
        setFilmStripDamaged();
    }

    /**
     * Increase invincibility frame
     * Effect: If frame is out of range, reset to 0
     */
    public void addInvincibility(){
        this.invincibility += 1;
        if (this.invincibility < 0 || this.invincibility > INVINCIBILITY_COOLDOWN) {
            this.invincibility = 0;
            setFilmStripNormal();
        }
    }
    /**
     * Return true if gameover frame is in range [1; GAMEOVER_COOLDOWN], and false otherwise
     */
    public boolean getGameOver(){
        return 1 <= gameOver && gameOver <= GAMEOVER_COOLDOWN;
    }
    /**
     * Start gameover frame at 1.
     * Effect: Set gameover frame
     */

    public void startGameOver(){
        this.gameOver = 1;
    }

    /**
     * Increase game over frame
     * Effect: If frame is out of range, reset to 0
     */
    public void addGameOver(){
        this.gameOver += 1;
        if (this.gameOver < 0 || this.gameOver > GAMEOVER_COOLDOWN) {
            this.gameOver = 0;
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


    public void setGameOverState(int i) { gameOverState = i;}
    public int getGameOverState() {return gameOverState;}

    public boolean isAlive(){return alive && this.hp > 0;}
    public void die(){alive = false;}
    public void setWin(boolean won){this.won = won;}
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

    public int getVacuumingProgression(){
        return this.vacuumingProgression;
    }

    public void addVacuumingProgression(int v){
        this.vacuumingProgression += v;
        if(this.vacuumingProgression > 30){
            this.vacuumingProgression = 0;
        }
    }

    public boolean isVacuuming(){
        return this.vacuumingProgression >= 31 && this.vacuumingProgression <= 52;
    }

    public void setVacuumingProgression(int v){
        this.vacuumingProgression = v;
        if(this.vacuumingProgression > 82){
            this.vacuumingProgression = 0;
        }
    }

    public VacuumingState getVacuumingState(){
        return this.vacuumingState;
    }

    public void setVacuumingState(VacuumingState state){
        this.vacuumingState = state;
    }

    public void createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set the position of the player body
        bodyDef.position.set(this.getPosition().cpy().scl(0.1f));

        body = world.createBody(bodyDef);
        body.setUserData(this);
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
                CollisionController.PhysicsConstants.CATEGORY_DESTRUCTIBLE |
                CollisionController.PhysicsConstants.CATEGORY_BOUNCY);

        body.createFixture(fixtureDef);
        shape.dispose(); // Always dispose shapes after use
    }

    public void movePlayer(Vector2 strength){
        body.applyForceToCenter(strength, true);
    }

    public void drawFire(GameCanvas canvas){
        if (body.getLinearVelocity().len() > 105){
            canvas.draw(fireBoost, Color.WHITE, (float) fireBoost.getWidth() / 2 + 275 , (float) fireBoost.getHeight() / 2, position.x , position.y+ 125, getRotation(),.5f,.5f,false);
        }else{
            canvas.draw(fire, Color.WHITE, (float) fire.getWidth() / 2 + 250 , (float) fire.getHeight() / 2, position.x , position.y+ 125, getRotation(),.5f,.5f,false);
        }

    }
}

