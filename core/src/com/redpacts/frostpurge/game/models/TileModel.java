package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
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
    /** x-coordinate of the tile center */
    public float cx;
    /** y-coordinate of the tile center */
    public float cy;
    public enum TileType{
        /** An empty tile */
        EMPTY,
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

    /**
     * Returns the center of this tile
     *
     * @return Vector2 center of the tile
     */
    public Vector2 getCenter(){
        return new Vector2(this.cx, this.cy);
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
