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
public class SwampTile extends TileModel{

    /** Type of the tile */
    private TileType type = TileType.SWAMP;
    private boolean powered = true;

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
     * Returns if this tile has a power up
     *
     * @return whether this tile still has power up on it
     */
    public boolean getPowered(){return this.powered;}

    /**
     * Sets the powered state of this tile
     * The tile's type becomes empty if it does not have power up anymore
     *
     * @param powered the powered state this tile is going to have
     */
    public void setPowered(boolean powered){
        this.powered = powered;
        if(!this.powered){
            this.type = TileType.EMPTY;
        }
    }

    /**
     * Create a default tile with flat texture
     */
    public SwampTile(){
        Pixmap pixmap = new Pixmap( 64, 64, Pixmap.Format.RGBA8888 );
        pixmap.setColor( 1, 1, 1, 1f );
        this.texture = new Texture( pixmap );
        pixmap.dispose();
    }

    /**
     * Create a tile with the specified texture
     *
     * @param texture The texture of the tile
     */
    public SwampTile(Texture texture){
        this.texture = texture;
    }
}
