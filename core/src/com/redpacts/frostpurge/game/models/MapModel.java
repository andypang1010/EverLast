package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.redpacts.frostpurge.game.views.GameCanvas;
import org.w3c.dom.Text;

public class MapModel {
    /** The dimensions of a single tile */
    private static final int TILE_WIDTH = 64;
    /** Color of a regular tile */
    private static final Color BASIC_COLOR = new Color(1f, 1f, 1f, 1f);
    /** Highlight color for power tiles */
    private static final Color OBSTACLE_COLOR = new Color( 0.0f,  1.0f,  0.5f, 1f);
    /** Highlight color for swamp tiles */
    private static final Color SWAMP_COLOR = new Color( 1f,  1.0f,  0f, 1f);

    /** The texture used for tiles */
    private Texture tile_texture;
    private Texture house_texture;
    private Texture plant_texture;

    // Instance attributes
    /** The map width (in number of tiles) */
    private int width;
    /** The map height (in number of tiles) */
    private int height;
    /** The base tile grid (with above dimensions) */
    private TileModel[][] base;
    /** The extra tile grid (with above dimensions) */
    private TileModel[][] extra;

    /** The items in the level */
    private Array<EnvironmentalObject> objects;

    /**
     * Creates a new empty map of the given size
     *
     * @param width Map width in tiles
     * @param height Map height in tiles
     */
//    public MapModel(int width, int height) {
//        FileHandle fileHandle = Gdx.files.internal("tile.jpg");
//        Pixmap pixmap = new Pixmap(fileHandle);
//        this.tile_texture = new Texture(pixmap);
//        pixmap.dispose();
//
//        this.width = width;
//        this.height = height;
//        tiles = new TileModel[width * height];
//        for (int ii = 0; ii < tiles.length; ii++) {
//            tiles[ii] = new EmptyTile(tile_texture);
//        }
//    }

    /**
     * Creates a new map of the given size with obstacles at specified positions
     *
     * @param width Map width in tiles
     * @param height Map height in tiles
     * @param obstacle_pos Indices of obstacle tiles
     * @param swamp_pos Indices of swamp tiles
     * @param objects An array of environmental objects
     */
    public MapModel(TileModel[][] base, TileModel[][] extra, AssetDirectory directory) {
        tile_texture = new TextureRegion(directory.getEntry( "Tile", Texture.class )).getTexture();
        house_texture = new TextureRegion(directory.getEntry( "House", Texture.class )).getTexture();
        plant_texture = new TextureRegion(directory.getEntry( "Plant", Texture.class )).getTexture();

        this.width = base.length;
        this.height = base[0].length;
        this.base = base;
        this.extra = extra;

    }

    /**
     * Creates a new map of the given size with obstacles and swamps at specified positions
     *
     * @param width Map width in tiles
     * @param height Map height in tiles
     * @param obstacle_pos Indices of obstacle tiles
     * @param swamp_pos Indices of swamp tiles
     */
//    public MapModel(int width, int height, Array<Integer> obstacle_pos, Array<Integer> swamp_pos) {
//        FileHandle fileHandle = Gdx.files.internal("tile.jpg");
//        Pixmap pixmap = new Pixmap(fileHandle);
//        this.tile_texture = new Texture(pixmap);
//        pixmap.dispose();
//
//        this.width = width;
//        this.height = height;
//        tiles = new TileModel[width * height];
//        for (int ii = 0; ii < tiles.length; ii++) {
//
//            if(obstacle_pos.contains(ii, true)){
////                System.out.println("OBSTACLE");
////                System.out.println(getTileCoordinate(ii));
//                tiles[ii] = new ObstacleTile(tile_texture);
//            } else if(swamp_pos.contains(ii, true)){
//                tiles[ii] = new SwampTile(tile_texture);
//            } else{
//                tiles[ii] = new EmptyTile(tile_texture);
//            }
//        }
//    }

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
        if (extra[x][y]==null){
            return base[x][y];
        }else{
            return extra[x][y];
        }
    }

    /**
     * Returns the base tiles
     *
     * @return the tiles array
     */
    public TileModel[][] getBase() {
        return base;
    }
    /**
     * Returns the tiles
     *
     * @return the tiles array
     */
    public TileModel[][] getExtra() {
        return extra;
    }

    /**
     * Returns the number of tiles horizontally across the map.
     *
     * @return the number of tiles horizontally across the map.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the number of tiles vertically across the map.
     *
     * @return the number of tiles vertically across the map.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the size of the tile texture.
     *
     * @return the size of the tile texture.
     */
    public int getTileSize() {
        return TILE_WIDTH;
    }

    /**
     * Returns true if a tile is an obstacle tile.
     *
     * @param x The x value in screen coordinates
     * @param y The y value in screen coordinates
     *
     * @return true if a tile is an obstacle tile
     */
    public boolean isObstacleTileAtScreen(float x, float y) {
        int tx = screenToBoard(x);
        int ty = screenToBoard(y);
        if (!inBounds(tx, ty)) {
            return false;
        }

        return getTileState(tx, ty).getType() == TileModel.TileType.OBSTACLE;
    }

    /**
     * Returns true if a tile is an obstacle tile.
     *
     * @param x The x index for the Tile
     * @param y The y index for the Tile
     *
     * @return true if a tile is an obstacle tile
     */
    public boolean isObstacleTileAt(int x, int y) {
        if (!inBounds(x, y)) {
            return false;
        }

        return getTileState(x, y).getType() == TileModel.TileType.OBSTACLE;
    }

    /**
     * Returns true if a tile is a swamp tile.
     *
     * @param x The x value in screen coordinates
     * @param y The y value in screen coordinates
     *
     * @return true if a tile is a swamp tile
     */
    public boolean isSwampTileAtScreen(float x, float y) {
        int tx = screenToBoard(x);
        int ty = screenToBoard(y);
        if (!inBounds(tx, ty)) {
            return false;
        }

        return getTileState(tx, ty).getType() == TileModel.TileType.SWAMP;
    }

    /**
     * Returns true if a tile is a swamp tile.
     *
     * @param x The x index for the Tile
     * @param y The y index for the Tile
     *
     * @return true if a tile is an swamp tile
     */
    public boolean isSwampTileAt(int x, int y) {
        if (!inBounds(x, y)) {
            return false;
        }

        return getTileState(x, y).getType() == TileModel.TileType.SWAMP;
    }

    /**
     * Removes the power up effect at a swamp tile
     * Does nothing if the given tile is not a swamp
     *
     * @param x The x index for the swamp tile
     * @param y The y index for the swamp tile
     */
    public void removePowerAt(int x, int y){
        if(this.getTileState(x, y).getType() == TileModel.TileType.SWAMP){
            SwampTile swamp = (SwampTile) this.getTileState(x, y);
            swamp.setPowered(false);
        }
    }

    /**
     * Returns the map tile index for a screen position.
     *
     * @param f Screen position coordinate
     *
     * @return the map tile index for a screen position.
     */
    public int screenToBoard(float f) {
        return (int)(f / (getTileSize()));
    }

    /**
     * Returns the map tile index for a screen position.
     *
     * @param pos Screen position coordinate in vector form
     *
     * @return the map tile index for a screen position.
     */
    public Vector2 screenToBoard(Vector2 pos) {
        return new Vector2 ((int)pos.x / getTileSize(), (int)pos.y / getTileSize());
    }

    /**
     * Returns the screen position coordinate for a map tile index(bottom left corner of the tile).
     *
     * @param n Tile index
     *
     * @return the screen position coordinate for a tile index.
     */
    public float boardToScreen(int n) {
        return (float) (n) * (getTileSize());
    }

    /**
     * Returns the screen position coordinate for a map tile index in vector form(bottom left corner of the tile).
     *
     * @param pos Tile index in vector form
     *
     * @return the screen position coordinate for a tile index.
     */
    public Vector2 boardToScreen(Vector2 pos) {
        return new Vector2(pos.x * getTileSize(), pos.y * getTileSize());
    }

    /**
     * Returns the (x, y) coordinates from 1D tiles indices.
     *
     * @param index the 1D index of the tile in the map's array of tiles
     * @return the (x, y) coordinates of tile[i]
     */
    public Vector2 getTileCoordinateIn2D(int index) {
        int x = index / height;
        int y = index % height;
        return new Vector2(boardToScreen(x),boardToScreen(y));
    }

    /**
     * Returns true if the given position is a valid tile
     *
     * @param x The x index for the Tile
     * @param y The y index for the Tile
     *
     * @return true if the given position is a valid tile
     */
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

    /**
     * Draws the map to the given canvas.
     *
     * This method draws all of the tiles in this map. It should be the first drawing
     * pass in the GameEngine.
     *
     * @param canvas the drawing context
     */
    public void draw(GameCanvas canvas) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                drawTile(x, y, canvas);
            }
        }
    }

    /**
     * Draws the individual tile at position (x,y).
     *
     * @param x The x index for the Tile cell
     * @param y The y index for the Tile cell
     */
    private void drawTile(int x, int y, GameCanvas canvas) {
        TileModel tile = getTileState(x, y);

        // Compute drawing coordinates
        float sx = boardToScreen(x);
        float sy = boardToScreen(y);

        float scale = (float) TILE_WIDTH / tile.getTexture().getWidth();

        // Draw
        if (tile.getType() == TileModel.TileType.OBSTACLE) {
            canvas.draw(tile.getTexture(), OBSTACLE_COLOR, 0, 0, sx, sy, 0, scale, scale, false);
        }else if(tile.getType() == TileModel.TileType.SWAMP){
            canvas.draw(tile.getTexture(), SWAMP_COLOR, 0, 0, sx, sy, 0, scale, scale, false);
        }else{
            canvas.draw(tile.getTexture(), BASIC_COLOR, 0, 0, sx, sy, 0, scale, scale, false);
        }
    }
    public void drawTile(TileModel object, GameCanvas canvas){
        if (object.getTextureRegion() == null){
            System.out.println("NULL");
        }
        canvas.draw(object.getTextureRegion(), object.getPosition().x, object.getPosition().y);
    }

    public void drawObject(EnvironmentalObject object, GameCanvas canvas){
        float x = object.getPosition().x;
        float y = object.getPosition().y;
        if(object.getType() == EnvironmentalObject.ObjectType.PLANT){
            canvas.draw(plant_texture, BASIC_COLOR, 0, 0, x, y, 0, 1, 1, false);
        }else if(object.getType() == EnvironmentalObject.ObjectType.HOUSE){
            canvas.draw(house_texture, BASIC_COLOR, 0, 0, x, y, 0, 1, 1, false);
        }
    }
}