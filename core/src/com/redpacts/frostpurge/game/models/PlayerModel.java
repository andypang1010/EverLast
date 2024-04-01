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

    private int energy;
    private Texture fire;
    private boolean isBoosting;
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
        texture = new TextureRegion(directory.getEntry("Liv", Texture.class)).getTexture();
        Texture run = new TextureRegion(directory.getEntry("Liv_Run", Texture.class)).getTexture();
        running = new FilmStrip(run, 1, 12, 12);
        fire = new TextureRegion(directory.getEntry("Fire", Texture.class)).getTexture();
        energy = 0;
        isBoosting = false;
    }

    public float getPositionY(){
        return this.position.y - 250;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int b) {
        energy = b;
    }

    public void addEnergy(int b) { energy += b;}

    public boolean getIsBoosting(){
        return isBoosting;
    }

    public void setIsBoosting(boolean b){
        isBoosting = b;
    }

    public void createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set the position of the player body
        bodyDef.position.set(this.getPosition());

        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.setSleepingAllowed(false);
        body.setFixedRotation(true);
        body.setLinearDamping(0);

        PolygonShape shape = new PolygonShape();
        // TODO: getTexture is not scaled...
//        shape.setAsBox((float) this.getTexture().getWidth() /2,
//                (float) this.getTexture().getHeight() / 2);
        shape.setAsBox(0.1f, 0.1f);
        // TODO: Adjust parameters as necessary
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.67f;
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
        Color c = Color.WHITE;
        float s = 0.5f;
        if(isBoosting){
            c = Color.SKY;
            s = 0.75f;
        }
        if (flip){
            canvas.draw(fire, c, (float) fire.getWidth() / 2 - 150 , (float) fire.getHeight() / 2, position.x + 40 , position.y+25, getRotation()-180,s,s,true);
        }else{
            canvas.draw(fire, c, (float) fire.getWidth() / 2 + 150 , (float) fire.getHeight() / 2, position.x - 40 , position.y+25, getRotation(),s,s,false);
        }

    }

    public void drawBody(GameCanvas canvas){
        canvas.draw(fire, Color.GREEN, (float) fire.getWidth() / 2 + 150 , (float) fire.getHeight() / 2, body.getPosition().x , body.getPosition().y, 0,.5f,.5f,false);
    }
}

