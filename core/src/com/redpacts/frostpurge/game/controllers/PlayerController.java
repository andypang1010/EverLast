package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.redpacts.frostpurge.game.models.PlayerModel;
import com.redpacts.frostpurge.game.views.GameCanvas;

public class PlayerController extends CharactersController {
    PlayerController(PlayerModel player){
        model = player;
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
     * Update function that will be called in gameplaycontroller to update the owner actions.
     * Right now the input controller isn't done yet, so I am using booleans for buttons presses.
     */
    public void update(float horizontal, float vertical, boolean decelerate, boolean boost, boolean vacuum){
        setAngle(horizontal,vertical);
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
      
        Vector2 newLocation = model.getPosition().add(model.getVelocity());
        model.setPosition(newLocation.x, newLocation.y);
    }
    public void draw(GameCanvas canvas){
        model.drawCharacter(canvas, (float) Math.toDegrees(model.getRotation()), Color.WHITE);
    }
}
