package com.redpacts.frostpurge.game.models;


import com.badlogic.gdx.math.Vector2;

public class EnemyModel extends CharactersModel{
    private Vector2 location;
    private float angle;
    private Vector2 velocity;
    @Override
    public void accelerate() {
        super.accelerate();
    }

    @Override
    public void rotate() {
        super.rotate();
    }

    @Override
    public void stop() {
        super.stop();
    }

    public Vector2 getLocation(){
        return location;
    }
    public void setLocation(float x, float y){
        this.location = new Vector2(x, y);
    }

    @Override
    public Vector2 getVelocity() {
        return velocity;
    }

    @Override
    public float getRotation() {
        return super.getRotation();
    }

    public void setVelocity(float x, float y){
        this.velocity = new Vector2(x, y);
    }
}