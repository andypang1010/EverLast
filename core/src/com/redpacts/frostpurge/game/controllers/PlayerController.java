package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.math.Vector2;
import com.redpacts.frostpurge.game.models.CharactersModel;
import com.redpacts.frostpurge.game.models.PlayerModel;
import com.redpacts.frostpurge.game.views.GameCanvas;

public class PlayerController extends CharactersModel {
    private PlayerModel player;
    PlayerController(PlayerModel player){
        this.player = player;
    }
    @Override
    public void accelerate() {
        Vector2 vel = player.getVelocity();
        float x = (float) (.5f*Math.cos(Math.toRadians(player.getRotation())));
        float y = (float) (.5f*Math.sin(Math.toRadians(player.getRotation())));
        vel.x += x;
        vel.y += y;
        player.setVelocity(vel.x, vel.y);
    }
    public void rotate(Boolean left) {
        if (left){
            player.setRotation(player.getRotation()+3f);
        }else{
            player.setRotation(player.getRotation()-3f);
        }
    }
    @Override
    public void stop() {
        Vector2 vel = player.getVelocity();
        float x = -.05f*player.getVelocity().x;
        float y = -.05f*player.getVelocity().y;
        vel.x = stophelper(vel.x,x);
        vel.y = stophelper(vel.y,y);
        player.setVelocity(vel.x, vel.y);
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

    public float getRotation() {
        return player.getRotation();
    }
    public void vacuum() {

    }
    public void boost() {

    }

    /**
     * resets the player to origin for testing
     */
    private void reset(){
        player.setLocation(100, 100);
        player.setRotation(0);
        player.setVelocity(0,0);
    }

    /**
     * Update function that will be called in gameplaycontroller to update the player actions.
     * Right now the input controller isn't done yet, so I am using booleans for buttons presses.
     */
    public void update(boolean accelerate, boolean decelerate, boolean left, boolean right, boolean restart){
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
        if (restart){
            reset();
        }
      
        Vector2 newLocation = player.getPosition().add(player.getVelocity());
        player.setLocation(newLocation.x, newLocation.y);
    }
    public void draw(GameCanvas canvas){
        player.drawPlayer(canvas);
    }
}
