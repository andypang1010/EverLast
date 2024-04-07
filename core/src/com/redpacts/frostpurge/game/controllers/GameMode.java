package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
//import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.redpacts.frostpurge.game.models.*;
import com.redpacts.frostpurge.game.util.EnemyStates;
import com.redpacts.frostpurge.game.util.ScreenListener;
import com.redpacts.frostpurge.game.util.TileGraph;
import com.redpacts.frostpurge.game.views.GameCanvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GameMode implements Screen {
    private GameCanvas canvas;

    private OrthographicCamera camera;
    private OrthographicCamera HUDcamera;
    private AssetDirectory directory;
    /** Reads input from keyboard or game pad (CONTROLLER CLASS) */
    private InputController inputController;
    /** Handle collision and physics (CONTROLLER CLASS) */
    private CollisionController collisionController;

    /** Whether or not this player mode is still active */
    private boolean active;
    /** Board for the game*/
    private LevelModel currentLevel;
    /** Environmental objects for the game*/
    private Array<EnvironmentalObject> envObjects = new Array<EnvironmentalObject>();
    /** Player for the game*/
    private PlayerModel playerModel;

    /** Player for the game*/
    private PlayerController playerController;

    private Array<EnemyController> enemyControllers;

    private Array<EnemyModel> enemies;

    private TileGraph tileGraph = new TileGraph();

    private Texture statusBarBGTexture;
    private Texture statusBarTexture;
    private TileModel[][] baseLayer;
    private TileModel[][] extraLayer;

    private boolean debug;

    /** Listener that will update the player mode when we are done */
    private ScreenListener listener;

    /** Variable to track the game state (SIMPLE FIELDS) */
    private GameState gameState;
    public GameMode(GameCanvas canvas) {
        this.canvas = canvas;
        active = false;
        // Null out all pointers, 0 out all ints, etc.
    }
    /**
     * Returns true if debug mode is active.
     *
     * If true, all objects will display their physics bodies.
     *
     * @return true if debug mode is active.
     */
    public boolean isDebug( ) {
        return debug;
    }

    /**
     * Sets whether debug mode is active.
     *
     * If true, all objects will display their physics bodies.
     *
     * @param value whether debug mode is active.
     */
    public void setDebug(boolean value) {
        debug = value;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {

    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public void sort_by_y(Array<GameObject> obj_list) {
        Comparator<GameObject> comparator = new Comparator<GameObject>() {
            public int compare(GameObject o1, GameObject o2) {
                float o1y;
                float o2y;
                if (o1 instanceof TileModel){
                    o1y = o1.getPositionY() + (((TileModel) o1).base-2) *64;
                } else{
                    o1y = o1.getPositionY();
                }
                if (o2 instanceof TileModel){
                    o2y = o2.getPositionY() + (((TileModel) o2).base-2) *64;
                } else{
                    o2y = o2.getPositionY();
                }
                float diff = o1y - o2y;
                if(diff > 0){
                    return 1;
                }else if(diff == 0){
                    return 0;
                }else{
                    return -1;
                }
            }
        };
        obj_list.sort(comparator);
    }

    private void populateTileGraph() {
        for (int i = 0; i < currentLevel.getWidth(); i++) {
            for (int j = 0; j < currentLevel.getHeight(); j++) {
                TileModel currentTile = null;

                if (currentLevel.getExtraLayer()[i][j] == null) {
                    tileGraph.addTile(currentLevel.getBaseLayer()[i][j]);
                    currentTile = currentLevel.getBaseLayer()[i][j];
                }
                else if (currentLevel.getExtraLayer()[i][j].getType() == TileModel.TileType.SWAMP) {
                    tileGraph.addTile(currentLevel.getExtraLayer()[i][j]);
                    currentTile = currentLevel.getExtraLayer()[i][j];
                }

                else {
                    continue;
                }

                for(int x = i - 1; x <= i + 1; x++) {
                    for(int y = j-1; y <= j+1; y++) {
                        if (i == x && j == y) {
                            continue;
                        }
                        if (currentLevel.inBounds(x, y)) {
                            if (currentLevel.getExtraLayer()[x][y] == null) {
                                tileGraph.connectTiles(currentTile, currentLevel.getBaseLayer()[x][y]);
                            } else if (currentLevel.getExtraLayer()[x][y].getType() == TileModel.TileType.SWAMP) {
                                tileGraph.connectTiles(currentTile, currentLevel.getExtraLayer()[x][y]);
                            }
                        }
                    }
                }
            }
        }
    }

    public void update(float delta) {
        Array<GameObject> drawble = new Array<GameObject>();
        for (int i = 0; i<currentLevel.getHeight();i++){
            for (int j = 0; j<currentLevel.getWidth();j++){
                if (currentLevel.getAccentLayer()[i][j]!=null){
                    drawble.add(currentLevel.getAccentLayer()[i][j]);
                }
                if (currentLevel.getExtraLayer()[i][j]!=null){
                    drawble.add(currentLevel.getExtraLayer()[i][j]);
                }
            }
        }

        drawble.add(playerModel);
        drawble.addAll(enemies);
        sort_by_y(drawble);
        drawble.reverse();

        inputController.readInput(null,null);

        // Toggle debug mode
        if (inputController.didDebug()) {
            debug = !debug;
        }

        playerController.update(inputController.getHorizontal(), inputController.getVertical(), inputController.didDecelerate(), inputController.didBoost(), inputController.didVacuum());
        for (EnemyController enemyController : enemyControllers) {
            enemyController.update();
        }
        collisionController.update();

        Gdx.gl.glClearColor(0.39f, 0.58f, 0.93f, 1.0f);  // Homage to the XNA years
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        canvas.begin();
//        System.out.println("Offset: " + playerController.cameraOffset(playerController.getModel().getVelocity().x));
        canvas.center(camera, playerController.getModel().getPosition().x + playerController.cameraOffset(playerController.getModel().getVelocity().x), playerController.getModel().getPosition().y + playerController.cameraOffset(playerController.getModel().getVelocity().y));
//        board.draw(canvas);
//        playerController.draw(canvas, inputController.getHorizontal(), inputController.getVertical());
//        enemyController.draw(canvas);
        for (int i = 0; i<currentLevel.getHeight();i++){
            for (int j = 0; j<currentLevel.getWidth();j++){
                currentLevel.drawTile(currentLevel.getBaseLayer()[i][j],canvas);
            }
        }
        int i = 0;
        for(GameObject object: drawble){
            if(object instanceof PlayerModel){
                playerController.draw(canvas, inputController.getHorizontal(), inputController.getVertical());
            }else if(object instanceof EnemyModel){
                enemyControllers.get(i).draw(canvas,(EnemyModel) object);
                i++;
            }else if (object instanceof TileModel){
                currentLevel.drawTile((TileModel) object, canvas);
            }
        }
        canvas.end();

        if (debug) {
            System.out.println(delta);
            canvas.beginDebug();
            for (int ii = 0; ii < currentLevel.getHeight(); ii++){
                for (int jj = 0; jj < currentLevel.getWidth(); jj++){
                    currentLevel.drawDebug(currentLevel.getExtraLayer()[ii][jj], canvas);
                }
            }

            for (EnemyModel enemy : enemies) {
                enemy.drawDebug(canvas);
            }
            playerModel.drawDebug(canvas);
            canvas.endDebug();
        }

        canvas.drawUI(statusBarBGTexture,Color.WHITE, -100, -1400, 0, .5f,.5f, HUDcamera);
        if (playerController.hasResources()){
            canvas.drawUI(statusBarTexture,Color.WHITE, -100, -1400, 0, .5f,.5f, HUDcamera);
            canvas.drawUI(statusBarTexture,Color.WHITE, 250, -1400, 0, .5f,.5f, HUDcamera);
            canvas.drawUI(statusBarTexture,Color.WHITE, 300, -1400, 0, .5f,.5f, HUDcamera);
        }
    }

    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    public void populate(AssetDirectory directory){
        directory.finishLoading();
        this.directory = directory;
        gameState = GameState.INTRO;

        int tilewidth = 64;
        int tileheight = 64;
        JsonValue leveljson = directory.getEntry("playgroundlevel", JsonValue.class);
        JsonValue tilesetjson = directory.getEntry("tileset", JsonValue.class);
        TextureRegion tilesetregion = new TextureRegion(directory.getEntry("tileset",Texture.class));
        TextureRegion[][] tileset = tilesetregion.split(tilewidth, tileheight);
        TextureRegion white = new TextureRegion(directory.getEntry("baselayer",Texture.class));
        TextureRegion[][] whitetile = white.split(tilewidth, tileheight);
        LevelController levelController = new LevelController();

        LevelModel level1 = levelController.initializeLevel(leveljson, tilesetjson,tileset,tileset[0].length,tileset.length, directory, whitetile);
        enemies = level1.getEnemies();
        baseLayer = level1.getBaseLayer();
        extraLayer = level1.getExtraLayer();
        playerModel = level1.getPlayer();


        // Create the controllers.

        statusBarBGTexture = new TextureRegion(directory.getEntry("StatusBar_BG", Texture.class)).getTexture();
        statusBarTexture = new TextureRegion(directory.getEntry("StatusBar_Bar", Texture.class)).getTexture();

        inputController = new InputController();

        currentLevel = level1;

        populateTileGraph();

//        playerModel = new PlayerModel(new Vector2(100,100),0, directory);
        playerController = new PlayerController(playerModel);

        //EnemyModel enemy = new EnemyModel(new Vector2(600, 300), 90, directory);
        //enemyController = new EnemyController(enemy, playerModel, board.getTileState(0, 7), board.getTileState(4, 7), EnemyStates.PATROL, tileGraph, board);
        enemyControllers = new Array<>();
        for (int i = 0; i < enemies.size; i++){
            enemyControllers.add(new EnemyController(enemies.get(i), playerModel, EnemyStates.PATROL,tileGraph,currentLevel));
        }
//        enemies.add(enemy);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        HUDcamera = new OrthographicCamera();
        HUDcamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // YOU WILL NEED TO MODIFY THIS NEXT LINE
        collisionController = new CollisionController(level1, playerModel, enemies, canvas.getWidth(), canvas.getHeight());

        //Testing
    }

    public enum GameState {
        /** Before the game has started */
        INTRO,
        /** While we are playing the game */
        PLAY,
        /** When the ships is dead (but shells still work) */
        OVER
    }

}
