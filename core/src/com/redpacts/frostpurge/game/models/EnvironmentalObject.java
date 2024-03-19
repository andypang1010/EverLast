package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class EnvironmentalObject extends GameObject{
    public enum ObjectType{
        PLANT,
        HOUSE,
        SWAMP
    }
    private ObjectType type;
    public EnvironmentalObject(ObjectType type, int x, int y){
        this.type = type;
        this.position = new Vector2(x, y);
    }

    public ObjectType getType(){
        return this.type;
    }
}
