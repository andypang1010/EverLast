package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.math.Vector2;
import com.redpacts.frostpurge.game.util.Movable;

public abstract class CharactersModel implements Movable {
    protected Vector2 location;
    protected float angle;
    protected Vector2 velocity;
    public void accelerate() {

    }

    public void rotate() {

    }

    public void stop() {

    }

    public Vector2 getLocation(){
        return location;
    }
    public void setLocation(float x, float y){
        location = new Vector2(x, y);
    }
    public float getAngle(){
        return angle;
    }
    public void setAngle(float angle){
        this.angle = angle;
    }
    public Vector2 getVelocity(){
        return velocity;
    }
    public void setVelocity(float x, float y){
        velocity = new Vector2(x, y);
    }
}
