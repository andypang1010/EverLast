package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.math.Vector2;
import com.redpacts.frostpurge.game.models.CharactersModel;
import com.redpacts.frostpurge.game.models.PlayerModel;
import com.redpacts.frostpurge.game.views.GameCanvas;

public class PlayerController extends CharactersModel {
    private PlayerModel player;
    void PlayerController(PlayerModel player){
        this.player = player;
    }
    @Override
    public void accelerate() {
        Vector2 vel = player.getVelocity();
        float x = (float) Math.cos(player.getAngle());
        float y = (float) -Math.sin(player.getAngle());
        vel.x += x;
        vel.y += y;
        player.setVelocity(vel);
    }
    public void rotate(Boolean left) {
        if (left){
            player.setAngle(player.getAngle()+.1f);
        }else{
            player.setAngle(player.getAngle()-.1f);
        }
    }
    @Override
    public void stop() {
        Vector2 vel = player.getVelocity();
        float x = (float) -Math.cos(player.getAngle());
        float y = (float) Math.sin(player.getAngle());
        vel.x = stophelper(vel.x,x);
        vel.y = stophelper(vel.y,y);
        player.setVelocity(vel);
    }
    private float stophelper (float vel, float change){
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
    @Override
    public Vector2 getVelocity() {
        return player.getVelocity();
    }
    @Override
    public float getRotation() {
        return player.getAngle();
    }
    public void vacuum() {

    }
    public void boost() {

    }

    /**
     * Update function that will be called in gameplaycontroller to update the player actions.
     * Right now the input controller isn't done yet, so I am using booleans for buttons presses.
     */
    public void update(GameCanvas canvas,boolean accelerate, boolean decelerate, boolean left, boolean right){
        if (accelerate){
            accelerate();
        }
        if (decelerate){
            stop();
        }
        if (left){
            rotate(true);
        }
        if (right){
            rotate(false);
        }
        player.setLocation(player.getLocation().add(player.getVelocity()));
        player.drawplayer(canvas);
    }
}
