package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.redpacts.frostpurge.game.controllers.CollisionController;
import com.redpacts.frostpurge.game.util.FilmStrip;
import com.redpacts.frostpurge.game.views.GameCanvas;

/**
 * Class representing tiles in the game scene
 * Each tile has certain attributes that affects the game
 */
public class BouncyTile extends TileModel{

    /** Type of the tile */
    private TileType type;
    /** Scale from texture to screen */
    private float scale;
    protected FilmStrip activeTexture;
    boolean active;

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
    public FilmStrip getFilmStrip(String type) { return this.activeTexture;}
    public void resetFilmStrip() {
        this.activeTexture.setFrame(0);
    }
    public boolean isActive() {return this.active;}

    /**
     * Create a tile with the specified texture
     *
     * @param textureIdle      The idle texture of the tile
     * @param textureActive    The active texture of the tile
     */
    public BouncyTile(TextureRegion textureIdle, TextureRegion textureActive, Vector2 position, float scale, int base, int rows, int cols, int size){
        this.type = TileType.BOUNCY;
        this.textureRegion = textureIdle;
        this.activeTexture = new FilmStrip(textureActive.getTexture(), rows, cols, size);
        this.active = false;
        this.position = position;
        this.scale = scale;
        this.base  = base;
    }

    @Override
    public void createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // Set the position of the obstacle
        bodyDef.position.set(this.getPosition().cpy().add(64f * 3 / 2, 64f * 2 / 2).scl(0.1f));

        Body body = world.createBody(bodyDef);
        body.setUserData(this);
        body.setSleepingAllowed(false);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(6.4f * 3 / 2, 6.4f * 2 / 2);

        this.shape = shape;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 1f;
        fixtureDef.filter.categoryBits = CollisionController.PhysicsConstants.CATEGORY_BOUNCY;
        fixtureDef.filter.maskBits = (short)(CollisionController.PhysicsConstants.CATEGORY_PLAYER |
                CollisionController.PhysicsConstants.CATEGORY_ENEMY);

        body.createFixture(fixtureDef);
        shape.dispose();
    }
}

