package com.redpacts.frostpurge.game.models;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.redpacts.frostpurge.game.assets.AssetDirectory;

import com.redpacts.frostpurge.game.controllers.CollisionController;
import com.redpacts.frostpurge.game.util.FilmStrip;
import com.redpacts.frostpurge.game.util.TileGraph;
import com.redpacts.frostpurge.game.views.GameCanvas;

public class EnemyModel extends CharactersModel{
    private int[] startpatrol;
    private int[] endpatrol;
    public int[] getStartPatrol(){
        return startpatrol;
    }
    public int[] getEndPatrol(){
        return endpatrol;
    }

    @Override
    public void activatePhysics(World world) {
        // Create and configure the enemy's physics body and fixtures
    }

    @Override
    public void deactivatePhysics(World world) {
        // Destroy the enemy's physics body from the world
    }
    /**
     * Instantiates the player with their starting location and angle and with their texture
     * @param position vector2 representing the starting location
     * @param rotation float representing angle the player is facing
     */
    public EnemyModel(Vector2 position, float rotation, AssetDirectory directory, int[] startpatrol, int[] endpatrol){
        this.position = position;
        this.rotation = rotation;
        this.velocity = new Vector2(0,0);
        //texture = new TextureRegion(directory.getEntry( "EnemyLR", Texture.class )).getTexture();

        Texture duck = new TextureRegion(directory.getEntry("EnemyLR", Texture.class)).getTexture();
        idle = new FilmStrip(duck, 1, 8, 8);
        idle.setFrame(3);

        run_right = new FilmStrip(duck, 1, 8, 8);
        run_right.setFrame(4);
        TextureRegion left = new TextureRegion(directory.getEntry( "EnemyLR", Texture.class ));
        left.flip(false,true);
        run_left = new FilmStrip(left.getTexture(),1,8,8);
        run_left.setFrame(4);
        Texture up= new TextureRegion(directory.getEntry( "EnemyUp", Texture.class )).getTexture();
        run_up = new FilmStrip(up, 1, 8, 8);
        Texture down = new TextureRegion(directory.getEntry( "EnemyDown", Texture.class )).getTexture();
        run_down = new FilmStrip(down, 1, 8, 8);

        this.startpatrol = startpatrol;
        this.endpatrol = endpatrol;
    }

    public void createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set the position of the enemy body
        bodyDef.position.set(this.getPosition().scl(0.1f));

        Body body = world.createBody(bodyDef);
        body.setUserData(this);
        body.setSleepingAllowed(false);

        // TODO: getTexture is not scaled...
        PolygonShape shape = new PolygonShape();
//        shape.setAsBox((float) this.getTexture().getWidth() / 2,
//                (float) this.getTexture().getHeight() / 2);
        shape.setAsBox(1f, 1f);

        this.shape = shape;

        // TODO: Adjust parameters as necessary
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.01f;
        fixtureDef.friction = 0.3f;

        // Setting category and mask bits for the enemy
        fixtureDef.filter.categoryBits = CollisionController.PhysicsConstants.CATEGORY_ENEMY;
        fixtureDef.filter.maskBits = (short)(CollisionController.PhysicsConstants.CATEGORY_PLAYER |
                CollisionController.PhysicsConstants.CATEGORY_OBSTACLE |
                CollisionController.PhysicsConstants.CATEGORY_DESTRUCTIBLE);

        body.createFixture(fixtureDef);
        shape.dispose(); // Always dispose shapes after use
    }
}