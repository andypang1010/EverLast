package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.math.Vector2;
import com.redpacts.frostpurge.game.models.CharactersModel;
import com.redpacts.frostpurge.game.models.PlayerModel;
import com.redpacts.frostpurge.game.views.GameCanvas;

public class PlayerController extends CharactersModel {
    private float angle;
    private PlayerModel player;
    PlayerController(PlayerModel player){
        angle = 0;
        this.player = player;
    }
    public void accelerate(float x, float y) {
        Vector2 vel = player.getVelocity();
        vel.x += .33f * x;
        vel.y -= .33f * y;
        player.setVelocity(vel.x, vel.y);
    }
    public void rotate(Boolean left) {
        if (left){
            player.setAngle(player.getAngle()+3f);
        }else{
            player.setAngle(player.getAngle()-3f);
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
        return player.getAngle();
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
        player.setAngle(0);
        player.setVelocity(0,0);
    }
    /**
     * Sets angle of the player so the character can be drawn correctly
     */
    private void setAngle(float x, float y){
        if (x != 0 && y!= 0){
            angle = (float) Math.atan2(-y,x);
        }
    }

    /**
     * Update function that will be called in gameplaycontroller to update the player actions.
     * Right now the input controller isn't done yet, so I am using booleans for buttons presses.
     */
    public void update(float horizontal, float vertical, boolean decelerate, boolean boost, boolean vacuum){
        setAngle(horizontal,vertical);
        System.out.println(angle);
        if (!decelerate){
            accelerate(horizontal,vertical);
        }else{
            stop();
        }
        if (boost){
            //Check boost then boost
        }
        if (vacuum){
            //Check if there is goop then vacuum
        }
        Vector2 newLocation = player.getLocation().add(player.getVelocity());
        player.setLocation(newLocation.x, newLocation.y);
    }
    public void draw(GameCanvas canvas){
        player.drawPlayer(canvas, (float) Math.toDegrees(angle));
    }
}
