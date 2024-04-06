package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Null;
import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.redpacts.frostpurge.game.controllers.EnemyController;
import com.redpacts.frostpurge.game.util.TileGraph;
import com.redpacts.frostpurge.game.views.GameCanvas;

import java.util.ArrayList;
import java.util.logging.Level;

public class LevelModel {
    private int width;
    private int height;
    private TileModel[][] baseLayer;
    private TileModel[][] extraLayer;
    private TileModel[][] accentLayer;
    private Array<EnemyModel> enemies;
    private PlayerModel player;
    private boolean altered;
    private AssetDirectory directory;
    /** The dimensions of a single tile */
    private static final int TILE_WIDTH = 64;
    public LevelModel(int height, int width, AssetDirectory directory){
        this.height = height;
        this.width = width;
        baseLayer = new TileModel[height][width];
        extraLayer = new TileModel[height][width];
        accentLayer = new TileModel[height][width];
        enemies = new Array<>();
        altered = false;
        this.directory = directory;
    }

    /**
     * Used to populate the array for the base layer by creating a emptytile in the appropriate index
     * @param i row that the tile is on
     * @param j column that the tile is in
     * @param texture the texture for the empty tile
     */
    public void populateBase(int i, int j, TextureRegion texture){
        baseLayer[i][j] = new EmptyTile(texture, new Vector2(j*64,i*64));
    }
    /**
     * Used to populate the array for the accent layer by creating an emptytile in the appropriate index
     * @param i row that the tile is on
     * @param j column that the tile is in
     * @param texture the texture for the empty tile
     */
    public void populateAccent(int i, int j, TextureRegion texture){
        accentLayer[i][j] = new EmptyTile(texture, new Vector2(j*64,i*64));
    }

    /**
     * Function to add an obstacle to the board at the appropriate index
     * @param i row that the tile is on
     * @param j column that the tile is in
     * @param texture the texture for the obstacle
     * @param shape The shape that the obstacle is (circle, LRtriangle, square)
     */
    public void populateObstacle(int i, int j, TextureRegion texture, String shape, int base){
        //TODO Make the obstacles have different types of hitboxes EX: Circle vs Full Tile vs Triangle
        extraLayer[i][j] = new ObstacleTile(texture, new Vector2(j*64,i*64), 1, base);
    }

    /**
     * Function to add a swamp to the board at the appropriate index
     * @param i row that the swamp tile is on
     * @param j row that the swamp tile is in
     * @param texture texture of the swamp tile
     */
    public void populateSwamp(int i, int j, TextureRegion texture, int base){
        extraLayer[i][j] = new SwampTile(texture, new Vector2(j*64,i*64), 1, base);
    }
    /**
     * Function to add an empty to the extra layer at the appropriate index
     * @param i row that the swamp tile is on
     * @param j row that the swamp tile is in
     * @param texture texture of the swamp tile
     */
    public void populateEmpty(int i, int j, TextureRegion texture, int base){
        extraLayer[i][j] = new EmptyTile(texture, new Vector2(j*64,i*64), base);
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
    public int getWidth(){return width;}
    public int getHeight(){return height;}
    public TileModel[][] getBaseLayer(){
        return baseLayer;
    }
    public TileModel[][] getExtraLayer(){
        return extraLayer;
    }
    public TileModel[][] getAccentLayer(){
        return accentLayer;
    }
    public Array<EnemyModel> getEnemies(){
        return enemies;
    }
    public PlayerModel getPlayer(){
        return player;
    }
    public boolean getAltered(){return altered;}
    public void Alter(){altered = true;}

    /**
     * Returns the tile object for the given position
     *
     * Returns null if that position is out of bounds.
     *
     * @return the tile object for the given position
     */
    public TileModel getTileState(int x, int y) {
        if (!inBounds(x, y)) {
            return null;
        }
        if (extraLayer[y][x]==null){
            return baseLayer[y][x];
        }else{
            return extraLayer[y][x];
        }
    }
    public TileModel getTileState(float x, float y) {
        int intx = (int) Math.floor(x/60);
        int inty = (int) Math.floor(y/60);
        if (!inBounds(inty, intx)) {
            return null;
        }
        if (extraLayer[inty][intx]==null){
            return baseLayer[inty][intx];
        }else{
            return extraLayer[inty][intx];
        }
    }
    public boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public Array<TileModel> getTileNeighbors(int x, int y){
        Array<TileModel> neighbors = new Array<TileModel>();
        for(int i = x-1; i <= x+1; i++){
            for(int j = y-1; j <= y+1; j++){
                if(i == x && j == y){
                    continue;
                }
                if(inBounds(i, j)){
                    neighbors.add(getTileState(i, j));
                }
            }
        }
        return neighbors;
    }

    public void drawTile(TileModel object, GameCanvas canvas){
        canvas.draw(object.getTextureRegion(), object.getPosition().x, object.getPosition().y);
    }

    public void drawDebug(TileModel object, GameCanvas canvas) {
        if (object != null && object.shape != null) {
            System.out.println(object.shape.getVertexCount());
            System.out.println(object.getPosition());
            System.out.println("\n");
            canvas.drawPhysics(object.shape, Color.GREEN, object.getPosition().x, object.getPosition().y, 0, 1, 1);
        }
    }

    public boolean isSwampTile(float x, float y){
        int indexx = (int) Math.floor(x/60);
        int indexy = (int) Math.floor(y/60);
        if (!inBounds(indexx,indexy)){
            return false;
        }
        return extraLayer[indexy][indexx] instanceof SwampTile;
    }
    public void removeExtra(float x, float y){
        int indexx = (int) Math.floor(x/60);
        int indexy = (int) Math.floor(y/60);
        extraLayer[indexy][indexx] = null;
    }
}
