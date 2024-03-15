package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.math.Vector2;
import com.redpacts.frostpurge.game.models.CharactersModel;
import com.redpacts.frostpurge.game.util.Movable;

public abstract class CharactersController implements Movable {
    protected CharactersModel model;

    @Override
    public void accelerate(float x, float y) {
        Vector2 vel = model.getVelocity();
        vel.x += .33f * x;
        vel.y -= .33f * y;
        model.setVelocity(vel.x, vel.y);
    }

    @Override
    public void stop() {
        Vector2 vel = model.getVelocity();
        float x = -.05f* model.getVelocity().x;
        float y = -.05f* model.getVelocity().y;
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

    public CharactersModel getModel() {
        return model;
    }
}
