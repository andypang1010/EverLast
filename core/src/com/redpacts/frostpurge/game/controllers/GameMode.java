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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.redpacts.frostpurge.game.models.*;
import com.redpacts.frostpurge.game.util.EnemyStates;
import com.redpacts.frostpurge.game.util.ScreenListener;
import com.redpacts.frostpurge.game.util.TileGraph;
import com.redpacts.frostpurge.game.views.GameCanvas;

import java.sql.Time;
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
    private LevelController levelController;
    private JsonValue tilesetjson;
    private TextureRegion[][] tileset;
    private TextureRegion[][] whitetile; //TO BE REMOVED

    /** Board for the game*/
    private LevelModel currentLevel;
    /** Player for the game*/
    private PlayerModel playerModel;

    /** Player for the game*/
    private PlayerController playerController;

    private Array<EnemyController> enemyControllers;

    private Comparator<GameObject> comparator;

    private Array<EnemyModel> enemies;

    private TileGraph tileGraph = new TileGraph();

    private Texture statusBarBGTexture;
    private Texture statusBarTexture;
    private boolean debug;

    /** Listener that will update the player mode when we are done */
    private ScreenListener listener;
    private BitmapFont font;
    private float currentTime;
    /** Variable to track the game state (SIMPLE FIELDS) */
    private GameState gameState;
    public GameMode(GameCanvas canvas) {
        this.canvas = canvas;
        this.comparator = new Comparator<GameObject>() {
            public int compare(GameObject o1, GameObject o2) {
                float o1y;
                float o2y;
                if (o1 instanceof TileModel){
                    o1y = o1.getPositionY() + (((TileModel) o1).base-3) *64;
                } else{
                    o1y = o1.getPositionY();
                }
                if (o2 instanceof TileModel){
                    o2y = o2.getPositionY() + (((TileModel) o2).base-3) *64;
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
        if (gameState == GameState.INTRO){
            currentTime -= Gdx.graphics.getDeltaTime();
        }

        if (currentTime <= 0) {
            gameState = GameState.OVER;
        }
        if (!playerModel.isAlive()){
            gameState = GameState.OVER;
        }
        if (playerModel.getPosition().x > 1700 && playerModel.getPosition().x < 2000 && playerModel.getPosition().y > 2500 && playerModel.getPosition().y < 2800){
            gameState = GameState.WIN;
        }
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

        if (gameState == GameState.WIN || gameState == GameState.OVER){
            if (inputController.didExit()){
                listener.exitScreen(this, 0);
            }
            if (inputController.didReplay()){
                loadLevel(currentLevel.getName());
            }
        }

        if (gameState == GameState.INTRO){
            playerController.update(inputController.getHorizontal(), inputController.getVertical(), inputController.didDecelerate(), inputController.didBoost(), inputController.didVacuum());
            for (EnemyController enemyController : enemyControllers) {
                enemyController.update();
            }
            collisionController.update();
        }

        Gdx.gl.glClearColor(1f, 1f, 1f, 1.0f);  // Homage to the XNA years
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        canvas.begin();

        //Vector2 cameraPos = playerController.cameraOffsetPos();
        canvas.center(camera, playerModel.getPosition().x, playerModel.getPosition().y);
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
        if(playerModel.getBoostNum() >= 1){
            canvas.drawUI(statusBarTexture,Color.WHITE, -100, -1400, 0, .5f,.5f, HUDcamera);
        }
        if(playerModel.getBoostNum() >= 2){
            canvas.drawUI(statusBarTexture,Color.WHITE, 250, -1400, 0, .5f,.5f, HUDcamera);
        }
        if(playerModel.getBoostNum() >= 3){
            canvas.drawUI(statusBarTexture,Color.WHITE, 300, -1400, 0, .5f,.5f, HUDcamera);
        }
        font.getData().setScale(1);
        font.setColor(Color.GRAY);
        canvas.drawTextHUD("Time: " + (int) currentTime, font, 1500, 1000, HUDcamera);
        if (gameState == GameState.OVER){
            font.setColor(Color.RED);
            canvas.drawTextCenteredHUD("GAME OVER!", font, 0, HUDcamera);
        }
        if (gameState == GameState.WIN){
            font.setColor(Color.GREEN);
            canvas.drawTextCenteredHUD("YOU WIN!", font, 0, HUDcamera);
        }
    }

    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    public void populate(AssetDirectory directory){
        directory.finishLoading();
        this.directory = directory;

        font = directory.getEntry("font", BitmapFont.class);


        int tilewidth = 64;
        int tileheight = 64;
        tilesetjson = directory.getEntry("tileset", JsonValue.class);
        TextureRegion tilesetregion = new TextureRegion(directory.getEntry("tileset",Texture.class));
        tileset = tilesetregion.split(tilewidth, tileheight);
        TextureRegion white = new TextureRegion(directory.getEntry("baselayer",Texture.class));
        whitetile = white.split(tilewidth, tileheight);
        levelController = new LevelController();


        // Create the controllers.

        statusBarBGTexture = new TextureRegion(directory.getEntry("StatusBar_BG", Texture.class)).getTexture();
        statusBarTexture = new TextureRegion(directory.getEntry("StatusBar_Bar", Texture.class)).getTexture();

        inputController = new InputController();

    }

    public void loadLevel(String level){
        gameState = GameState.INTRO;
        JsonValue leveljson = directory.getEntry(level, JsonValue.class);
        currentLevel = levelController.initializeLevel(leveljson, tilesetjson,tileset,tileset[0].length,tileset.length, directory, whitetile);
        enemies = currentLevel.getEnemies();
        playerModel = currentLevel.getPlayer();
        currentTime = 46;
        currentLevel.setName(level);

        populateTileGraph();

        playerController = new PlayerController(playerModel);


        enemyControllers = new Array<>();
        for (int i = 0; i < enemies.size; i++){
            enemyControllers.add(new EnemyController(enemies.get(i), playerModel, EnemyStates.PATROL,tileGraph,currentLevel));
        }
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        HUDcamera = new OrthographicCamera();
        HUDcamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        collisionController = new CollisionController(currentLevel, playerModel, enemies, canvas.getWidth(), canvas.getHeight());
    }

    public enum GameState {
        /** Before the game has started */
        INTRO,
        /** While we are playing the game */
        PLAY,
        /** When the ships is dead (but shells still work) */
        OVER,
        WIN
    }

}
