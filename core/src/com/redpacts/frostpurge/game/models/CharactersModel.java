package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.math.Vector2;
import com.redpacts.frostpurge.game.util.Movable;

public abstract class CharactersModel extends GameObject implements Movable {

    protected Vector2 velocity;

    public void accelerate() {

    }

    public void rotate() {

    }

    public void stop() {

    }

    public Vector2 getVelocity(){
        return velocity;
    }
    public void setVelocity(float x, float y){
        velocity = new Vector2(x, y);
    }
}
