package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.redpacts.frostpurge.game.models.LevelModel;
import com.redpacts.frostpurge.game.models.TileModel;

import java.util.logging.Level;

public class LevelController {
    private int tilesetWidth;
    private int tilesetHeight;
    private int height;
    private int width;
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
    public LevelModel initializeLevel (JsonValue leveljson, JsonValue tileProperties, TextureRegion[][] extratileset, TextureRegion[][] basetileset,int twidth, int theight, AssetDirectory directory){
        tilesetWidth = twidth;
        tilesetHeight = theight;
        height = leveljson.getInt("height");
        width = leveljson.getInt("width");
        LevelModel level = new LevelModel(height, width, directory);

        JsonValue layer1 = leveljson.get("layers").child();
        JsonValue layer2 = layer1.next();
        JsonValue layer3 = layer2.next();
        JsonValue characters = layer3.next();

        initializeBaseTileLayer(level, layer1, basetileset);
        initializeExtraTileLayer(level, layer2, extratileset,tileProperties);
        initializeAccentTileLayer(level, layer3, basetileset);
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
            if (index!=0){
                index-=93; //NOTE: THIS IS A NUMBER THAT NEEDS TO BE ADJUSTED BASED ON TILSET SIZE AND ORDER
                level.populateBase(height- 1-i/width, i%width, tileset[index/2][index%2]);
            }else{
                level.populateBase(height- 1-i/width, i%width, tileset[0][0]);
            }
        }
    }

    /**
     * This function initializes the secondary layer of tiles which is just the tiles that you can collide
     * with and have special effects. These tiles will have extra properties that you will parse in this function
     * @param level This is the instance of the level that we are storing everything into
     * @param layer This is the layer Json that we will be reading from to get all of the tiles
     */
    private void initializeExtraTileLayer(LevelModel level, JsonValue layer, TextureRegion[][]tileset, JsonValue tileProperties){
        boolean done;
        String type = "";
        String shape = "";
        int[] data = layer.get("data").asIntArray();
        for (int i = 0; i<data.length;i++){
            JsonValue properties = tileProperties.get("tiles").child();
            int index = data[i];
            if (index == 0){
                done = true;
                type = "none";
            }else{
                done = false;
            }
            index-=21; //NOTE: THIS IS A NUMBER THAT NEEDS TO BE ADJUSTED BASED ON TILSET SIZE AND ORDER
            while(!done){
                if (properties.getInt("id") == index){
                    JsonValue variables = properties.get("properties").child();
                    shape = variables.getString("value");
                    variables = variables.next();
                    type = variables.getString("value");
                    done = true;
                } else{
                    properties = properties.next();
                }
            }
            switch(type){
                case "obstacle":
                    level.populateObstacle(height-1- i/width, i%width, tileset[index/8][index%8], shape);
                    break;
                case "swamp":
                    level.populateSwamp(height-1- i/width, i%width, tileset[index/8][index%8]);
                    break;
                case "empty tile":
                    level.populateEmpty(height-1- i/width, i%width, tileset[index/8][index%8]);
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
        int x,y,rotation;
        int index = 0;
        String type;
        JsonValue objects = layer.get("objects").child();
        while (objects != null){
            x = objects.getInt("x");
            y = objects.getInt("y");
            rotation = objects.getInt("rotation");
            JsonValue properties = objects.get("properties").child();
            type = properties.getString("value");
            switch (type){
                case "player":
                    level.createPlayer(x,height*64-y,rotation,directory);
                    break;
                default:
                    properties = properties.next();
                    String end = properties.getString("value");
                    properties = properties.next();
                    String start = properties.getString("value");
                    level.createEnemy(x,height*64-y,rotation,directory,type, stringToCoordinate(start), stringToCoordinate(end), index);
                    index +=1;
                    break;

            }
            objects = objects.next();
        }
    }
    /**
     * This function initializes the initial layer of tiles which is just the tiles that you can regularly
     * walk on and also the tiles that are a part of the background
     * @param level This is the instance of the level that we are storing everything into
     * @param layer This is the layer Json that we will be reading from to get all of the tiles
     * @param tileset This is the tileset that the tiles are pulling their textures from
     */
    public void initializeAccentTileLayer(LevelModel level, JsonValue layer, TextureRegion[][] tileset){
        int[] data = layer.get("data").asIntArray();
        for (int i = 0; i<data.length;i++){
            int index = data[i];
            if (index!=0){
                index-=93; //NOTE: THIS IS A NUMBER THAT NEEDS TO BE ADJUSTED BASED ON TILESET SIZE AND ORDER
                level.populateAccent(height- 1-i/width, i%width, tileset[index/2][index%2]);
            }
        }
    }

    /**
     * Converts the string given into an array with the tile that is meant to patrol to or from
     * @param input The string that represents the tile location
     * @return The tile location in an array
     */
    private int[] stringToCoordinate(String input){
        String [] list = input.split(",");
        int [] coordinates = new int[2];
        coordinates[0] = Integer.parseInt(list[0]);
        coordinates[1] = Integer.parseInt(list[1]);
        return coordinates;
    }

}
