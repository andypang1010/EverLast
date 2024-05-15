package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.redpacts.frostpurge.game.util.ScreenListener;
import com.redpacts.frostpurge.game.views.GameCanvas;
//import com.redpacts.frostpurge.game.assets.AssetDirectory;

public class GDXRoot extends Game implements ScreenListener {
	/** AssetManager to load game assets (textures, sounds, etc.) */
//	AssetDirectory directory;
	/** Drawing context to display graphics (VIEW CLASS) */
	private GameCanvas canvas;
	/** Player mode for the asset loading screen (CONTROLLER CLASS) */
//	private LoadingMode loading;
	/** Player mode for the the game proper (CONTROLLER CLASS) */
	private GameMode playing;
	private LoadingMode loading;
	private LevelSelectMode levelselect;
	private AssetDirectory directory;
	private String mode;
	SaveFileManager saveFileManager;
	/**
	 * Creates a new game from the configuration settings.
	 *
	 * This method configures the asset manager, but does not load any assets
	 * or assign any screen.
	 */
	public GDXRoot( ) {}

	/**
	 * Called when the Application is first created.
	 *
	 * This is method immediately loads assets for the loading screen, and prepares
	 * the asynchronous loader for all other assets.
	 */
	public void create() {
		canvas  = new GameCanvas();
		loading = new LoadingMode("assets.json", canvas);
		playing = new GameMode(canvas);

		mode = "loading";
		loading.setScreenListener(this);

		setScreen(loading);
//		setScreen(playing);
	}

	@Override
	public void render(){
		// Update the game state
		switch (mode){
			case "loading":
				loading.render(60);
				break;
			case "levelselect":
				levelselect.render(60);
				break;
			case "playing":
				playing.update(60);
		}
		// Draw the game

	}

	/**
	 * Called when the Application is destroyed.
	 *
	 * This is preceded by a call to pause().
	 */
	public void dispose() {
		// Call dispose on our children
		Screen screen = getScreen();
		setScreen(null);
		screen.dispose();
		canvas.dispose();
		canvas = null;

		// Unload all of the resources
//		if (directory != null) {
//			directory.unloadAssets();
//			directory.dispose();
//			directory = null;
//		}
		super.dispose();
	}

	/**
	 * Called when the Application is resized.
	 *
	 * This can happen at any point during a non-paused state but will never happen
	 * before a call to create().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		canvas.resize();
		super.resize(width,height);
	}

	/**
	 * The given screen has made a request to exit its player mode.
	 *
	 * The value exitCode can be used to implement menu options.
	 *
	 * @param screen   The screen requesting to exit
	 * @param exitCode The state of the screen upon exit
	 */
	public void exitScreen(Screen screen, int exitCode) {
		if (exitCode != 0 && exitCode != 1) {
			Gdx.app.error("GDXRoot", "Exit with error code "+exitCode, new RuntimeException());
			Gdx.app.exit();
		} else if (exitCode == 1) {
			Gdx.app.exit();
		} else if (screen == loading) {
			loading.resetButton();

			directory = loading.getAssets();
			playing.populate(directory);

			levelselect = new LevelSelectMode(canvas,playing);
			levelselect.setScreenListener(this);
			setScreen(levelselect);
			mode = "levelselect";
			levelselect.playmusic();

		} else if (screen == levelselect) {
			levelselect.resetPressState();
			levelselect.pausemusic();

//			playing.loadLevel(levelselect.getLevel(), levelselect.getSaveFile());
			playing.setScreenListener(this);
			setScreen(playing);
			mode = "playing";
			playing.playmusic();
		} else if (screen == playing && playing.isSettings()) {
			playing.resetButton();
			playing.pausemusic();

			levelselect.loading = false;
			levelselect.loadNext = false;
			levelselect.levelPage = -1;
			levelselect.resetTime();
			levelselect.levelSelectButton.resize("up");
			levelselect.setScreenListener(this);
			setScreen(levelselect);
			mode = "levelselect";
			levelselect.playmusic();
		} else if (screen == playing && playing.isLevelSelectScreen()) {
			playing.resetButton();
			playing.pausemusic();

			levelselect.loading = false;
			levelselect.loadNext = false;
			levelselect.resetTime();
			levelselect.setScreenListener(this);
			setScreen(levelselect);
			mode = "levelselect";
			levelselect.playmusic();
		} else if (screen == playing && playing.isRetry()) {
			playing.resetButton();
			playing.pausemusic();

			if (levelselect.xbox != null){
				System.out.println("HEHEHHEHEHEHEHEHHEHEHEHEHEHEHE");
				levelselect.loadNext = true;
			}else{
				levelselect.loading = true;
			}
			levelselect.setScreenListener(this);
			setScreen(levelselect);
			mode = "levelselect";
			levelselect.playmusic();
			new Thread(levelselect.load).start();
		} else if (screen == playing) {
			playing.resetButton();
			playing.pausemusic();

			levelselect.loading = false;
			levelselect.resetTime();
			levelselect.setScreenListener(this);
			setScreen(levelselect);
			mode = "levelselect";
			levelselect.playmusic();
		} else {
			// We quit the main application
			Gdx.app.exit();
		}
	}

}
