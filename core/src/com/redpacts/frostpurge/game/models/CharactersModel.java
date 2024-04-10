package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Null;
import com.redpacts.frostpurge.game.util.FilmStrip;
import com.redpacts.frostpurge.game.views.GameCanvas;

public abstract class CharactersModel extends GameObject {
    protected Vector2 velocity;
    protected FilmStrip run_left;
    protected FilmStrip run_right;
    protected FilmStrip run_down;
    protected FilmStrip run_up;
    protected FilmStrip idleright;
    protected FilmStrip idleleft;
    protected FilmStrip idleup;
    protected String type;

    public Vector2 getVelocity(){
        return velocity;
    }
    public void setVelocity(float x, float y){
        velocity = new Vector2(x, y);
    }
    public FilmStrip getFilmStrip(String type) {
        switch (type){
            case "left":
                return run_left;
            case "right":
                return run_right;
            case "up":
                return run_up;
            case "down":
                return run_down;
            case "idleright":
                return idleright;
            case "idleleft":
                return idleleft;
            case "idleup":
                return idleup;
            default:
                return null;
        }
    }
    public void resetFilmStrip(FilmStrip value) {
        value.setFrame(0);
    }

    public void drawCharacter(GameCanvas canvas, float rotation, Color tint, String state, String direction){
        switch(state){
            case "idle":
                System.out.println(direction);
                switch (direction){
                    case "left":
                        canvas.draw(idleleft, tint, (float) idleleft.getRegionHeight() / 2, (float) idleleft.getRegionHeight() / 2, position.x, position.y, 0,.25f,.25f, false);
                        break;
                    case "right":
                        canvas.draw(idleright, tint, (float) idleright.getRegionHeight() / 2, (float) idleright.getRegionHeight() / 2, position.x, position.y, 0,.25f,.25f, false);
                        break;
                    default:
                        canvas.draw(idleup, tint, (float) idleup.getRegionHeight() / 2, (float) idleup.getRegionHeight() / 2, position.x, position.y, 0,.25f,.25f, false);
                }
                break;
            case "running":
                switch(direction){
                    case "left":
                        canvas.draw(run_left, tint, (float) run_left.getRegionWidth() / 2, (float) run_left.getRegionHeight() / 2, position.x, position.y, 0,.25f,.25f, type.equals("enemy"));
                        break;
                    case "right":
                        canvas.draw(run_right, tint, (float) run_right.getRegionWidth() / 2, (float) run_right.getRegionHeight() / 2, position.x, position.y, 0,.25f,.25f,false);
                        break;
                    case "up":
                        canvas.draw(run_up, tint, (float) run_up.getRegionWidth() / 2, (float) run_up.getRegionHeight() / 2, position.x, position.y, 0,.25f,.25f,false);
                        break;
                    case "down":
                        canvas.draw(run_down, tint, (float) run_down.getRegionWidth() / 2, (float) run_down.getRegionHeight() / 2, position.x, position.y, 0,.25f,.25f,false);
                        break;
                    default:
                        throw new IllegalArgumentException("Character animation fail");
                }
                break;
            default:
                break;
        }


    }
}
