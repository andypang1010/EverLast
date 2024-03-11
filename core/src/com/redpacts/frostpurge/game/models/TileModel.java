package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.graphics.Texture;
import com.redpacts.frostpurge.game.views.GameCanvas;

/**
 * Class representing tiles in the game scene
 * Each tile has certain attributes that affects the game
 */
abstract class TileModel extends GameObject {
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

}
