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

    public void accelerate(float x, float y, float scale) {
        vel = model.getVelocity();
        vel.x += .5f * scale * x;
        vel.y -= .5f * scale * y;
        model.setVelocity(vel.x, vel.y);
    }

    public void stop() {
        vel = model.getVelocity();
        float x = -.05f* model.getVelocity().x;
        float y = -.05f* model.getVelocity().y;
        vel.x = stopHelper(vel.x,x);
        vel.y = stopHelper(vel.y,y);
        model.setVelocity(vel.x, vel.y);
    }
    public void friction(){
        vel = model.getVelocity();
        float x = -.15f* model.getVelocity().x/ Math.abs(model.getVelocity().x);
        float y = -.15f* model.getVelocity().y/ Math.abs(model.getVelocity().y);
        vel.x = stopHelper(vel.x,x);
        vel.y = stopHelper(vel.y,y);
        model.setVelocity(vel.x, vel.y);
    }
    private float stopHelper(float vel, float change){
        if (vel<0){
            if (vel + change >0){
                vel = 0;
            }
            else{
                vel += change;
            }
        }else if(vel>0){
            if (vel + change <0){
                vel = 0;
            }
            else{
                vel += change;
            }
        }
        return vel;
    }
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
        if (running != null) {
            if (type.startsWith("idle")){
                if (time >= .25){
                    frame++;
                    time = 0f;
                    if (frame >= model.getFilmStrip(type).getSize())
                        frame = 0;
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
        }

        if (angle >= 45 && angle <= 135){
            if (model instanceof  EnemyModel){
                model.setRotation(0);
            }
            return "right";
        } else if (angle>=135 || angle<=-135) {
            if (model instanceof  EnemyModel){
                model.setRotation(270);
            }
            return "up";
        } else if (angle >= -135 && angle <=-45) {
            if (model instanceof  EnemyModel){
                model.setRotation(180);
            }
            return "left";
        }else{
            if (model instanceof  EnemyModel){
                model.setRotation(90);
            }
            return "down";
        }
    }
}
