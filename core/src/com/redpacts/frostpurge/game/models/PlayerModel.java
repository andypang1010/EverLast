package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.redpacts.frostpurge.game.controllers.CollisionController;
import com.redpacts.frostpurge.game.util.FilmStrip;
import com.redpacts.frostpurge.game.views.GameCanvas;

public class PlayerModel extends CharactersModel {

    private boolean canBoost;
    private Texture fire;
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
        canBoost = false;
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
        bodyDef.position.set(this.getPosition());

        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.setSleepingAllowed(false);

        PolygonShape shape = new PolygonShape();
        // TODO: getTexture is not scaled...
//        shape.setAsBox((float) this.getTexture().getWidth() /2,
//                (float) this.getTexture().getHeight() / 2);
        shape.setAsBox(50f, 50f);
        // TODO: Adjust parameters as necessary
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.3f;

        // Setting category and mask bits for the player
        fixtureDef.filter.categoryBits = CollisionController.PhysicsConstants.CATEGORY_PLAYER;
        fixtureDef.filter.maskBits = (CollisionController.PhysicsConstants.CATEGORY_ENEMY |
                CollisionController.PhysicsConstants.CATEGORY_OBSTACLE |
                CollisionController.PhysicsConstants.CATEGORY_SWAMP |
                CollisionController.PhysicsConstants.CATEGORY_DESTRUCTIBLE);

        body.createFixture(fixtureDef);
        shape.dispose(); // Always dispose shapes after use
    }

    public void drawFire(GameCanvas canvas, boolean flip){
        if (flip){
            canvas.draw(fire, Color.WHITE, (float) fire.getWidth() / 2 - 150 , (float) fire.getHeight() / 2, position.x + 40 , position.y+25, getRotation()-180,.5f,.5f,true);
        }else{
            canvas.draw(fire, Color.WHITE, (float) fire.getWidth() / 2 + 150 , (float) fire.getHeight() / 2, position.x - 40 , position.y+25, getRotation(),.5f,.5f,false);
        }

    }
}

