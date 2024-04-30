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
import com.redpacts.frostpurge.game.util.EnemyStates;
import com.redpacts.frostpurge.game.util.FilmStrip;
import com.redpacts.frostpurge.game.util.TileGraph;
import com.redpacts.frostpurge.game.views.GameCanvas;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;

public class LevelModel {
    private int width;
    private int height;
    private TileModel[][] baseLayer;
    private TileModel[][] extraLayer;
    private TileModel[][] accentLayer;
    private Array<EnemyModel> enemies;
    private Array<BouncyTile> bouncy;
    private Array<BreakableTile> breakables;
    private PlayerModel player;
    private boolean altered;
    private String name;
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
        bouncy = new Array<>();
        breakables = new Array<>();
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
    public void populateAccent(int i, int j, TextureRegion texture, int base){
        accentLayer[i][j] = new EmptyTile(texture, new Vector2(j*64,i*64), base);
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
     * Function to add an goal to the extra layer at the appropriate index
     * @param i row that the swamp tile is on
     * @param j row that the swamp tile is in
     * @param texture texture of the swamp tile
     */
    public void populateGoal(int i, int j, TextureRegion texture, int base){
        extraLayer[i][j] = new GoalTile(texture, new Vector2(j*64,i*64), base);
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
     * @param index number enemy on in the level
     * @return enemy to be put into an enemy controller
     */
    public void createEnemy(int x, int y, int rotation, AssetDirectory directory, String type, int[] start, int index){
        //TODO: Add different enemy types so that the types actually matter
        // NOTE: THIS ONLY SUPPORTS UP TO TWENTY ENEMIES
        //System.out.println("Enemy test:" + type);
        if(Objects.equals(type, "enemy")){
            type = "Messenger";
        }
        enemies.insert(index-1,new EnemyModel(new Vector2(x,y), rotation, directory, start, EnemyStates.PATROL, type, index));
    }
    public void addWaypoint(int x, int y, int enemyID, int pointNumber){
        int[] coordinates = {(int) Math.floor((double) x /64), (int) Math.floor((double) y /64)};
        enemies.get(enemyID-1).addWaypoint(coordinates,pointNumber);
    }
    /**
     * Creates a bouncy object
     * @param x x coordinate of spawn point
     * @param y y coordinate of spawn point
     * @param rotation initial rotation of player
     * @param directory The directory so that the bouncy can get its animations
     * @param index number bouncy on in the level
     * @param label type of breakable (e.g. glass, wood, etc.)
     * @return enemy to be put into an enemy controller
     */
    public void createBreakable(int x, int y, int rotation, AssetDirectory directory, int index, String label, int base){
        // TODO: Right now only supports one type of breakable.
        TextureRegion glassBox =  new TextureRegion(directory.getEntry("BreakableGlassBox", Texture.class));
        switch (label) {
            case "glass":
                breakables.insert(bouncy.size, new BreakableTile(glassBox, new Vector2(x,y), 1,  base, 1, 4, 4));
                break;
        }
//        extraLayer[y / 64][x / 64] = new BreakableTile(texture, new Vector2(x,y), 1,  base, 1, 4, 4);
    }
    /**
     * Creates a bouncy object
     * @param x x coordinate of spawn point
     * @param y y coordinate of spawn point
     * @param rotation initial rotation of player
     * @param directory The directory so that the bouncy can get its animations
     * @param index number bouncy on in the level
     * @param label type of bouncy (mushroom, etc.)
     * @return enemy to be put into an enemy controller
     */
    public void createBouncy(int x, int y, int rotation, AssetDirectory directory, int index, String label, int base){
        // TODO: Right now only supports one type of bouncy.
        TextureRegion activeBouncyMushroom
                =  new TextureRegion(directory.getEntry("ActiveBouncyMushroom", Texture.class));
        System.out.println("BOUNCE");
        switch (label) {
            case "mushroom":
                System.out.println("MUSHROOM");
                bouncy.insert(bouncy.size, new BouncyTile(activeBouncyMushroom, new Vector2(x,y), 1, base, 1, 8, 8));
                break;
        }
//        extraLayer[y / 64][x / 64] = new BouncyTile(idle, active, new Vector2(x,y), 1, base, 1, 8, 8);
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
    public Array<BouncyTile> getBouncy() { return bouncy;}
    public Array<BreakableTile> getBreakables() { return breakables;}
    public String getName(){return name;}
    public String getNextLevelName(){
        char num = name.charAt(name.length()-1);
        int intValue = Character.getNumericValue(num);
        return "level"+Integer.toString(intValue+1);
    }
    public void setName(String name){this.name = name;}



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
        int intx = (int) Math.floor(x/64)+1;
        int inty = (int) Math.floor(y/64)+1;
        if (!inBounds(intx, inty)) {
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

    public void drawBouncy(BouncyTile object, GameCanvas canvas){
        object.processFilmStrip();
        canvas.draw(object.getFilmStrip(), object.getPosition().x, object.getPosition().y);
    }
    public void drawBreakable(BreakableTile object, GameCanvas canvas){
        object.processFilmStrip();
        canvas.draw(object.getFilmStrip(), object.getPosition().x, object.getPosition().y);
    }
    public void drawTile(TileModel object, GameCanvas canvas){
        canvas.draw(object.getTextureRegion(), object.getPosition().x, object.getPosition().y);
    }

    public void drawDebug(TileModel object, GameCanvas canvas) {
        if (object != null && object.shape != null) {
//            System.out.println(object.shape.getVertexCount());
//            System.out.println(object.getPosition());
//            System.out.println("\n");
//            System.out.println(object.getType());
            canvas.drawPhysics(object.shape, Color.RED, object.getPosition().x + 32, object.getPosition().y + 32, 0, 1, 1);
        }
    }

    public boolean isSwampTile(float x, float y){
        int indexx = (int) Math.floor(x/64);
        int indexy = (int) Math.floor(y/64);
        if (!inBounds(indexx,indexy)){
            return false;
        }
        return extraLayer[indexy][indexx] instanceof SwampTile;
    }
    public boolean isGoalTile(float x, float y){
        int indexx = (int) Math.floor(x/64);
        int indexy = (int) Math.floor(y/64);
        if (!inBounds(indexx,indexy)){
            return false;
        }
        return extraLayer[indexy][indexx] instanceof GoalTile;
    }
    public void removeExtra(float x, float y){
        int indexx = (int) Math.floor(x/64);
        int indexy = (int) Math.floor(y/64);
        extraLayer[indexy][indexx] = null;
    }
}
