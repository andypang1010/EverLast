package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
//import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.redpacts.frostpurge.game.models.EmptyTile;
import com.redpacts.frostpurge.game.models.enemyModel;
import com.redpacts.frostpurge.game.models.MapModel;
import com.redpacts.frostpurge.game.models.PlayerModel;
import com.redpacts.frostpurge.game.util.EnemyStates;
import com.redpacts.frostpurge.game.util.ScreenListener;
import com.redpacts.frostpurge.game.views.GameCanvas;

public class GameMode implements Screen {
    private GameCanvas canvas;

    private OrthographicCamera camera;
    private AssetDirectory directory;
    /** Reads input from keyboard or game pad (CONTROLLER CLASS) */
    private InputController inputController;
    /** Handle collision and physics (CONTROLLER CLASS) */
    private CollisionController collisionController;
    /** Constructs the game models and handle basic gameplay (CONTROLLER CLASS) */
    private GameplayController gameplayController;
    /** Whether or not this player mode is still active */
    private boolean active;
    /** Board for the game*/
    private MapModel board;
    /** Player for the game*/
    private PlayerModel playerModel;

    /** Player for the game*/
    private PlayerController playerController;

    private EnemyController enemyController;

    private Array<enemyModel> enemies;

    /** Listener that will update the player mode when we are done */
    private ScreenListener listener;

    /** Variable to track the game state (SIMPLE FIELDS) */
    private GameState gameState;
    public GameMode(GameCanvas canvas) {
        this.canvas = canvas;
        active = false;
        // Null out all pointers, 0 out all ints, etc.
        gameState = GameState.INTRO;

        directory = new AssetDirectory("assets.json");
        directory.loadAssets();
        directory.finishLoading();
        enemies = new Array<enemyModel>();
        // Create the controllers.

        Array<Integer> obstacles = new Array<Integer>();// Obstacle locations
        obstacles.add(21, 24, 51, 54);

        inputController = new InputController();
        gameplayController = new GameplayController();
        board = new MapModel(10,10, obstacles, directory);

        playerModel = new PlayerModel(new Vector2(100,100),0, directory);
        playerController = new PlayerController(playerModel);

        enemyModel enemy = new enemyModel(new Vector2(600, 300), 90, directory);
        enemyController = new EnemyController(enemy, playerModel, board.getTileState(0, 0), board.getTileState(0, 0), EnemyStates.PATROL);

        enemies.add(enemy);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // YOU WILL NEED TO MODIFY THIS NEXT LINE
        collisionController = new CollisionController(board, playerModel, enemies, canvas.getWidth(), canvas.getHeight());
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
    public void update(float delta) {
        inputController.readInput(null,null);
        playerController.update(inputController.getHorizontal(), inputController.getVertical(), inputController.didDecelerate(), inputController.didBoost(), inputController.didVacuum());
        enemyController.update();
        collisionController.update();

        Gdx.gl.glClearColor(0.39f, 0.58f, 0.93f, 1.0f);  // Homage to the XNA years
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        canvas.begin();
        canvas.center(camera, playerController.getModel().getPosition().x, playerController.getModel().getPosition().y);
        board.draw(canvas);
        playerController.draw(canvas, inputController.getHorizontal(), inputController.getVertical());
        enemyController.draw(canvas);
        canvas.end();
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
