package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.redpacts.frostpurge.game.models.LevelModel;

import java.util.logging.Level;

public class LevelController {
    private int tilesetWidth;
    private int tilesetHeight;
    /**
     * Function to read the Json level file and add all the objects into the level model.
     * This will be called when the game initially loads so that all the Jsons are parsed
     * and the levels can be easily switched between.
     *
     * @param leveljson this the json file of the level that is made by Tiled and it's stored
     *                  in the asset folder
     * @param tileset This is the tileset where all the tiles and their properties are stored
     *                It is also made by Tiled but applies to all instances of those textures
     * @param twidth This is the width of the tileset in tiles
     * @param theight This is the height of the tileset in tiles
     */
    public LevelModel initializeLevel (JsonValue leveljson, JsonValue tileProperties, TextureRegion[][] tileset, int twidth, int theight, AssetDirectory directory){
        tilesetWidth = twidth;
        tilesetHeight = theight;
        int height = leveljson.getInt("height");
        int width = leveljson.getInt("width");
        LevelModel level = new LevelModel(width, height);

        JsonValue layer1 = leveljson.get("layers").child();
        JsonValue layer2 = layer1.next();
        JsonValue characters = layer2.next();

        initializeBaseTileLayer(level, layer1, tileset);
        initializeExtraTileLayer(level, layer2, tileset,tileProperties);
        initializeCharacterLayer(level, characters, directory);

        return level;
    }

    /**
     * This function initializes the initial layer of tiles which is just the tiles that you can regularly
     * walk on and also the tiles that are a part of the background
     * @param level This is the instance of the level that we are storing everything into
     * @param layer This is the layer Json that we will be reading from to get all of the tiles
     * @param tileset This is the tileset that the tiles are pulling their textures from
     */
    private void initializeBaseTileLayer(LevelModel level, JsonValue layer, TextureRegion[][]tileset){
        int[] data = layer.get("data").asIntArray();
        for (int i = 0; i<data.length;i++){
            int index = data[i];
            level.populateBase(i/20, i%20, tileset[index/tilesetWidth][index/tilesetHeight].getTexture());
        }
    }

    /**
     * This function initializes the secondary layer of tiles which is just the tiles that you can collide
     * with and have special effects. These tiles will have extra properties that you will parse in this function
     * @param level This is the instance of the level that we are storing everything into
     * @param layer This is the layer Json that we will be reading from to get all of the tiles
     */
    private void initializeExtraTileLayer(LevelModel level, JsonValue layer, TextureRegion[][]tileset, JsonValue tileProperties){
        String type = "";
        String shape = "";
        int[] data = layer.get("data").asIntArray();
        JsonValue properties = tileProperties.get("tiles").child();
        for (int i = 0; i<data.length;i++){
            int index = data[i];
            boolean done = false;
            while(!done){
                if (properties.getInt("id") == index){
                    JsonValue variables = properties.get("properties").child();
                    type = variables.getString("value");
                    variables = variables.next();
                    shape = variables.getString("value");
                    done = true;
                }else{
                    properties = properties.next();
                }
            }
            switch(type){
                case "obstacle":
                    level.populateObstacle(i/20, i%20, tileset[index/tilesetWidth][index/tilesetHeight].getTexture(), shape);
                    break;
                case "swamp":
                    level.populateSwamp(i/20,i%20,tileset[index/tilesetWidth][index/tilesetHeight].getTexture());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * This function initializes the last layer, which has all the information about the player and the
     * enemies. The enemies and the player have different properties which will be broken down in later functions
     * @param level This is the instance of the level that we are storing everything into
     * @param layer This is the layer Json that we will be reading from to get all of the characters in the game
     */
    private void initializeCharacterLayer(LevelModel level, JsonValue layer, AssetDirectory directory){

    }

}
