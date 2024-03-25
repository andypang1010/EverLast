package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.redpacts.frostpurge.game.controllers.EnemyController;
import com.redpacts.frostpurge.game.util.TileGraph;

import java.util.ArrayList;
import java.util.logging.Level;

public class LevelModel {
    private TileModel[][] baseLayer;
    private TileModel[][] extraLayer;
    private Array<EnemyModel> enemies;
    private PlayerModel player;
    public LevelModel(int height, int width){
        baseLayer = new TileModel[height][width];
        extraLayer = new TileModel[height][width];
        enemies = new Array<>();
    }

    /**
     * Used to populate the array for the base layer by creating a emptytile in the appropriate index
     * @param i row that the tile is on
     * @param j column that the tile is in
     * @param texture the texture for the empty tile
     */
    public void populateBase(int i, int j, TextureRegion texture){
        if (texture == null){
            System.out.println("uh oh");
        }
        baseLayer[i][j] = new EmptyTile(texture, new Vector2(j*64,i*64));
    }

    /**
     * Function to add an obstacle to the board at the appropriate index
     * @param i row that the tile is on
     * @param j column that the tile is in
     * @param texture the texture for the obstacle
     * @param shape The shape that the obstacle is (circle, LRtriangle, square)
     */
    public void populateObstacle(int i, int j, TextureRegion texture, String shape){
        //TODO Make the obstacles have different types of hitboxes EX: Circle vs Full Tile vs Triangle
        extraLayer[i][j] = new ObstacleTile(texture, new Vector2(j*64,i*64), 1);
    }

    /**
     * Function to add a swamp to the board at the appropriate index
     * @param i row that the swamp tile is on
     * @param j row that the swamp tile is in
     * @param texture texture of the swamp tile
     */
    public void populateSwamp(int i, int j, TextureRegion texture){
        extraLayer[i][j] = new SwampTile(texture);
    }

    /**
     * Creates the player in the scene so that they are instantiated correctly
     * @param x x coordinate of spawn point
     * @param y y coordinate of spawn point
     * @param rotation initial rotation of player
     * @param directory directory that can be used to get all animations and textures
     */
    public void createPlayer(int x, int y, int rotation, AssetDirectory directory){
        player = new PlayerModel(new Vector2(x,y), rotation, directory);
    }

    /**
     * Creates an enemy that will be put into an enemycontroller in the enemy list
     * @param x x coordinate of spawn point
     * @param y y coordinate of spawn point
     * @param rotation initial rotation of player
     * @param type the type of enemy that is being created
     * @param directory The directory so that the enemy can get it's animations
     * @param start starting patrol tile for enemy
     * @param end ending patrol tile for the enemy
     * @param index number enemy on in the level
     * @return enemy to be put into an enemy controller
     */
    public void createEnemy(int x, int y, int rotation, AssetDirectory directory, String type, int[] start, int[]end, int index){
        //TODO: Add different enemy types so that the types actually matter
        // NOTE: THIS ONLY SUPPORTS UP TO TWENTY ENEMIES
        enemies.add(new EnemyModel(new Vector2(x,y), rotation, directory, start,end));
    }

    public TileModel[][] getBaseLayer(){
        return baseLayer;
    }
    public TileModel[][] getExtraLayer(){
        return extraLayer;
    }
    public Array<EnemyModel> getEnemies(){
        return enemies;
    }
    public PlayerModel getPlayer(){
        return player;
    }
}
