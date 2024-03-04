package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.Gdx;
import com.redpacts.frostpurge.game.views.GameCanvas;

public class PlayerModel {

    /**
    location of the player where the first index is the x coordinate and the second
    is the y coordinate
     */
    private Vector2 location;
    /**
    Angle of the player in radians where 0 is the player facing right and it follows
    standard unit circle conventions
     */
    private float angle;
    /**
     * Velocity vector of the player where the first index is the x coordinate and the
     * second is the y coordinate
     */
    private Vector2 velocity;
    /**
     *sprite for the player
     */
    private Texture texture;

    public Vector2 getLocation(){
        return location;
    }
    public void setLocation(float x, float y){
        this.location = new Vector2(x, y);
    }
    public void setLocation(Vector2 location){
        this.location = location;
    }
    public float getAngle(){
        return angle;
    }
    public void setAngle(float angle){
        this.angle = angle;
    }
    public Vector2 getVelocity(){
        return velocity;
    }
    public void setVelocity(float x, float y){
        this.velocity = new Vector2(x, y);
    }
    public void setVelocity(Vector2 v){
        this.velocity = new Vector2(v);
    }
    /**
     * Instantiates the player with their starting location and angle and with their texture
     * @param location vector2 representing the starting location
     * @param angle float representing angle the player is facing
     */
    public PlayerModel(Vector2 location, float angle){
        this.location = location;
        this.angle = angle;
        this.velocity = new Vector2(0,0);
        FileHandle fileHandle = Gdx.files.internal("redpacts.jpg");
        Pixmap pixmap = new Pixmap(fileHandle);
        this.texture = new Texture(pixmap);
        pixmap.dispose();
    }
    /**
     * draws the player onto the game canvas
     */
    public void drawPlayer(GameCanvas canvas){
        float ox = 0.5f * texture.getWidth();
        float oy = 0.5f * texture.getHeight();
        canvas.draw(texture, Color.WHITE,ox,oy,location.x,location.y,angle,.1f,.1f);
    }

}
