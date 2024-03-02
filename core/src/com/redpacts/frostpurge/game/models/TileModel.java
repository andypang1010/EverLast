package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.graphics.Texture;
import com.redpacts.frostpurge.game.views.GameCanvas;

/**
 * Class representing tiles in the game scene
 * Each tile has certain attributes that affects the game
 */
public class TileModel {
    /** Texture of the tile */
    private Texture texture;
    /** Is this a power up tile? */
    private boolean power = false;
    /** Is this a goal tile? */
    private boolean goal = false;

    /**
     * Returns whether this tile is a power up tile
     *
     * @return whether this tile is a power up tile
     */
    public boolean isPowered(){
        return power;
    }

    /**
     * Returns whether this tile is a goal tile
     *
     * @return whether this tile is a goal tile
     */
    public boolean isGoal(){
        return goal;
    }

    /**
     * Sets the power up attribute of this tile
     *
     * @param power the power up attribute to be given to the tile
     */
    public void setPower(boolean power){
        this.power = power;
    }

    /**
     * Sets the goal attribute of this tile
     *
     * @param goal the goal attribute to be given to the tile
     */
    public void setGoal(boolean goal){
        this.goal = goal;
    }

    /**
     * Returns the texture of this tile
     *
     * @return texture of this tile
     */
    public Texture getTexture(){
        return this.texture;
    }

    /**
     * Create a default tile without special attributes
     */
    public TileModel(){
        this.goal = false;
        this.power = false;
    }

    /**
     * Create tile with the specified attributes
     *
     * @param power The tile power attribute
     * @param goal The tile goal attribute
     */
    public TileModel(boolean power, boolean goal){
        this.power = power;
        this.goal = goal;
    }

}
