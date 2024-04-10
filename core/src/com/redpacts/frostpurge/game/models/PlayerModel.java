package com.redpacts.frostpurge.game.models;

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

    private boolean canBoost;
    private Texture fire;
    private boolean alive;
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
        this.alive = true;

        Texture liv = new TextureRegion(directory.getEntry("Liv_Run_Right", Texture.class)).getTexture();
        idle = new FilmStrip(liv, 1, 8, 8);
        idle.setFrame(3);
//        texture = new TextureRegion(directory.getEntry("Liv", Texture.class)).getTexture();

        Texture run_left_text = new TextureRegion(directory.getEntry("Liv_Run_Left", Texture.class)).getTexture();
        run_left = new FilmStrip(run_left_text, 1, 8, 8);
        Texture run_right_text = new TextureRegion(directory.getEntry("Liv_Run_Right", Texture.class)).getTexture();
        run_right = new FilmStrip(run_right_text, 1, 8, 8);
        Texture run_down_text = new TextureRegion(directory.getEntry("Liv_Run_Left", Texture.class)).getTexture(); // NOT THE CORRECT ONE YET
        run_down = new FilmStrip(run_down_text, 1, 8, 8);
        Texture run_up_text = new TextureRegion(directory.getEntry("Liv_Run_Up", Texture.class)).getTexture();
        run_up = new FilmStrip(run_up_text, 1, 8, 8);

        fire = new TextureRegion(directory.getEntry("Fire", Texture.class)).getTexture();
        canBoost = false;
        alive = true;
        type = "player";
    }


    public boolean isAlive(){return alive;}
    public void die(){alive = false;}

    public float getPositionY(){
        return this.position.y - 250;
    }

    public boolean getCanBoost() {
        return canBoost;
    }

    public void setCanBoost(boolean b) {
        canBoost = b;
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

        PolygonShape shape = new PolygonShape();
        // TODO: getTexture is not scaled...
//        shape.setAsBox((float) this.getTexture().getWidth() /2,
//                (float) this.getTexture().getHeight() / 2);
        shape.setAsBox(1f, 1f);

        this.shape = shape;

        // TODO: Adjust parameters as necessary
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.0067f;
        fixtureDef.friction = 0.3f;
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

    public void drawFire(GameCanvas canvas, boolean flip){
        if (flip){
            canvas.draw(fire, Color.WHITE, (float) fire.getWidth() / 2 - 150 , (float) fire.getHeight() / 2, position.x + 40 , position.y+25, getRotation()-180,.5f,.5f,true);
        }else{
            canvas.draw(fire, Color.WHITE, (float) fire.getWidth() / 2 + 250 , (float) fire.getHeight() / 2, position.x , position.y+25, getRotation(),.5f,.5f,false);
        }

    }
}

