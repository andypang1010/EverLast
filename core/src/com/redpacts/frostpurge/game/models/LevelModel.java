package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.redpacts.frostpurge.game.controllers.EnemyController;
import com.redpacts.frostpurge.game.util.TileGraph;

import java.util.logging.Level;

public class LevelModel {
    private TileModel[][] baseLayer;
    private TileModel[][] extraLayer;
    private EnemyController[] enemies;
    private PlayerModel player;
    public LevelModel(int width, int height){
        baseLayer = new TileModel[width][height];
        extraLayer = new TileModel[width][height];
    }

    /**
     * Used to populate the array for the base layer by creating a emptytile in the appropriate index
     * @param i row that the tile is on
     * @param j column that the tile is in
     * @param texture the texture for the empty tile
     */
    public void populateBase(int i, int j, Texture texture){
        baseLayer[i][j] = new EmptyTile(texture);
    }

    /**
     * Function to add an obstacle to the board at the appropriate index
     * @param i row that the tile is on
     * @param j column that the tile is in
     * @param texture the texture for the obstacle
     * @param shape The shape that the obstacle is (circle, LRtriangle, square)
     */
    public void populateObstacle(int i, int j, Texture texture, String shape){
        //TODO Make the obstacles have different types of hitboxes EX: Circle vs Full Tile vs Triangle
        extraLayer[i][j] = new ObstacleTile(texture, new Vector2(i,j), 1);
    }

    /**
     * Function to add a swamp to the board at the appropriate index
     * @param i row that the swamp tile is on
     * @param j row that the swamp tile is in
     * @param texture texture of the swamp tile
     */
    public void populateSwamp(int i, int j, Texture texture){
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
     * @return enemy to be put into an enemy controller
     */
    public EnemyModel createEnemy(int x, int y, int rotation, AssetDirectory directory, String type){
        //TODO: Make it so that the enemies store where their patrol paths are
        return new EnemyModel(new Vector2(x,y), rotation, directory);
    }
    public void createController(EnemyModel enemy, PlayerModel target, int startpatrolx, int startpatroly, int endpatrolx, int endpatroly, TileGraph)
}
