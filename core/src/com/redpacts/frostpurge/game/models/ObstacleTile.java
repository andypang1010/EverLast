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
public class ObstacleTile extends TileModel{

    /** Type of the tile */
    private TileType type;
    /** Scale from texture to screen */
    private float scale;

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
        this.type = TileType.OBSTACLE;
//        this.position = ;
    }

    /**
     * Create a tile with the specified texture
     *
     * @param texture The texture of the tile
     */
    public ObstacleTile(Texture texture, Vector2 position, float scale){
        this.texture = texture;
        this.type = TileType.OBSTACLE;
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
//        System.out.println("Obstacle");
//        System.out.println(this.getPosition());
//        System.out.println(this.getTexture().getWidth());
//        System.out.println(this.getTexture().getHeight());

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = CollisionController.PhysicsConstants.CATEGORY_OBSTACLE;
        fixtureDef.filter.maskBits = (short)(CollisionController.PhysicsConstants.CATEGORY_PLAYER |
                CollisionController.PhysicsConstants.CATEGORY_ENEMY);

        body.createFixture(fixtureDef);
        shape.dispose();
    }
}


