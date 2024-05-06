package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.redpacts.frostpurge.game.models.CharactersModel;
import com.redpacts.frostpurge.game.models.EnemyModel;
import com.redpacts.frostpurge.game.models.PlayerModel;
import com.redpacts.frostpurge.game.util.FilmStrip;

public abstract class CharactersController {
    protected CharactersModel model;
    private Float time;
    protected boolean flip;
    protected String previousDirection = "right";
    protected Vector2 vel;
    /**
     * Update the animation of the ship to process a turn
     *
     * Turning changes the frame of the filmstrip, as we change from a level ship to
     * a hard bank.  This method also updates the field dang cumulatively.
     *
     */
    protected void processRun(String type) {
        if (time == null){
            time = 0f;
        }
        else{
            time += Gdx.graphics.getDeltaTime();
        }
        FilmStrip running = model.getFilmStrip(type);
        int frame = (running == null ? 11 : running.getFrame());
        System.out.println("FRAME");
        System.out.println(frame);
        if (running != null) {
            if (type.startsWith("idle") || type.startsWith("vacuumstart") || type.startsWith("vacuumend")){
                if (time >= .25){
                    frame++;
                    time = 0f;
                    if (frame >= model.getFilmStrip(type).getSize())
                        frame = 0;
                    running.setFrame(frame);
                }
            } else if(type.startsWith("vacuum")){
                if (time >= 1/3f){
                    frame++;
                    time = 0f;
                    if (frame >= model.getFilmStrip(type).getSize())
                        frame = 0;
                    running.setFrame(frame);
                }
            } else if (type.equals("death")) {
                if (time >= .125){
                    frame++;
                    time = 0f;
                    if (frame >= model.getFilmStrip(type).getSize())
                        frame = model.getFilmStrip(type).getSize() - 1;
                    running.setFrame(frame);
                }
            }else{
                if (time >= .125){
                    frame++;
                    time = 0f;
                    if (frame >= model.getFilmStrip(type).getSize())
                        frame = 0;
                    running.setFrame(frame);
                }
            }
        }
    }
    public CharactersModel getModel() {
        return model;
    }

    /**
     * helper to find which animation to draw
     * @param x
     * @param y
     * @return the direction that the animtion should face
     */
    public String getDirection(float x, float y, String previous) {
        float angle = (float) Math.toDegrees(Math.atan2(x,y));

        if (model instanceof PlayerModel){
            if (x==0 && y==0){
                return previous;
            }
            if (angle >= 0 && angle <= 135){
                return "right";
            } else if (angle>=135 || angle<=-135) {
                return "up";
            } else if (angle >= -135 && angle <=0) {
                return "left";
            }
        } else if (model instanceof EnemyModel){ // Assume model instanceof EnemyModel
            angle = (float) Math.toDegrees(Math.atan2(y,x));
            // setRotation is broken as of now, but also have found workaround that doesn't use setRotation
//            model.setRotation(angle);
            if (angle <= 45 && angle >= -45){
                return "right";
            } else if (angle>=45 && angle<=135) {
                return "up";
            } else if (angle >= 135 || angle <=-135) {
                return "left";
            }else{
                return "down";
            }
        }
        return previousDirection;
    }
}
