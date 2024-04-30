package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.redpacts.frostpurge.game.controllers.CollisionController;

/**
 * Class representing tiles in the game scene
 * Each tile has certain attributes that affects the game
 */
public class GoalTile extends TileModel{

    /** Type of the tile */
    private TileType type;

    /**
     * Returns the texture of this tile
     *
     * @return texture of this tile
     */
    public Texture getTexture(){
        return this.texture;
    }

    /**
     * Returns the type of this tile
     *
     * @return type of this tile
     */
    public TileType getType(){
        return this.type;
    }


    /**
     * Create a tile with the specified texture
     *
     * @param texture The texture of the tile
     */
    public GoalTile(TextureRegion texture){
        this.textureRegion = texture;
        this.type = TileType.SWAMP;
    }

    public GoalTile(TextureRegion texture, Vector2 position, int base){
        this.textureRegion = texture;
        this.type = TileType.SWAMP;
        this.position = position;
        this.base = base;
    }

    @Override
    public void createBody(World world) {
        System.out.println("BODY CREATED");
        BodyDef bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // Set the position of the obstacle
        bodyDef.position.set(this.getPosition().cpy().add(64f / 2, 64f / 2).scl(0.1f));

        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.setSleepingAllowed(false);

        // TODO: shape needs scaling
        PolygonShape shape = new PolygonShape();
//        shape.setAsBox((float) this.getTexture().getWidth() / (2 * scale),
//                (float) this.getTexture().getHeight() / (2 * scale));
        shape.setAsBox(6.4f / 2, 6.4f / 2);
//        System.out.println("Swamp");
//        System.out.println(this.getPosition());
//        System.out.println(this.getTexture().getWidth());
//        System.out.println(this.getTexture().getHeight());

        this.shape = shape;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = CollisionController.PhysicsConstants.CATEGORY_SWAMP;
        fixtureDef.filter.maskBits = CollisionController.PhysicsConstants.CATEGORY_PLAYER;

        body.createFixture(fixtureDef);
        shape.dispose();
    }
}
