package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
//import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.redpacts.frostpurge.game.models.*;
import com.redpacts.frostpurge.game.models.ButtonBox;
import com.redpacts.frostpurge.game.util.EnemyStates;
import com.redpacts.frostpurge.game.util.ScreenListener;
import com.redpacts.frostpurge.game.util.TileGraph;
import com.redpacts.frostpurge.game.views.GameCanvas;

import java.awt.*;
import java.util.Comparator;

public class GameMode implements Screen, InputProcessor {
    /*
    Pause Screen
    */
    private AssetDirectory pauseScreenAssets;
    /**
     * Background texture for pause screen
     */
    private Texture pauseScreenTexture;
    /**
     * Texture for pause button
     */
    private Texture pauseTexture;
    private ButtonBox pauseButton;
    /**
     * Texture for resume button
     */
    private Texture resumeTexture;
    private ButtonBox resumeButton;
    /**
     * Texture for home button
     */
    private Texture homeTexture;
    private ButtonBox homeButton;
    /**
     * Texture for level select button
     */
    private Texture levelSelectTexture;
    private ButtonBox levelSelectButton;
    /**
     * pressState = 0 means no click on button, 1 means clicking on home, 2 means home clicked.
     * 3 means clicking on level select button, 4 means level select clicked.
     * 5 means clicking on resume button, 6 means resume clicked.
     * 7 means clicking on retry, 8 means retry clicked.
     */
    private int pressState = 0;
    private boolean settings;
    private boolean levelSelectScreen;

    /*
    Game Over Screen
     */
    /**
     * Background texture for retry screen
     */
    private Texture retryScreenTexture;
    /**
     * Texture for retry button
     */
    private Texture retryTexture;
    private ButtonBox retryButton;
    /**
     * Background texture for win screen
     */
    private Texture winScreenTexture;

    /*
    Gameplay
     */
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

    public static Array<EnemyController> enemyControllers;

    private Comparator<GameObject> comparator;
    private Array<GameObject> drawble;

    private Array<EnemyModel> enemies;
    private Array<BouncyTile> bouncy;
    private Array<BreakableTile> breakables;
    private Array<ButtonBox> buttons = new Array<>();;

    private TileGraph tileGraph = new TileGraph();

    private Texture statusBarBGTexture;
    private Texture boostBarTexture;
    private Texture healthBarTexture;
    private boolean debug;
    private float scale;
    private float sx;
    private float sy;
    /** Standard window size (for scaling) */
    private static int STANDARD_WIDTH = 1920;
    /** Standard window height (for scaling) */
    private static int STANDARD_HEIGHT = 1080;

    /** Listener that will update the player mode when we are done */
    private ScreenListener listener;
    private BitmapFont font;
    private float maxTime;
    private float currentTime;
    /** Variable to track the game state (SIMPLE FIELDS) */
    private GameState gameState;
    private SaveFileManager saveFileManager;
    private float[] controllerTime;
    public GameMode(GameCanvas canvas) {
        this.canvas = canvas;
        // TODO: Change scale?
        float enlargeScale = 8/7f;
        // Compute the dimensions from the canvas
        this.resize(canvas.getWidth(), canvas.getHeight());
        STANDARD_WIDTH = canvas.getWidth();
        STANDARD_HEIGHT = canvas.getHeight();

        // We need these files loaded immediately
        pauseScreenAssets = new AssetDirectory( "pausescreen.json" );
        pauseScreenAssets.loadAssets();
        pauseScreenAssets.finishLoading();

        // Load the pause screen assets
        pauseScreenTexture = pauseScreenAssets.getEntry("pauseScreen", Texture.class);
        pauseScreenTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        pauseTexture = pauseScreenAssets.getEntry("pauseButton", Texture.class);
        pauseButton = new ButtonBox(0, enlargeScale, scale,
                new Rectangle(canvas.getWidth() * 42 / 100, canvas.getHeight() * 36/100, pauseTexture.getWidth(), pauseTexture.getHeight()), pauseTexture);

        resumeTexture = pauseScreenAssets.getEntry("resumeButton", Texture.class);
        resumeButton = new ButtonBox(1, enlargeScale, scale,
                new Rectangle(canvas.getWidth() * 47 / 100, canvas.getHeight() * 24/100, resumeTexture.getWidth(), resumeTexture.getHeight()), resumeTexture);

        homeTexture = pauseScreenAssets.getEntry("homeButton", Texture.class);
        homeButton = new ButtonBox(2, enlargeScale, scale,
                new Rectangle(canvas.getWidth() * 6 / 100, canvas.getHeight() * 9/100, homeTexture.getWidth(), homeTexture.getHeight()), homeTexture);
        settings = false;

        levelSelectTexture = pauseScreenAssets.getEntry("levelSelectButton", Texture.class);
        levelSelectButton = new ButtonBox(3, enlargeScale, scale,
                new Rectangle(canvas.getWidth() * 18 / 100, canvas.getHeight() * 9/100, levelSelectTexture.getWidth(), levelSelectTexture.getHeight()), levelSelectTexture);
        levelSelectScreen = false;

        // Load the retry screen assets
        retryScreenTexture = pauseScreenAssets.getEntry("retryScreen", Texture.class);
        retryScreenTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        retryTexture = pauseScreenAssets.getEntry("retryButton", Texture.class);
        retryButton = new ButtonBox(4, enlargeScale, scale,
                new Rectangle(canvas.getWidth() * 40 / 100, canvas.getHeight() * 42/100, retryTexture.getWidth(), retryTexture.getHeight()), retryTexture);

        // Load the win screen assets
        winScreenTexture = pauseScreenAssets.getEntry("winScreen", Texture.class);
        winScreenTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // TODO: Add more buttons to scale
        this.buttons.add(pauseButton);
        this.buttons.add(resumeButton);
        this.buttons.add(homeButton);
        this.buttons.add(levelSelectButton);
        this.buttons.add(retryButton);

        this.drawble = new Array<GameObject>();
        this.sx = (float) canvas.getWidth() / STANDARD_WIDTH;
        this.sy = (float) canvas.getHeight() / STANDARD_HEIGHT;
        scale = (Math.min(sx, sy));
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
        controllerTime = new float[]{0};
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

    /**
     * Checks if the player chooses to return to home screen.
     */
    public boolean isSettings() {return this.settings;}


    /**
     * Checks if the player chooses to return to level select screen.
     */
    public boolean isLevelSelectScreen() {return this.levelSelectScreen;}
    /**
     * Reset boolean flags for home screen and level select screen..
     */
    public void resetButton(){
        pressState = 0;
        settings = false;
        levelSelectScreen = false;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {

    }

    @Override
    public void resize(int i, int i1) {
        sx = ((float)canvas.getWidth())/STANDARD_WIDTH;
        sy = ((float)canvas.getHeight())/STANDARD_HEIGHT;
        scale = (sx < sy ? sx : sy);
        for (ButtonBox button : buttons) {
            button.setScreenScale(scale);
        }
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

                if (currentLevel.getExtraLayer()[j][i] == null) {
                    tileGraph.addTile(currentLevel.getBaseLayer()[j][i]);
                    currentTile = currentLevel.getBaseLayer()[j][i];
                }
                else if (currentLevel.getExtraLayer()[j][i].getType() != TileModel.TileType.OBSTACLE) {
                    tileGraph.addTile(currentLevel.getExtraLayer()[j][i]);
                    currentTile = currentLevel.getExtraLayer()[j][i];
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
                            if (currentLevel.getExtraLayer()[y][x] == null) {
                                tileGraph.connectTiles(currentTile, currentLevel.getBaseLayer()[y][x]);
                            } else if (currentLevel.getExtraLayer()[y][x].getType() != TileModel.TileType.OBSTACLE) {
                                tileGraph.connectTiles(currentTile, currentLevel.getExtraLayer()[y][x]);
                            }
                        }
                    }
                }
            }
        }
    }

    public void update(float delta) {
        controllerTime[0] += Gdx.graphics.getDeltaTime();
        Gdx.input.setInputProcessor(this);
        buttonDown(inputController.xbox, 0);
        buttonUp(inputController.xbox, 0);
        for(ButtonBox button : buttons) {
            button.setScreenScale(scale);
        }

        inputController.readInput(null,null);
        if (inputController.didDebug()){
            playerModel.setWin(true);
        }
        // Handle pausing the game
        if (pressState == 2) { // Home button selected
            pressState = 0;
            levelSelectScreen = false;
            settings = true;
            listener.exitScreen(this, 0);
        } else if (pressState == 4) { // Level select button selected
            pressState = 0;
            levelSelectScreen = true;
            settings = false;
            listener.exitScreen(this, 0);
        } else if (inputController.didPause() || pressState == 6) {
            if (gameState == GameState.PLAY) {
                gameState = GameState.PAUSE;
                pauseButton.resize("up");
                resumeButton.resize("down");
                retryButton.resize("down");
                levelSelectButton.resize("down");
                homeButton.resize("down");
            } else if (gameState == GameState.PAUSE && !inputController.didPause()) {
                pressState = 0;
                gameState = GameState.PLAY;
            }
            inputController.clearPausePressed();
        } else if (pressState == 8){
            pressState = 0;
            gameState = GameState.PLAY;
            loadLevel(currentLevel.getName(), saveFileManager);
        }

        if (gameState == GameState.PAUSE) {
            // Draw pause screen
            drawPauseScreen();
            return; // Skip the rest of the update loop
        }

        if (gameState == GameState.OVER){
            drawRetryScreen();
            return; // Skip the rest of the update loop
        } else if (gameState == GameState.WIN){
            drawWinScreen();
            return;
        }

        if (gameState == GameState.PLAY){
            currentTime -= Gdx.graphics.getDeltaTime();
            playerModel.addHp(-100 * Gdx.graphics.getDeltaTime() / maxTime);
        }

        if (currentTime <= 0) {
            gameState = GameState.OVER;
            pauseButton.resize("down");
            resumeButton.resize("down");
            retryButton.resize("up");
            levelSelectButton.resize("down");
            homeButton.resize("down");
        }
        if (!playerModel.isAlive()){
            gameState = GameState.OVER;
            pauseButton.resize("down");
            resumeButton.resize("down");
            retryButton.resize("up");
            levelSelectButton.resize("down");
            homeButton.resize("down");
        }
        if (playerModel.didwin()){
            gameState = GameState.WIN;
            playerModel.setWin(false);
            saveFileManager.saveGame(currentLevel.getName(),true, true, (int)(currentTime*100));
            saveFileManager.saveGame(currentLevel.getNextLevelName(),true, false, 0);
            pauseButton.resize("down");
            resumeButton.resize("down");
            retryButton.resize("down");
            levelSelectButton.resize("up");
            homeButton.resize("down");
        }

        drawble.clear();
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
        drawble.addAll(bouncy);
        drawble.addAll(breakables);
        sort_by_y(drawble);
        drawble.reverse();

        // Toggle debug mode
        if (inputController.didDebug()) {
            debug = !debug;
        }

        if (gameState == GameState.PLAY){
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
//        canvas.center(camera, (float) (playerModel.getPosition().x+Math.random()*10), (float) (playerModel.getPosition().y+Math.random()*10));
        canvas.center(camera, playerModel.getPosition().x, playerModel.getPosition().y);
        camera.zoom = 1/scale;
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
            } else if(object instanceof EnemyModel){
                enemyControllers.get(i).draw(canvas,(EnemyModel) object);
                i++;
            } else if (object instanceof ObstacleTile || object instanceof EmptyTile || object instanceof GoalTile || object instanceof SwampTile){
                currentLevel.drawTile((TileModel) object, canvas);
            } else if (object instanceof BreakableTile) {
                currentLevel.drawBreakable((BreakableTile) object, canvas);
            } else if (object instanceof BouncyTile) {
                currentLevel.drawBouncy((BouncyTile) object, canvas);
            }
        }

        canvas.end();

        if (debug) {
//            System.out.println(delta);
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

        canvas.drawUI(statusBarBGTexture,Color.WHITE, 50*sx, -800*sy, 0f, 1.2f*scale,1.2f*scale, HUDcamera);
        if(playerModel.getBoostNum() >= 1){
            canvas.drawUI(boostBarTexture,Color.WHITE, 50*sx, -800*sy, 0, 1.2f*scale,1.2f*scale, HUDcamera);
        }
        if(playerModel.getBoostNum() >= 2){
            canvas.drawUI(boostBarTexture,Color.WHITE, 150*sx, -800*sy, 0, 1.2f*scale,1.2f*scale, HUDcamera);
        }
        if(playerModel.getBoostNum() >= 3){
            canvas.drawUI(boostBarTexture,Color.WHITE, 250*sx, -800*sy, 0, 1.2f*scale,1.2f*scale, HUDcamera);
        }
        if(playerModel.getBoostNum() >= 4){
            canvas.drawUI(boostBarTexture,Color.WHITE, 350*sx, -800*sy, 0, 1.2f*scale,1.2f*scale, HUDcamera);
        }
        canvas.drawUI(healthBarTexture, Color.WHITE, (50+(1.932f)*(100-playerModel.getHp()))*sx, -800*sy, 0, 1.2f*playerModel.getHp()/100*scale, 1.2f*scale, HUDcamera);
        font.getData().setScale(1);
        font.setColor(Color.GRAY);
//        canvas.drawTextHUD("Time: " + (int) currentTime, font, 1500, 1000, HUDcamera);
    }

    public void drawPauseScreen(){
        canvas.begin();
        canvas.drawBackground(pauseScreenTexture, 0, 0, true);
        Rectangle bounds;
        pauseButton.hoveringButton(inputController.xbox, controllerTime, pauseButton,resumeButton,levelSelectButton,homeButton,retryButton ,gameState);
        bounds = pauseButton.getBounds();
        canvas.draw(pauseButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

        resumeButton.hoveringButton(inputController.xbox, controllerTime, pauseButton,resumeButton,levelSelectButton,homeButton,retryButton,gameState);
        bounds = resumeButton.getBounds();
        canvas.draw(resumeButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

        homeButton.hoveringButton(inputController.xbox, controllerTime, pauseButton,resumeButton,levelSelectButton,homeButton,retryButton,gameState);
        bounds = homeButton.getBounds();
        canvas.draw(homeButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

        levelSelectButton.hoveringButton(inputController.xbox, controllerTime, pauseButton,resumeButton,levelSelectButton,homeButton,retryButton,gameState);
        bounds = levelSelectButton.getBounds();
        canvas.draw(levelSelectButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

        canvas.end();
    }

    public void drawRetryScreen(){
        canvas.begin();
        canvas.drawBackground(retryScreenTexture, 0, 0, true);
        Rectangle bounds;

        retryButton.hoveringButton(inputController.xbox , controllerTime, pauseButton,resumeButton,levelSelectButton,homeButton,retryButton,gameState);
        bounds = retryButton.getBounds();
        canvas.draw(retryButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

        homeButton.hoveringButton(inputController.xbox, controllerTime, pauseButton,resumeButton,levelSelectButton,homeButton,retryButton,gameState);
        bounds = homeButton.getBounds();
        canvas.draw(homeButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

        levelSelectButton.hoveringButton(inputController.xbox , controllerTime, pauseButton,resumeButton,levelSelectButton,homeButton,retryButton,gameState);
        bounds = levelSelectButton.getBounds();
        canvas.draw(levelSelectButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

        canvas.end();
    }

    public void drawWinScreen(){
        canvas.begin();
        canvas.drawBackground(winScreenTexture, 0, 0, true);
        Rectangle bounds;

        homeButton.hoveringButton(inputController.xbox, controllerTime, pauseButton,resumeButton,levelSelectButton,homeButton,retryButton,gameState);
        bounds = homeButton.getBounds();
        canvas.draw(homeButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

        levelSelectButton.hoveringButton(inputController.xbox, controllerTime, pauseButton,resumeButton,levelSelectButton,homeButton,retryButton,gameState);
        bounds = levelSelectButton.getBounds();
        canvas.draw(levelSelectButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

        canvas.end();
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
        levelController = new LevelController();


        // Create the controllers.

        statusBarBGTexture = new TextureRegion(directory.getEntry("StatusBar_BG", Texture.class)).getTexture();
        boostBarTexture = new TextureRegion(directory.getEntry("StatusBar_Boost", Texture.class)).getTexture();
        healthBarTexture = new TextureRegion(directory.getEntry("StatusBar_Health", Texture.class)).getTexture();

        inputController = new InputController();

    }

    public void loadLevel(String level, SaveFileManager savefile){
        saveFileManager = savefile;
        gameState = GameState.PLAY;
        JsonValue leveljson = directory.getEntry(level, JsonValue.class);
        currentLevel = levelController.initializeLevel(leveljson, tilesetjson,tileset,tileset[0].length,tileset.length, directory);
        enemies = currentLevel.getEnemies();
        playerModel = currentLevel.getPlayer();
        bouncy = currentLevel.getBouncy();
        breakables = currentLevel.getBreakables();
        switch (level){
            case "level1":
                maxTime = 46;
                break;
            case "level2":
                maxTime = 61;
                break;
            case "level3":
                maxTime = 46;
                break;
            default:
                maxTime = 61;
        }

        currentTime = maxTime;
        currentLevel.setName(level);

        populateTileGraph();

        playerController = new PlayerController(playerModel);


        enemyControllers = new Array<>();
        for (int i = 0; i < enemies.size; i++){
//            for (int j = 0; j<)
//            enemies.get(i).getWaypoints()
            enemyControllers.add(new EnemyController(enemies.get(i), playerModel, EnemyStates.PATROL,tileGraph,currentLevel,enemies.get(i).getWaypoints()));
        }
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        System.out.println(camera.zoom);
//        System.out.println(camera.zoom);
//        System.out.println(scale);
        HUDcamera = new OrthographicCamera();
        HUDcamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        collisionController = new CollisionController(currentLevel, playerModel, enemies, bouncy, breakables,canvas.getWidth(), canvas.getHeight());
    }

    // PROCESSING PLAYER INPUT

    /**
     * Called when the screen was touched or a mouse button was pressed.
     * <p>
     * This method checks to see if the play button is available and if the click
     * is in the bounds of the play button.  If so, it signals the that the button
     * has been pressed and is currently down. Any mouse button is accepted.
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the button or touch finger number
     * @return whether to hand the event to other listeners.
     */
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (pressState % 2 == 0 && pressState != 0) {
            return true;
        }
        if (gameState == GameState.PAUSE) {
             if (homeButton.isPressed()) {
                pressState = 1;
             } else if (levelSelectButton.isPressed()){
                 pressState = 3;
             } else if (resumeButton.isPressed() || pauseButton.isPressed()) {
                 pressState = 5;
             }
        } else if (gameState == GameState.OVER) {
            if (homeButton.isPressed()) {
                pressState = 1;
            } else if (levelSelectButton.isPressed()){
                pressState = 3;
            } else if (retryButton.isPressed()) {
                pressState = 7;
            }
        } else if (gameState == GameState.WIN) {
            if (homeButton.isPressed()) {
                pressState = 1;
            } else if (levelSelectButton.isPressed()){
                pressState = 3;
            }
        }
        return false;
    }

    /**
     * Called when a finger was lifted or a mouse button was released.
     * <p>
     * This method checks to see if the play button is currently pressed down. If so,
     * it signals the that the player is ready to go.
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the button or touch finger number
     * @return whether to hand the event to other listeners.
     */
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pressState % 2 == 1) {
            pressState++;
            return false;
        }
        return true;
    }

    /**
     * Called when a button on the Controller was pressed.
     * <p>
     * The buttonCode is controller specific. This listener only supports the start
     * button on an X-Box controller.  This outcome of this method is identical to
     * pressing (but not releasing) the play button.
     *
     * @param controller The game controller
     * @param buttonCode The button pressed
     * @return whether to hand the event to other listeners.
     */
    public boolean buttonDown(Controller controller, int buttonCode) {
        // TODO: Support XBox
        if (inputController.xbox != null) {
            if (pressState % 2 == 0 && pressState != 0) {
                return true;
            }
            if (gameState == GameState.PAUSE && inputController.xbox.getBack()) {
                if (homeButton.getEnlarged()) {
                    pressState = 1;
                } else if (levelSelectButton.getEnlarged()) {
                    pressState = 3;
                } else if (resumeButton.getEnlarged() || pauseButton.getEnlarged()) {
                    pressState = 5;
                }
            } else if (gameState == GameState.OVER && inputController.xbox.getBack()) {
                if (homeButton.getEnlarged()) {
                    pressState = 1;
                } else if (levelSelectButton.getEnlarged()) {
                    pressState = 3;
                } else if (retryButton.getEnlarged()) {
                    pressState = 7;
                }
            } else if (gameState == GameState.WIN && inputController.xbox.getBack()) {
                if (homeButton.getEnlarged()) {
                    pressState = 1;
                } else if (levelSelectButton.getEnlarged()) {
                    pressState = 3;
                }
            }
        }
        return false;
    }

    /**
     * Called when a button on the Controller was released.
     * <p>
     * The buttonCode is controller specific. This listener only supports the start
     * button on an X-Box controller.  This outcome of this method is identical to
     * releasing the the play button after pressing it.
     *
     * @param controller The game controller
     * @param buttonCode The button pressed
     * @return whether to hand the event to other listeners.
     */
    public boolean buttonUp(Controller controller, int buttonCode) {
        if (pressState %2 ==1) {
			if (inputController.xbox != null) {
				pressState +=1;
				return false;
			}
		}
        return true;
    }

    // UNSUPPORTED METHODS FROM InputProcessor

    /**
     * Called when a key is pressed (UNSUPPORTED)
     *
     * @param keycode the key pressed
     * @return whether to hand the event to other listeners.
     */
    public boolean keyDown(int keycode) {
        return true;
    }

    /**
     * Called when a key is typed (UNSUPPORTED)
     *
     * @return whether to hand the event to other listeners.
     */
    public boolean keyTyped(char character) {
        return true;
    }

    /**
     * Called when a key is released (UNSUPPORTED)
     *
     * @param keycode the key released
     * @return whether to hand the event to other listeners.
     */
    public boolean keyUp(int keycode) {
        return true;
    }

    /**
     * Called when the mouse was moved without any buttons being pressed. (UNSUPPORTED)
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @return whether to hand the event to other listeners.
     */
    public boolean mouseMoved(int screenX, int screenY) {
        return true;
    }

    /**
     * Called when the mouse wheel was scrolled. (UNSUPPORTED)
     *
     * @param dx the amount of horizontal scroll
     * @param dy the amount of vertical scroll
     * @return whether to hand the event to other listeners.
     */
    public boolean scrolled(float dx, float dy) {
        return true;
    }

    /**
     * Called when the touch gesture is cancelled (UNSUPPORTED)
     * <p>
     * Reason may be from OS interruption to touch becoming a large surface such
     * as the user cheek. Relevant on Android and iOS only. The button parameter
     * will be Input.Buttons.LEFT on iOS.
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the button or touch finger number
     * @param button  the button
     * @return whether to hand the event to other listeners.
     */
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return true;
    }

    /**
     * Called when the mouse or finger was dragged. (UNSUPPORTED)
     *
     * @param screenX the x-coordinate of the mouse on the screen
     * @param screenY the y-coordinate of the mouse on the screen
     * @param pointer the button or touch finger number
     * @return whether to hand the event to other listeners.
     */
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }

    // UNSUPPORTED METHODS FROM ControllerListener

    /**
     * Called when a controller is connected. (UNSUPPORTED)
     *
     * @param controller The game controller
     */
    public void connected(Controller controller) {
    }

    /**
     * Called when a controller is disconnected. (UNSUPPORTED)
     *
     * @param controller The game controller
     */
    public void disconnected(Controller controller) {
    }

    /**
     * Called when an axis on the Controller moved. (UNSUPPORTED)
     * <p>
     * The axisCode is controller specific. The axis value is in the range [-1, 1].
     *
     * @param controller The game controller
     * @param axisCode   The axis moved
     * @param value      The axis value, -1 to 1
     * @return whether to hand the event to other listeners.
     */
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return true;
    }

    public enum GameState {
        /** While we are playing the game */
        PLAY,
        /** When pausing the game */
        PAUSE,
        /** When the ships is dead (but shells still work) */
        OVER,
        WIN
    }
}
