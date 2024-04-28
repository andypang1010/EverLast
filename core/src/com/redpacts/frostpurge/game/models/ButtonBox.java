package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class ButtonBox {
    private int label;
    private float scale;
    private Rectangle bounds;
    private Texture texture;
    private boolean enlarged;

    public ButtonBox(int label, float scale, Rectangle bounds, Texture texture) {
        this.label = label;
        this.scale = scale;
        this.bounds = bounds;
        this.texture = texture;
        this.enlarged = false;
    }

    public Texture getTexture() {return this.texture;}
    public Rectangle getBounds() {return this.bounds;}
    public int getLabel() {return this.label;}

    /**
     * Adjusts the size and position of the button when hovered over by the mouse cursor.
     *
     * This method checks if the mouse cursor is hovering over the button's bounds and enlarges the button
     * if it is not already enlarged. When the cursor moves away from the button, it returns to its original size.
     * The resizing of the button ensures it maintains its center position.
     */
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


    /**
     * Checks if the button is currently being pressed based on the mouse cursor's position.
     *
     * This method retrieves the x and y coordinates of the mouse cursor and verifies whether
     * it lies within the button's bounds, indicating a press if true.
     *
     * @return True if the button is pressed, false otherwise.
     */
    public boolean isPressed(){
        int x = Gdx.input.getX();
        int y = Gdx.graphics.getHeight()- Gdx.input.getY();
        return bounds.contains(x, y);
    }
}