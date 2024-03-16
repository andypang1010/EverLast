package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.redpacts.frostpurge.game.views.GameCanvas;

/**
 * Class representing tiles in the game scene
 * Each tile has certain attributes that affects the game
 */
public class ObstacleTile extends TileModel{

    /** Type of the tile */
    private final TileType type = TileType.OBSTACLE;

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
     * Create a default obstacle tile with flat texture
     *
     * @param ox The x-coordinate of the tile's origin(bottom left)
     * @param oy The y-coordinate of the tile's origin(bottom left)
     * @param width The width of the tile
     */
    public ObstacleTile(float ox, float oy, int width){
        this.ox = ox;
        this.oy = oy;
        this.cx = ox + width/2;
        this.cy = oy + width/2;
        Pixmap pixmap = new Pixmap( 64, 64, Pixmap.Format.RGBA8888 );
        pixmap.setColor( 1, 1, 1, 1f );
        this.texture = new Texture( pixmap );
        pixmap.dispose();
    }

    /**
     * Create an obstacle tile with the specified texture
     *
     * @param ox The x-coordinate of the tile's origin(bottom left)
     * @param oy The y-coordinate of the tile's origin(bottom left)
     * @param width The width of the tile
     * @param texture The texture of the tile
     */
    public ObstacleTile(float ox, float oy, int width, Texture texture){
        this.ox = ox;
        this.oy = oy;
        this.cx = ox + width/2;
        this.cy = oy + width/2;
        this.texture = texture;
    }
}


