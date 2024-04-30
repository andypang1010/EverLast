package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.redpacts.frostpurge.game.controllers.GameMode;
import com.redpacts.frostpurge.game.util.XBoxController;

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
    public boolean getEnlarged(){return this.enlarged;}
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
    public void hoveringButton(XBoxController xbox, float[] time, ButtonBox pausebutton, ButtonBox resumebutton, ButtonBox levelselectbutton, ButtonBox homebutton, ButtonBox retrybutton,GameMode.GameState state){
        if (xbox!=null){
            if (this.enlarged && time[0] >.2f) {
                float x = xbox.getLeftX();
                float y = xbox.getLeftY();
                if (Math.abs(x) < .5) {
                    x = 0;
                }
                if (Math.abs(y) < .5) {
                    y = 0;
                }
                y *=-1;
                switch (state) {
                    case PAUSE:
                        switch (this.label) {
                            case 0:
                                if (y < 0) {
                                    this.resize("down");
                                    resumebutton.resize("up");
                                    time[0] = 0;
                                }
                                break;
                            case 1:
                                if (y < 0 || x < 0) {
                                    this.resize("down");
                                    levelselectbutton.resize("up");
                                    time[0] = 0;
                                } else if (y > 0) {
                                    this.resize("down");
                                    pausebutton.resize("up");
                                    time[0] = 0;
                                }
                                break;
                            case 3:
                                if (x < 0) {
                                    this.resize("down");
                                    homebutton.resize("up");
                                    time[0] = 0;
                                } else if (y > 0 || x > 0) {
                                    this.resize("down");
                                    resumebutton.resize("up");
                                    time[0] = 0;
                                }
                                break;
                            case 2:
                                if (x>0){
                                    this.resize("down");
                                    levelselectbutton.resize("up");
                                    time[0] = 0;
                                }
                                break;
                        }
                        break;
                    case WIN:
                        switch (this.label) {
                            case 3:
                                if (x < 0) {
                                    this.resize("down");
                                    homebutton.resize("up");
                                    time[0] = 0;
                                }
                                break;
                            case 2:
                                if (x>0){
                                    this.resize("down");
                                    levelselectbutton.resize("up");
                                    time[0] = 0;
                                }
                                break;
                        }
                        break;
                    case OVER:
                        switch (this.label) {
                            case 3:
                                if (x < 0) {
                                    this.resize("down");
                                    homebutton.resize("up");
                                    time[0] = 0;
                                } else if (y > 0 || x > 0) {
                                    this.resize("down");
                                    retrybutton.resize("up");
                                    time[0] = 0;
                                }
                                break;
                            case 2:
                                if (x>0){
                                    this.resize("down");
                                    levelselectbutton.resize("up");
                                    time[0] = 0;
                                }
                                break;
                            case 4:
                                if (y < 0 || x < 0) {
                                    this.resize("down");
                                    levelselectbutton.resize("up");
                                    time[0] = 0;
                                }
                                break;
                        }

                }
            }

        }else{
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
    }

    public void resize(String direction){
        float centerX = this.bounds.x + this.bounds.width/2;
        float centerY = this.bounds.y + this.bounds.height/2;
        if (direction.equals("up") && !this.enlarged){
            this.enlarged = true;
            this.bounds.width = this.bounds.width * enlargeScale;
            this.bounds.height = this.bounds.height * enlargeScale;
            this.bounds.x = (int) centerX - this.bounds.width / 2;
            this.bounds.y = (int) centerY - this.bounds.height / 2;
        }else if (direction.equals("down")&& this.enlarged){
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