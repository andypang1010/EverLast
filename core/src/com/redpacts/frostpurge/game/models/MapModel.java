package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.redpacts.frostpurge.game.views.GameCanvas;

public class MapModel {
    /** The dimensions of a single tile */
    private static final int TILE_WIDTH = 64;
    /** Color of a regular tile */
    private static final Color BASIC_COLOR = new Color(1f, 1f, 1f, 0.5f);
    /** Highlight color for power tiles */
    private static final Color POWER_COLOR = new Color( 0.0f,  1.0f,  0.5f, 0.5f);

    // Instance attributes
    /** The map width (in number of tiles) */
    private int width;
    /** The map height (in number of tiles) */
    private int height;
    /** The tile grid (with above dimensions) */
    private TileModel[] tiles;

    /**
     * Creates a new map of the given size
     *
     * @param width Map width in tiles
     * @param height Map height in tiles
     */
    public MapModel(int width, int height) {
        this.width = width;
        this.height = height;
        tiles = new TileModel[width * height];
        for (int ii = 0; ii < tiles.length; ii++) {
            tiles[ii] = new TileModel();
        }
    }

    /**
     * Returns the tile object for the given position
     *
     * Returns null if that position is out of bounds.
     *
     * @return the tile object for the given position
     */
    private TileModel getTileState(int x, int y) {
        if (!inBounds(x, y)) {
            return null;
        }
        return tiles[x * height + y];
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
     * Returns true if a tile is a power up tile.
     *
     * @param x The x value in screen coordinates
     * @param y The y value in screen coordinates
     *
     * @return true if a tile is a power up tile
     */
    public boolean isPowerTileAtScreen(float x, float y) {
        int tx = screenToBoard(x);
        int ty = screenToBoard(y);
        if (!inBounds(tx, ty)) {
            return false;
        }

        return getTileState(tx, ty).isPowered();
    }

    /**
     * Returns true if a tile is a power up tile.
     *
     * @param x The x index for the Tile
     * @param y The y index for the Tile
     *
     * @return true if a tile is a power up tile
     */
    public boolean isPowerTileAt(int x, int y) {
        if (!inBounds(x, y)) {
            return false;
        }

        return getTileState(x, y).isPowered();
    }

    /**
     * Sets a tile as a power up tile.
     *
     * @param x The x index for the Tile
     * @param y The y index for the Tile
     */
    public void setPower(int x, int y) {
        if (!inBounds(x,y)) {
            Gdx.app.error("Map", "Illegal tile "+x+","+y, new IndexOutOfBoundsException());
            return;
        }
        getTileState(x, y).setPower(true);
    }

    /**
     * Returns true if a tile is a goal tile.
     *
     * @param x The x value in screen coordinates
     * @param y The y value in screen coordinates
     *
     * @return true if a tile is a goal tile
     */
    public boolean isGoalAtScreen(float x, float y) {
        int tx = screenToBoard(x);
        int ty = screenToBoard(y);
        if (!inBounds(tx, ty)) {
            return false;
        }

        return getTileState(tx, ty).isGoal();
    }

    /**
     * Returns true if the tile is a goal.
     *
     * A tile position that is not on the map will always evaluate to false.
     *
     * @param x The x index for the Tile
     * @param y The y index for the Tile
     *
     * @return true if the tile is a goal.
     */
    public boolean isGoal(int x, int y) {
        if (!inBounds(x, y)) {
            return false;
        }

        return getTileState(x, y).isGoal();
    }

    /**
     * Marks a tile as a goal.
     *
     * @param x The x index for the Tile
     * @param y The y index for the Tile
     */
    public void setGoal(int x, int y) {
        if (!inBounds(x,y)) {
            Gdx.app.error("Map", "Illegal tile "+x+","+y, new IndexOutOfBoundsException());
            return;
        }
        getTileState(x, y).setGoal(true);
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
     * Returns the screen position coordinate for a map tile index.
     *
     * @param n Tile index
     *
     * @return the screen position coordinate for a tile index.
     */
    public float boardToScreen(int n) {
        return (float) (n + 0.5f) * (getTileSize());
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

        // Draw
        if (tile.isPowered()) {
            canvas.draw(tile.getTexture(), POWER_COLOR, sx, sy, sx, sy, 0, 1, 1);
        }else{
            canvas.draw(tile.getTexture(), BASIC_COLOR, sx, sy, sx, sy, 0, 1, 1);
        }
    }
}
