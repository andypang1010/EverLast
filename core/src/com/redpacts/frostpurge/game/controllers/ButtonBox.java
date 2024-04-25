package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class ButtonBox {
    private int label;
    private float scale;
    private Rectangle bounds;
    private Texture texture;
    private boolean enlarged;

    ButtonBox(int label, float scale, Rectangle bounds, Texture texture) {
        this.label = label;
        this.scale = scale;
        this.bounds = bounds;
        this.texture = texture;
        this.enlarged = false;
    }

    public Texture getTexture() {return this.texture;}
    public Rectangle getBounds() {return this.bounds;}
    public int getLabel() {return this.label;}

    public void hoveringButton(){
        int x = Gdx.input.getX();
        int y = Gdx.graphics.getHeight()- Gdx.input.getY();
        float centerX = this.bounds.x + this.bounds.width/2;
        float centerY = this.bounds.y + this.bounds.height/2;
        if (bounds.contains(x, y) && !this.enlarged){
            this.enlarged = true;
            this.bounds.width = this.bounds.width * scale;
            this.bounds.height = this.bounds.height * scale;
            this.bounds.x = (int) centerX - this.bounds.width / 2;
            this.bounds.y = (int) centerY - this.bounds.height / 2;
        } else if(!bounds.contains(x,y) && this.enlarged){
            this.enlarged = false;
            this.bounds.width = this.bounds.width / scale;
            this.bounds.height = this.bounds.height / scale;
            this.bounds.x = (int) centerX - this.bounds.width / 2;
            this.bounds.y = (int) centerY - this.bounds.height / 2;
        }
    }
    public boolean isPressed(){
        int x = Gdx.input.getX();
        int y = Gdx.graphics.getHeight()- Gdx.input.getY();
        return bounds.contains(x, y);
    }
}