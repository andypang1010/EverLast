package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.redpacts.frostpurge.game.controllers.CollisionController;
import com.redpacts.frostpurge.game.views.GameCanvas;
//import org.w3c.dom.Text;
//import sun.invoke.empty.Empty;

/**
 * Class representing tiles in the game scene
 * Each tile has certain attributes that affects the game
 */
public class EmptyTile extends TileModel{

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
     * Create a default tile with flat texture
     */
    public EmptyTile(){
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
    public EmptyTile(Texture texture){
        this.texture = texture;
        this.type = TileType.EMPTY;
    }

    /**
     * Create a tile with the specified texture
     *
     * @param texture The texture of the tile
     * @param position The texture of the tile
     */
    public EmptyTile(TextureRegion texture, Vector2 position){
        this.textureRegion = texture;
        this.type = TileType.EMPTY;
        this.position = position;
    }

    public void createBody(World world) {
        // TODO: should we have a body here?
        return;
    }
}
