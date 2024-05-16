package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
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
    public LevelModel initializeLevel (JsonValue leveljson, JsonValue tileProperties, TextureRegion[][] tileset, int twidth, int theight, AssetDirectory directory){
        tilesetWidth = twidth;
        tilesetHeight = theight;
        height = leveljson.getInt("height");
        width = leveljson.getInt("width");
        LevelModel level = new LevelModel(height, width, directory);

        JsonValue layer1 = leveljson.get("layers").child();
        JsonValue layer2 = layer1.next();
        JsonValue layer3 = layer2.next();
        JsonValue layer4 = layer3.next();
        JsonValue characters = layer4.next();

        initializeBaseTileLayer(level, layer1, tileset);
        initializeBase2TileLayer(level,layer2,tileset);
        initializeExtraTileLayer(level, layer3, tileset,tileProperties);
        initializeAccentTileLayer(level, layer4, tileset, tileProperties);
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
                index-=1; //NOTE: THIS IS A NUMBER THAT NEEDS TO BE ADJUSTED BASED ON TILSET SIZE AND ORDER
                //System.out.println(index);
                level.populateBase(height- 1-i/width, i%width, tileset[index/tilesetWidth][index%tilesetWidth]);
            }else{
                level.populateBase(height- 1-i/width, i%width, tileset[0][0]);
            }
        }
    }
    private void initializeBase2TileLayer(LevelModel level, JsonValue layer, TextureRegion[][]tileset){
        int[] data = layer.get("data").asIntArray();
        for (int i = 0; i<data.length;i++){
            int index = data[i];
            if (index!=0){
                index-=1; //NOTE: THIS IS A NUMBER THAT NEEDS TO BE ADJUSTED BASED ON TILSET SIZE AND ORDER
                level.populateBase2(height- 1-i/width, i%width, tileset[index/tilesetWidth][index%tilesetWidth]);
            }else{
                level.populateBase2(height- 1-i/width, i%width, tileset[0][0]);
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
        int base = 0;
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
                index-=1; //NOTE: THIS IS A NUMBER THAT NEEDS TO BE ADJUSTED BASED ON TILSET SIZE AND ORDER
            }
            while(!done){
                if (properties.getInt("id") == index){
                    JsonValue variables = properties.get("properties").child();
                    base = variables.getInt("value");
                    variables = variables.next();
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
                    level.populateObstacle(height-1- i/width, i%width, tileset[index/tilesetWidth][index%tilesetWidth], shape, base);
                    break;
                case "swamp":
                    level.populateSwamp(height-1- i/width, i%width, tileset[index/tilesetWidth][index%tilesetWidth], base);
                    break;
                case "empty tile":
                    level.populateEmpty(height-1- i/width, i%width, tileset[index/tilesetWidth][index%tilesetWidth], base);
                    break;
//                case "goal":
//                    level.populateGoal(height-1- i/width, i%width, tileset[index/tilesetWidth][index%tilesetWidth], base);
//                    break;
                default:
                    if (index!=0){
                        level.populateEmpty(height-1- i/width, i%width, tileset[index/tilesetWidth][index%tilesetWidth], base);
                    }
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
        int id;
        int base = 3;
        String type;
        String label;
        JsonValue objects = layer.get("objects").child();
        while (objects != null){
            x = objects.getInt("x");
            y = objects.getInt("y");
            rotation = objects.getInt("rotation");
            JsonValue properties = objects.get("properties").child();
            type = properties.getString("value");
            switch (type){
                case "player":
                    x+= 140;
                    level.createPlayer(x,(height*64-y),rotation,directory);
                    break;
                case "enemy":
                    properties = properties.next();
                    x+= 75;
                    id = properties.getInt("value");
                    //System.out.println(id);
                    level.createEnemy(x,height*64-y,rotation,directory,type, new int[] {(int)Math.floor((double) x /64), height - (int)Math.floor((double) y /64)}, id);
                    break;
                case "waypoint":
                    properties = properties.next();
                    id = properties.getInt("value");
                    properties = properties.next();
                    int pointNumber = properties.getInt("value");
                    x += 32;
                    //System.out.println(id);
                    level.addWaypoint(x,height*64 - y,id,pointNumber);
                    break;
                case "bouncy":
                    properties = properties.next();
                    id = properties.getInt("value");
                    properties = properties.next();
                    label = properties.getString("value");
                    level.createBouncy(x,(height*64-y),rotation,directory, id, label, -base);
                    break;
                case "breakable":
                    properties = properties.next();
                    id = properties.getInt("value");
                    properties = properties.next();
                    label = properties.getString("value");
                    level.createBreakable(x,(height*64-y),rotation,directory, id, label, -base);
                    break;
                case "goal":
                    properties = properties.next();
                    label = properties.getString("value");
                    level.createGoal(x,(height*64-y),rotation,directory, label, -base);
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
    public void initializeAccentTileLayer(LevelModel level, JsonValue layer, TextureRegion[][] tileset, JsonValue tileProperties){
        int[] data = layer.get("data").asIntArray();
        int base = 0;
        for (int i = 0; i<data.length;i++){
            JsonValue properties = tileProperties.get("tiles").child();
            int index = data[i];
            if (index!=0){
                index-=1; //NOTE: THIS IS A NUMBER THAT NEEDS TO BE ADJUSTED BASED ON TILESET SIZE AND ORDER
                boolean done = false;
                while(!done){
                    if (properties.getInt("id") == index){
                        JsonValue variables = properties.get("properties").child();
                        base = variables.getInt("value");
                        done = true;
                    } else{
                        properties = properties.next();
                    }
                }
                level.populateAccent(height- 1-i/width, i%width, tileset[index/tilesetWidth][index%tilesetWidth], base);
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
