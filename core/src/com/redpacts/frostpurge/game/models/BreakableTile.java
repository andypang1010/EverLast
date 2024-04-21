package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
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
public class BreakableTile extends TileModel{

    /** Type of the tile */
    private TileType type;
    /** Scale from texture to screen */
    private float scale;
    protected FilmStrip activeTexture;
    boolean active;
    boolean destroyed;
    float time;

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
    public FilmStrip getFilmStrip() { return this.activeTexture;}
    public void resetFilmStrip() {
        this.activeTexture.setFrame(0);
    }
    public boolean isActive() {return this.active;}
    public void activate() {active = true;}
    public void deactivate() {active = false;}

    /**
     * Create a tile with the specified texture
     *
     * @param textureActive    The active texture of the tile
     */
    public BreakableTile(TextureRegion textureActive, Vector2 position, float scale, int base, int rows, int cols, int size){
        this.type = TileType.DESTRUCTIBLE;
        this.texture = new FilmStrip(textureActive.getTexture(), rows, cols, size).getTexture();
        this.activeTexture = new FilmStrip(textureActive.getTexture(), rows, cols, size);
        this.active = true;
        this.destroyed = false;
        this.time = 0;
        this.position = position;
        this.scale = scale;
        this.base  = base;
    }
    protected void processFilmStrip() {
        if(active || destroyed) return;
        Filter filter = body.getFixtureList().first().getFilterData();
        filter.categoryBits = CollisionController.PhysicsConstants.CATEGORY_EMPTY;
        body.getFixtureList().first().setFilterData(filter);

        time += Gdx.graphics.getDeltaTime();
        int frame = activeTexture.getFrame();
        if (time >= .125 / 3){
            frame++;
            time = 0f;
            if (frame >= activeTexture.getSize()) {
                destroyed = true;
                frame--;
            }
            activeTexture.setFrame(frame);
        }
    }

    @Override
    public void createBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.active = true;
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // Set the position of the obstacle
        bodyDef.position.set(this.getPosition().cpy().add(64f / 2, 64f / 2).scl(0.1f));

        body = world.createBody(bodyDef);
        body.setUserData(this);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(6.4f / 2, 6.4f / 2);

        this.shape = shape;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0f;
        fixtureDef.filter.categoryBits = CollisionController.PhysicsConstants.CATEGORY_DESTRUCTIBLE;
        fixtureDef.filter.maskBits = (short)(CollisionController.PhysicsConstants.CATEGORY_PLAYER |
                CollisionController.PhysicsConstants.CATEGORY_ENEMY);

        body.createFixture(fixtureDef);
        shape.dispose();
    }
}

