package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.redpacts.frostpurge.game.util.Movable;
import com.redpacts.frostpurge.game.views.GameCanvas;

public abstract class CharactersModel extends GameObject {
    protected Vector2 velocity;

    public Vector2 getVelocity(){
        return velocity;
    }
    public void setVelocity(float x, float y){
        velocity = new Vector2(x, y);
    }

    public void drawCharacter(GameCanvas canvas, float rotation, Color tint){
        canvas.draw(texture, tint, (float) texture.getWidth() / 2, (float) texture.getHeight() / 2, position.x, position.y, rotation,.5f,.5f);
    }
}
