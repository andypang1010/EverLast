package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class ButtonBox {
    private int label;
    private float enlargeScale;
    private float screenScale;
    private Rectangle bounds;
    private Texture texture;
    private boolean enlarged;

    public ButtonBox(int label, float enlargeScale, float screenScale, Rectangle bounds, Texture texture) {
        this.label = label;
        this.enlargeScale = enlargeScale;
        this.screenScale = screenScale;
        this.bounds = bounds;
        this.texture = texture;
        this.enlarged = false;
    }

    public Texture getTexture() {return this.texture;}
    public Rectangle getBounds() {return this.bounds;}
    public int getLabel() {return this.label;}
    public void setScreenScale(float scale) {
        this.screenScale = scale;
    }

    /**
     * Adjusts the size and position of the button when hovered over by the mouse cursor.
     *
     * This method checks if the mouse cursor is hovering over the button's bounds and enlarges the button
     * if it is not already enlarged. When the cursor moves away from the button, it returns to its original size.
     * The resizing of the button ensures it maintains its center position.
     */
    public void hoveringButton(){
        int x = (int) (Gdx.input.getX() / screenScale);
        int y = (int) ((Gdx.graphics.getHeight()- Gdx.input.getY()) / screenScale);
        float centerX = this.bounds.x + this.bounds.width/2;
        float centerY = this.bounds.y + this.bounds.height/2;
        if (bounds.contains(x, y) && !this.enlarged){
            this.enlarged = true;
            this.bounds.width = this.bounds.width * enlargeScale;
            this.bounds.height = this.bounds.height * enlargeScale;
            this.bounds.x = (int) centerX - this.bounds.width / 2;
            this.bounds.y = (int) centerY - this.bounds.height / 2;
        } else if(!bounds.contains(x,y) && this.enlarged){
            this.enlarged = false;
            this.bounds.width = this.bounds.width / enlargeScale;
            this.bounds.height = this.bounds.height / enlargeScale;
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
        int x = (int) (Gdx.input.getX() / screenScale);
        int y = (int) ((Gdx.graphics.getHeight()- Gdx.input.getY()) / screenScale);
        return bounds.contains(x, y);
    }
}