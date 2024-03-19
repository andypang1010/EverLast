package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.redpacts.frostpurge.game.controllers.CollisionController;
import com.redpacts.frostpurge.game.views.GameCanvas;

/**
 * Class representing tiles in the game scene
 * Each tile has certain attributes that affects the game
 */
public class SwampTile extends TileModel{

    /** Type of the tile */
    private TileType type;
    private float scale;
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
        this.type = TileType.SWAMP;
    }

    public SwampTile(Texture texture, Vector2 position, float scale){
        this.texture = texture;
        this.type = TileType.SWAMP;
        this.position = position;
        this.scale = scale;
    }

    @Override
    public void createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // Set the position of the obstacle
        bodyDef.position.set(this.getPosition().cpy().add(128f / 2, 128f / 2));

        Body body = world.createBody(bodyDef);
        body.setUserData(this);
        body.setSleepingAllowed(false);

        // TODO: shape needs scaling
        PolygonShape shape = new PolygonShape();
//        shape.setAsBox((float) this.getTexture().getWidth() / (2 * scale),
//                (float) this.getTexture().getHeight() / (2 * scale));
        shape.setAsBox(128f / 2, 128f / 2);
//        System.out.println("Swamp");
//        System.out.println(this.getPosition());
//        System.out.println(this.getTexture().getWidth());
//        System.out.println(this.getTexture().getHeight());

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = CollisionController.PhysicsConstants.CATEGORY_SWAMP;
        fixtureDef.filter.maskBits = CollisionController.PhysicsConstants.CATEGORY_PLAYER;

        body.createFixture(fixtureDef);
        shape.dispose();
    }
}
