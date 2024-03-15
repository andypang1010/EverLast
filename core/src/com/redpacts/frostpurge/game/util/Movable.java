package com.redpacts.frostpurge.game.util;

import com.badlogic.gdx.math.Vector2;

public interface Movable {

    void accelerate(float x, float y);

    void rotate(boolean left);

    void stop();

}
