package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.redpacts.frostpurge.game.models.CharactersModel;
import com.redpacts.frostpurge.game.util.FilmStrip;

public abstract class CharactersController {
    protected CharactersModel model;
    private Float time;
    protected boolean flip;

    public void accelerate(float x, float y, float scale) {
        Vector2 vel = model.getVelocity();
        vel.x += .5f * scale * x;
        vel.y -= .5f * scale * y;
        model.setVelocity(vel.x, vel.y);
    }

    public void accelerate(float x, float y, float scale) {
        Vector2 vel = model.getVelocity();
        vel.x += .5f * scale * x;
        vel.y -= .5f * scale * y;
        model.setVelocity(vel.x, vel.y);
    }

    public void stop() {
        Vector2 vel = model.getVelocity();
        float x = -.05f* model.getVelocity().x;
        float y = -.05f* model.getVelocity().y;
        vel.x = stopHelper(vel.x,x);
        vel.y = stopHelper(vel.y,y);
        model.setVelocity(vel.x, vel.y);
    }
    public void friction(){
        Vector2 vel = model.getVelocity();
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
    protected void processRun() {
        if (time == null){
            time = 0f;
        }
        else{
            time += Gdx.graphics.getDeltaTime();
        }
        FilmStrip running = model.getFilmStrip();
        int frame = (running == null ? 11 : running.getFrame());
        if (running != null) {
            if (time >= .08){
                frame++;
                time = 0f;
                if (frame >= model.getFilmStrip().getSize())
                    frame = 0;
                running.setFrame(frame);
            }
        }
    }
    public CharactersModel getModel() {
        return model;
    }
}
