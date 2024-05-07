package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.audio.Sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.redpacts.frostpurge.game.models.PlayerModel;
import com.redpacts.frostpurge.game.util.FilmStrip;
import com.redpacts.frostpurge.game.views.GameCanvas;

public class PlayerController extends CharactersController {

    static final float MAX_OFFSET = 500f;
    private static final float MAX_SPEED = 100;
    private static final float BOOST_MAX_SPEED = 120;
    static final float OFFSET_MULTIPLIER = 2f;

    /** The sound for accelerating */
    private Sound accelerateSound;
    /** The sound for boosting */
    private Sound boostSound;


    PlayerController(PlayerModel player){
        model = player;
        flip = false;

        accelerateSound = ((PlayerModel) model).getActionSound(PlayerModel.Actions.ACCELERATE);
        boostSound = ((PlayerModel) model).getActionSound(PlayerModel.Actions.BOOST);
    }

    public void vacuum() {
        if(((PlayerModel)model).getVacuumingProgression() == 0){
            ((PlayerModel)model).setVacuumingProgression(1);
        }
    }

    public void boost(float horizontal, float vertical) {
        if(((PlayerModel) model).getBoostNum() > 0 && ((PlayerModel) model).getBoostCoolDown() == 0){
            playBoost();
            model.getBody().applyForceToCenter(horizontal*100f, -vertical*100f, true);
            ((PlayerModel) model).addCanBoost(-1);
            ((PlayerModel) model).resetBoostCoolDown();
        }
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
            model.setRotation((float) Math.toDegrees(Math.atan2(-y,x)));
        }
    }
    /**
     * Checks if the player has any resources
     */
    public boolean hasResources(){
        return ((PlayerModel) model).getBoostNum() > 0;
    }

    /**
     * Update function that will be called in gameplaycontroller to update the owner actions.
     * Right now the input controller isn't done yet, so I am using booleans for buttons presses.
     */
    public void update(float horizontal, float vertical, boolean decelerate, boolean boost, boolean vacuum){
        ((PlayerModel) model).addBoostCoolDown(-1);
        if (((PlayerModel) model).getInvincibility()){
            ((PlayerModel) model).addInvincibility();
        }
//         Switch vacuum state
        if(((PlayerModel) model).getVacuumingProgression() > 0){
            int vacuumFrame = ((PlayerModel)model).getVacuumingProgression();
            if(vacuumFrame >= 1 && vacuumFrame <= 9){
                ((PlayerModel) model).setVacuumingState(PlayerModel.VacuumingState.START);
            }else if(vacuumFrame >= 10 && vacuumFrame <= 19){
                ((PlayerModel) model).setVacuumingState(PlayerModel.VacuumingState.VACUUM);
                if(vacuum){
                    ((PlayerModel) model).addVacuumingProgression(-1);
                }
            }else if(vacuumFrame >= 20 && vacuumFrame <= 30){
                ((PlayerModel) model).setVacuumingState(PlayerModel.VacuumingState.END);
            }
            ((PlayerModel) model).addVacuumingProgression(1);
            model.getBody().setLinearVelocity(model.getBody().getLinearVelocity().scl(0.95f));
        }else{
            ((PlayerModel) model).setVacuumingState(PlayerModel.VacuumingState.NONE);
        }
        setAngle(horizontal,vertical);

        if (!decelerate){
            playAccelerate(true);
//            System.out.println("PLAYER VELOCITY: " + model.getBody().getLinearVelocity().len());
            model.getBody().applyForceToCenter(horizontal*1.5f, -vertical*1.5f, true);

//            model.getBody().setLinearVelocity(model.getBody().getLinearVelocity().cpy().nor().scl(Math.min(model.getBody().getLinearVelocity().len(), MAX_SPEED)));
        }else{
            playAccelerate(false);
            model.getBody().setLinearVelocity(model.getBody().getLinearVelocity().scl(0.95f));
        }

        if (boost){
            this.boost(horizontal, vertical);

        }
//        if(vacuum){
//            this.vacuum();
//        }
        if (Math.abs(horizontal) >= .1f || Math.abs(vertical) >= .1f){
            model.setRotation(-(float) Math.toDegrees(Math.atan2(vertical,horizontal)));
        }
        model.getBody().setLinearVelocity(model.getBody().getLinearVelocity().scl(0.99f));//friction
        model.setPosition(model.getBody().getPosition().scl(10));
    }

    private void playAccelerate(boolean on) {
        long soundId = ((PlayerModel) model).getActionId(PlayerModel.Actions.ACCELERATE);

        if (on) {
            accelerateSound.setVolume(soundId, Math.min(model.getBody().getLinearVelocity().len() / 120f, 1f));

            if (soundId == -1) {
                soundId = accelerateSound.loop();
                ((PlayerModel) model).setActionId(PlayerModel.Actions.ACCELERATE, soundId);
            }
        } else {
            ((PlayerModel) model).setActionId(PlayerModel.Actions.ACCELERATE, -1);
            accelerateSound.stop(soundId);
        }
    }

    public void playBoost() {
        boostSound.play(1);
    }

    public Vector2 cameraOffsetPos() {
        Vector2 pos = model.getPosition().cpy();
        Vector2 dir = model.getBody().getLinearVelocity().nor();
        return pos.interpolate(new Vector2(dir.x * MAX_OFFSET + pos.x, dir.y * MAX_OFFSET + pos.y), model.getBody().getLinearVelocity().len() / 100f, Interpolation.smooth);
    }

    private void resetOtherFilmStrips(FilmStrip filmStrip){
    }

    public void draw(GameCanvas canvas, float horizontal, float vertical){
        drawShadow(canvas);
        // Draw player
        switch (((PlayerModel) model).getGameOverState()) {
            case 0: // Player active
                String direction = getDirection(horizontal,vertical,previousDirection);
                int vacuumFrame = ((PlayerModel)model).getVacuumingProgression();
                if(((PlayerModel) model).getVacuumingState() == PlayerModel.VacuumingState.START){
                    model.resetFilmStrip(model.getFilmStrip("vacuum"+direction));
                    model.resetFilmStrip(model.getFilmStrip("vacuumend"+direction));
                    model.resetFilmStrip(model.getFilmStrip("idle"+direction));
                    model.resetFilmStrip(model.getFilmStrip(direction));
                    processRun("vacuumstart"+direction);
                    model.drawCharacter(canvas, (float) Math.toDegrees(model.getRotation()), Color.WHITE, "vacuuming_start", direction);
                    ((PlayerModel) model).drawFire(canvas);
                }else if(((PlayerModel) model).getVacuumingState() == PlayerModel.VacuumingState.VACUUM){
                    model.resetFilmStrip(model.getFilmStrip("vacuumstart"+direction));
                    model.resetFilmStrip(model.getFilmStrip("vacuumend"+direction));
                    model.resetFilmStrip(model.getFilmStrip("idle"+direction));
                    model.resetFilmStrip(model.getFilmStrip(direction));
                    processRun("vacuum"+direction);
                    model.drawCharacter(canvas, (float) Math.toDegrees(model.getRotation()), Color.WHITE, "vacuuming", direction);
                    ((PlayerModel) model).drawFire(canvas);
                }else if(((PlayerModel) model).getVacuumingState() == PlayerModel.VacuumingState.END){
                    model.resetFilmStrip(model.getFilmStrip("vacuumstart"+direction));
                    model.resetFilmStrip(model.getFilmStrip("vacuum"+direction));
                    model.resetFilmStrip(model.getFilmStrip("idle"+direction));
                    model.resetFilmStrip(model.getFilmStrip(direction));
                    processRun("vacuumend"+direction);
                    model.drawCharacter(canvas, (float) Math.toDegrees(model.getRotation()), Color.WHITE, "vacuuming_end", direction);
                    ((PlayerModel) model).drawFire(canvas);
                }else if(Math.abs(model.getBody().getLinearVelocity().y) + Math.abs(model.getBody().getLinearVelocity().x) > 1 || Math.abs(horizontal) + Math.abs(vertical)>.5) {
                    model.resetFilmStrip(model.getFilmStrip("vacuumstart"+direction));
                    model.resetFilmStrip(model.getFilmStrip("vacuum"+direction));
                    model.resetFilmStrip(model.getFilmStrip("vacuumend"+direction));
                    model.resetFilmStrip(model.getFilmStrip("idle"+direction));
                    processRun(direction);
                    model.drawCharacter(canvas, (float) Math.toDegrees(model.getRotation()), Color.WHITE, "running", direction);
                    ((PlayerModel) model).drawFire(canvas);
                }else{
                    //System.out.println(Math.abs(model.getVelocity().y) + Math.abs(model.getVelocity().x));
                    model.resetFilmStrip(model.getFilmStrip("vacuumstart"+direction));
                    model.resetFilmStrip(model.getFilmStrip("vacuum"+direction));
                    model.resetFilmStrip(model.getFilmStrip("vacuumend"+direction));
                    model.resetFilmStrip(model.getFilmStrip(direction));
                    processRun("idle"+direction);
                    model.drawCharacter(canvas, (float) Math.toDegrees(model.getRotation()), Color.WHITE, "idle", direction);
                }
                previousDirection = direction;
                break;
            case 1: // Player wins
                processRun("win");
                model.drawCharacter(canvas, (float) Math.toDegrees(model.getRotation()), Color.WHITE, "win", previousDirection);
                break;
            case -1: // Player loses
                processRun("death");
                model.drawCharacter(canvas, (float) Math.toDegrees(model.getRotation()), Color.WHITE, "death", previousDirection);
                break;
        }
    }

    private void drawShadow(GameCanvas canvas){
        // Draw shadow
        short[] indices = new short[3];
        indices[0] = 0;
        indices[1] = 1;
        indices[2] = 2;

        Vector2 rayStart = model.getBody().getPosition().cpy();
        int numRays = 20; // Number of segments for circle
        float deltaAngle = 360f / (numRays - 1); // Angle between each segment

        float angle = 0;
        Vector2 dir = new Vector2(1, 0);
        Vector2 rayDirection = dir.cpy().rotateDeg(angle);
        Vector2 rayEnd = rayStart.cpy().add(rayDirection.scl(((PlayerModel)model).getRadius())); // Calculate end point of the ray
        Vector2 rayPrevious = rayEnd.cpy();
        Vector2 ray1, ray2, ray3;

        for (int i = 1; i < numRays; i++) {
            angle += deltaAngle;
            rayDirection = dir.cpy().rotateDeg(angle);
            rayEnd = rayStart.cpy().add(rayDirection.scl(((PlayerModel)model).getRadius()));

            ray1 = rayStart.cpy().scl(10).add(-100, -100);
            ray2 = rayPrevious.cpy().scl(10).add(-100, -100);
            ray3 = rayEnd.cpy().scl(10).add(-100, -100);

            float[] vertices = {ray1.x, ray1.y, ray2.x, ray2.y, ray3.x, ray3.y};
            PolygonRegion cone = new PolygonRegion(new TextureRegion(), vertices, indices);
            canvas.draw(cone, new Color(0f, 0f, 0f, 0.5f), 100, 100 ,0);

            rayPrevious = rayEnd.cpy();
        }
    }
}
