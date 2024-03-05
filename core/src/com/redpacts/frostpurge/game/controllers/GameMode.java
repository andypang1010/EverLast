package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
//import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redpacts.frostpurge.game.models.MapModel;
import com.redpacts.frostpurge.game.models.PlayerModel;
import com.redpacts.frostpurge.game.util.ScreenListener;
import com.redpacts.frostpurge.game.views.GameCanvas;

public class GameMode implements Screen {
    private GameCanvas canvas;

    /** Reads input from keyboard or game pad (CONTROLLER CLASS) */
    private InputController inputController;
    /** Handle collision and physics (CONTROLLER CLASS) */
    private CollisionController physicsController;
    /** Constructs the game models and handle basic gameplay (CONTROLLER CLASS) */
    private GameplayController gameplayController;
    /** Whether or not this player mode is still active */
    private boolean active;
    /** Board for the game*/
    private MapModel Board;
    /** Player for the game*/
    private PlayerModel Player;
    /** Player for the game*/
    private PlayerController Playercontroller;

    /** Listener that will update the player mode when we are done */
    private ScreenListener listener;

    /** Variable to track the game state (SIMPLE FIELDS) */
    private GameState gameState;
    public GameMode(GameCanvas canvas) {
        this.canvas = canvas;
        active = false;
        // Null out all pointers, 0 out all ints, etc.
        gameState = GameState.INTRO;

        // Create the controllers.

        Array<Integer> obstacles = new Array<Integer>();// Obstacle locations
        obstacles.add(21, 24, 51, 54);

        inputController = new InputController();
        gameplayController = new GameplayController();
        Board = new MapModel(10,10, obstacles);
        Player = new PlayerModel(new Vector2(100,100),0);
        Playercontroller = new PlayerController(Player);
        // YOU WILL NEED TO MODIFY THIS NEXT LINE
        physicsController = new CollisionController(Board,Player,null, canvas.getWidth(), canvas.getHeight());
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
        Playercontroller.update(inputController.didAccelerate(),inputController.didDecelerate(),inputController.didRotateLeft(),inputController.didRotateRight(), inputController.didBoost());
        physicsController.update();

        Gdx.gl.glClearColor(0.39f, 0.58f, 0.93f, 1.0f);  // Homage to the XNA years
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        canvas.begin();
        Board.draw(canvas);
        Playercontroller.draw(canvas);
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
