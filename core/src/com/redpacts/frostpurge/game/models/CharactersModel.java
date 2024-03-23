package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.redpacts.frostpurge.game.util.FilmStrip;
import com.redpacts.frostpurge.game.views.GameCanvas;

public abstract class CharactersModel extends GameObject {
    protected Vector2 velocity;
    protected FilmStrip running;

    public Vector2 getVelocity(){
        return velocity;
    }
    public void setVelocity(float x, float y){
        velocity = new Vector2(x, y);
    }
    public FilmStrip getFilmStrip() {
        return running;
    }
    public void resetFilmStrip(FilmStrip value) {
        value.setFrame(11);
    }

    public void drawCharacter(GameCanvas canvas, float rotation, Color tint, String state, boolean flip){
        switch(state){
            case "idle":
                canvas.draw(texture, tint, (float) texture.getWidth() / 2, (float) texture.getHeight() / 2, position.x, position.y, 0,.25f,.25f, flip);
                break;
            case "running":
                canvas.draw(running, tint, (float) running.getRegionWidth() / 2, (float) running.getRegionHeight() / 2, position.x, position.y, 0,.25f,.25f,flip);
                break;
            default:
                break;
        }


    }
}
