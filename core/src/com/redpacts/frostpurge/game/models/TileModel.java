package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.physics.box2d.*;
import com.redpacts.frostpurge.game.controllers.CollisionController;
import com.redpacts.frostpurge.game.views.GameCanvas;

/**
 * Class representing tiles in the game scene
 * Each tile has certain attributes that affects the game
 */
public abstract class TileModel extends GameObject {
    /** Index of the tile */
    public int index;
    /** x-coordinate of the tile origin(bottom left corner) */
    public float ox;
    /** y-coordinate of the tile origin(bottom right corner) */
    public float oy;

    public enum TileType{
        /** An empty tile */
        EMPTY,
        /** A swamp of radioactive liquid */
        SWAMP,
        /** Obstacle that blocks movement */
        OBSTACLE
    }

    /** Type of the tile */
    protected TileType type;

    /**
     * Returns the type of this tile
     *
     * @return type of this tile
     */
    public TileType getType(){
        return this.type;
    }

    /**
     * Returns the origin of this tile
     *
     * @return Vector2 origin of the tile
     */
    public Vector2 getOrigin(){
        return new Vector2(this.ox, this.oy);
    }


    public void setIndex(int index) {
        this.index = index;
    }
//
//    protected void createDestructibleBody(TileModel tile) {
//        BodyDef bodyDef = new BodyDef();
//        bodyDef.type = BodyDef.BodyType.StaticBody;
//        bodyDef.position.set(tile.getPosition());
//
//        Body body = world.createBody(bodyDef);
//        body.setUserData(tile);
//
//        PolygonShape shape = new PolygonShape();
//        shape.setAsBox((float) tile.getTexture().getWidth() / 2,
//                (float) tile.getTexture().getHeight() / 2);
//
//        FixtureDef fixtureDef = new FixtureDef();
//        fixtureDef.shape = shape;
//        fixtureDef.filter.categoryBits = CollisionController.PhysicsConstants.CATEGORY_DESTRUCTIBLE;
//        fixtureDef.filter.maskBits = (short)(CollisionController.PhysicsConstants.CATEGORY_PLAYER |
//                CollisionController.PhysicsConstants.CATEGORY_ENEMY);
//
//        body.createFixture(fixtureDef);
//        shape.dispose();
//    }

}
