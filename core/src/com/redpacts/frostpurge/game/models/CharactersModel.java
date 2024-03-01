package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.math.Vector2;
import com.redpacts.frostpurge.game.util.Movable;

public abstract class CharactersModel implements Movable {
    public void accelerate() {

    }

    public void rotate() {

    }

    public void stop() {

    }

    @Override
    public Vector2 getVelocity() {
        return null;
    }

    @Override
    public float getRotation() {
        return 0;
    }
}
