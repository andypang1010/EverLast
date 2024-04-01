package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.redpacts.frostpurge.game.models.PlayerModel;
import com.redpacts.frostpurge.game.views.GameCanvas;

public class PlayerController extends CharactersController {
    PlayerController(PlayerModel player){
        model = player;
        flip = false;
    }

    public void vacuum() {

    }
    public void boost() {

    }

    /**
     * resets the owner to origin for testing
     */
    private void reset(){
        model.setPosition(100, 100);
        model.setRotation(0);
        model.setVelocity(0,0);
    }
    /**
     * Sets angle of the owner so the character can be drawn correctly
     */
    private void setAngle(float x, float y){
        if (x != 0 && y!= 0){
            model.setRotation((float) Math.atan2(-y,x));
        }
    }
    /**
     * Checks if the player has any resources
     */
    public boolean hasResources(){
        return ((PlayerModel) model).getCanBoost();
    }

    /**
     * Update function that will be called in gameplaycontroller to update the owner actions.
     * Right now the input controller isn't done yet, so I am using booleans for buttons presses.
     */
    public void update(float horizontal, float vertical, boolean decelerate, boolean boost, boolean vacuum){
//        if (!decelerate){
//            accelerate(horizontal,vertical);
//        }else{
//            stop();
//        }
//        if (boost && ((PlayerModel) model).getCanBoost()){
//            model.getVelocity().scl(1.5f);
//            ((PlayerModel) model).setCanBoost(false);
//        }
//        if (vacuum){
//            //Check if there is goop then vacuum
//        }
//        if (Math.abs(horizontal) >= .1f || Math.abs(vertical) >= .1f){
//            model.setRotation(-(float) Math.toDegrees(Math.atan2(vertical,horizontal)));
//        }
//        friction();
//        Vector2 newLocation = model.getPosition().add(model.getVelocity());
        //System.out.println(model.getVelocity());
        setAngle(horizontal,vertical);
        if(!decelerate) {
            model.getBody().applyForceToCenter(horizontal, -vertical, true);
        }else{
            model.getBody().setLinearVelocity(model.getBody().getLinearVelocity().scl(0.95f));
        }
        if (boost && ((PlayerModel) model).getCanBoost()){
            model.getBody().applyForceToCenter(horizontal*100f, -vertical*100f, true);
            ((PlayerModel) model).setCanBoost(false);
        }
        if (Math.abs(horizontal) >= .1f || Math.abs(vertical) >= .1f){
            model.setRotation(-(float) Math.toDegrees(Math.atan2(vertical,horizontal)));
        }
        model.getBody().setLinearVelocity(model.getBody().getLinearVelocity().scl(0.99f));//friction
        model.setPosition(model.getBody().getPosition().scl(10));
    }
    public void draw(GameCanvas canvas, float horizontal, float vertical){

        if (horizontal<0){
            flip = true;
        }else if (horizontal >0){
            flip = false;
        }
        if (Math.abs(model.getVelocity().y) + Math.abs(model.getVelocity().x) > 1 || Math.abs(horizontal) + Math.abs(vertical)>1) {
            processRun();
            model.drawCharacter(canvas, (float) Math.toDegrees(model.getRotation()), Color.WHITE, "running", flip);
            ((PlayerModel) model).drawFire(canvas, flip);
            //((PlayerModel) model).drawBody(canvas);
        }else{
            model.resetFilmStrip(model.getFilmStrip());
            model.drawCharacter(canvas, (float) Math.toDegrees(model.getRotation()), Color.WHITE, "idle", flip);
        }




    }
}
