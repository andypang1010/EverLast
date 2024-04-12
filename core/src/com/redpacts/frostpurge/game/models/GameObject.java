package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.redpacts.frostpurge.game.views.GameCanvas;

public abstract class GameObject {

    protected Vector2 position;
    protected float rotation;
    protected Texture texture;
    protected TextureRegion textureRegion;
    protected Shape shape;
    protected Body body;

    public void activatePhysics(World world) {};

    public void deactivatePhysics(World world) {};
    public Vector2 getPosition(){
        return position;
    }
    public float getPositionY() {return position.y; }
    public void setPosition(float x, float y){
        position = new Vector2(x, y);
    }
    public void setPosition(Vector2 v){
        position = new Vector2(v);
    }
    public float getRotation(){
        return rotation;
    }
    public void setRotation(float rotation){
        this.rotation = rotation;
    }

    public Texture getTexture(){
        return texture;
    }
    public void setTexture(Texture texture){
        this.texture = texture;
    }
    public TextureRegion getTextureRegion(){
        return textureRegion;
    }

    public boolean isRemoved() { return false;}
    public void update(float dt) { return;
    };
    public void createBody(World world) {}
    public Body getBody() {return body;}


    public void drawDebug(GameCanvas canvas) {
        canvas.drawPhysics(shape, Color.BLUE, position.x*10, position.y*10, 0, 0.1f, 0.1f);
    }
}
