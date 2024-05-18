/*
 * LoadingMode.java
 *
 * Asset loading is a really tricky problem.  If you have a lot of sound or images,
 * it can take a long time to decompress them and load them into memory.  If you just
 * have code at the start to load all your assets, your game will look like it is hung
 * at the start.
 *
 * The alternative is asynchronous asset loading.  In asynchronous loading, you load a
 * little bit of the assets at a time, but still animate the game while you are loading.
 * This way the player knows the game is not hung, even though he or she cannot do 
 * anything until loading is complete. You know those loading screens with the inane tips 
 * that want to be helpful?  That is asynchronous loading.  
 *
 * This player mode provides a basic loading screen.  While you could adapt it for
 * between level loading, it is currently designed for loading all assets at the 
 * start of the game.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * Updated asset version, 2/6/2021
 */
package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Null;
import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.redpacts.frostpurge.game.util.Controllers;
import com.redpacts.frostpurge.game.util.FilmStrip;
import com.redpacts.frostpurge.game.util.ScreenListener;
import com.redpacts.frostpurge.game.util.XBoxController;
import com.redpacts.frostpurge.game.views.GameCanvas;
import com.badlogic.gdx.math.Rectangle;

import com.badlogic.gdx.utils.Array;
import com.redpacts.frostpurge.game.controllers.GameMode;

import java.util.Locale;

/**
 * Class that provides a loading screen for the state of the game.
 *
 * You still DO NOT need to understand this class for this lab.  We will talk about this
 * class much later in the course.  This class provides a basic template for a loading
 * screen to be used at the start of the game or between levels.  Feel free to adopt
 * this to your needs.
 *
 * You will note that this mode has some textures that are not loaded by the AssetManager.
 * You are never required to load through the AssetManager.  But doing this will block
 * the application.  That is why we try to have as few resources as possible for this
 * loading screen.
 */
public class LevelSelectMode implements Screen, InputProcessor, ControllerListener {
	// There are TWO asset managers.  One to load the loading screen.  The other to load the assets
	/** Internal assets for this loading screen */
	/** Standard window size (for scaling) */
	private static int STANDARD_WIDTH = 1920;
	/** Standard window height (for scaling) */
	private static int STANDARD_HEIGHT = 1080;
	/**
	 * The actual assets to be loaded
	 */
	private AssetDirectory assets;
	public boolean loading = false;
	public boolean loadNext = false;
	public float loadtime = 0;

	/**
	 * Background texture for level-select
	 */
	private Texture background0;
	private Texture background1;
	private Texture background2;
	private Texture background3;
	private Texture settingsBackground;
	private Texture page1;
	private Texture page2;
	private Texture page3;
	private Texture page4;
	private Texture page5;
	/**
	 * Texture for forward button
	 */
	private Texture forwardTexture;
	private ButtonBox forwardButton;
	/**
	 * Texture for backward button
	 */
	private Texture backwardTexture;
	private ButtonBox backwardButton;
	private Texture levelSelectTexture;
	public ButtonBox levelSelectButton;
	private Texture settingsTexture;
	private ButtonBox settingsButton;
	private Texture incTexture;

	private ButtonBox volumeLowButton;
	private Texture decTexture;
	private ButtonBox volumeHighButton;
	public static ProgressBar volumeBar;
	private ButtonBox sensitivityLowButton;
	private ButtonBox sensitivityHighButton;
	public static ProgressBar sensitivityBar;
	private Texture smallWindowTexture;
	private ButtonBox smallWindowButton;
	private Texture largeWindowTexture;
	private ButtonBox largeWindowButton;

	private Texture exitTexture;
	private ButtonBox exitButton;

	private Array<ButtonBox> levels;
	/** Texture atlas to support a progress bar */

	// statusBar is a "texture atlas." Break it up into parts.
	/** Left cap to the status background (grey region) */

	/**
	 * Reference to GameCanvas created by the root
	 */
	private GameCanvas canvas;
	/**
	 * Listener that will update the player mode when we are done
	 */
	private ScreenListener listener;
	/** Reads input from keyboard or game pad (CONTROLLER CLASS) */
	private InputController inputController;

	/** The width of the progress bar */

	/**
	 * Current progress (0 to 1) of the asset manager
	 */
	private int pressState;
	/**
	 * Whether or not this player mode is still active
	 */
	private boolean active;
	/**
	 * The scale for texts on the screen
	 */
	private static float scale;
	/**
	 * The scale of the screen relative to a standard size in x
	 */
	private float sx;
	/**
	 * The scale of the screen relative to a standard size in y
	 */
	private float sy;
	private BitmapFont font;
	private String selectedLevel;
	public XBoxController xbox;
	private float time;

	private SaveFileManager game;

	int numberOfLevels;
	int levelPage;
	/**
	 * pageDirection = 1 means go to right, = -1 means go to left, 0 means go nowhere
	 */
	int pageDirection;
	Texture emptyStars;
	TextureRegion stars;
	Texture lock;
	Music sample;
	AssetLoader load;
	Texture loadingscreen;
	FilmStrip loadAnimation;
	BitmapFont chalkFont;

	GameMode gamemode;

	public String getLevel(){
		return selectedLevel;
	}
	public void increaseLevel(){
		String curr = selectedLevel.substring(5);
		int level = Integer.parseInt(curr)+1;
		selectedLevel = "level" + Integer.toString(level);
	}
	public void resetPressState(){pressState = 0;}

	public SaveFileManager getSaveFile(){return game;}

	public void resetTime(){
		time =0;
	}


	/**
	 * Returns true if all assets are loaded and the player is ready to go.
	 *
	 * @return true if the player is ready to go
	 */
	public boolean isReady() {
		return pressState % 2 == 0 && pressState != 0;
	}

	/**
	 * Returns the asset directory produced by this loading screen
	 * <p>
	 * This asset loader is NOT owned by this loading scene, so it persists even
	 * after the scene is disposed.  It is your responsbility to unload the
	 * assets in this directory.
	 *
	 * @return the asset directory produced by this loading screen
	 */
	public AssetDirectory getAssets() {
		return assets;
	}


	/**
	 * Creates a LoadingMode with the default size and position.
	 * <p>
	 * The budget is the number of milliseconds to spend loading assets each animation
	 * frame.  This allows you to do something other than load assets.  An animation
	 * frame is ~16 milliseconds. So if the budget is 10, you have 6 milliseconds to
	 * do something else.  This is how game companies animate their loading screens.
	 *
	 * @param canvas The game canvas to draw to
	 */
	public LevelSelectMode(GameCanvas canvas, GameMode gamemode) {
		load = new AssetLoader(this,gamemode);
		this.canvas = canvas;
		inputController = new InputController();
		this.gamemode = gamemode;
		// Compute the dimensions from the canvas
		resize(canvas.getWidth(), canvas.getHeight());

		// We need these files loaded immediately
		assets = new AssetDirectory( "levelselect.json" );
		assets.loadAssets();
		assets.finishLoading();

		loadingscreen = assets.getEntry("loadingBackground",Texture.class);
		Texture loadtexture = assets.getEntry("loading",Texture.class);
		loadAnimation = new FilmStrip(loadtexture,1,4,4);
		// Load the background
		background0 = assets.getEntry("background0", Texture.class);
		background0.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		background1 = assets.getEntry("background1", Texture.class);
		background1.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		background2 = assets.getEntry("background2", Texture.class);
		background2.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		background3 = assets.getEntry("background3", Texture.class);
		background3.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		settingsBackground = assets.getEntry("settingsBackground",Texture.class);
		settingsBackground.setFilter(TextureFilter.Linear,TextureFilter.Linear);

		page1 = assets.getEntry("page1",Texture.class);
		page2 = assets.getEntry("page2",Texture.class);
		page3 = assets.getEntry("page3",Texture.class);
		page4 = assets.getEntry("page4",Texture.class);
		page5 = assets.getEntry("page5",Texture.class);

		emptyStars = assets.getEntry("emptystars",Texture.class);
		stars = new TextureRegion(assets.getEntry("filledstars",Texture.class));
		lock = assets.getEntry("lock",Texture.class);
		// Load the direction button

		forwardTexture = assets.getEntry("forwardButton", Texture.class);
		forwardButton = new ButtonBox(0,
				new Rectangle(STANDARD_WIDTH * 85 / 100, STANDARD_HEIGHT * 1/18, forwardTexture.getWidth(), forwardTexture.getHeight()), forwardTexture, this);
		forwardButton.available = true;

		backwardTexture = assets.getEntry("backwardButton", Texture.class);
		backwardButton = new ButtonBox(0,
				new Rectangle(STANDARD_WIDTH * 6 / 100, STANDARD_HEIGHT * 1/18, backwardTexture.getWidth(), backwardTexture.getHeight()), backwardTexture, this);
		backwardButton.available = true;

		levelSelectTexture = assets.getEntry("levelSelectButton",Texture.class);
		levelSelectButton = new ButtonBox(-1,new Rectangle(STANDARD_WIDTH*6/100,STANDARD_HEIGHT*1/15,levelSelectTexture.getWidth(),levelSelectTexture.getHeight()),levelSelectTexture,this);
		levelSelectButton.available = true;

		settingsTexture = assets.getEntry("settingsButton",Texture.class);
		settingsButton = new ButtonBox(-2,new Rectangle(STANDARD_WIDTH*86/100,STANDARD_HEIGHT*13/15,settingsTexture.getWidth(), settingsTexture.getHeight()),settingsTexture,this);
		settingsButton.available = true;


		Skin skin = new Skin(Gdx.files.internal("ui/skin/clean-crispy-ui.json"));

		volumeBar = new ProgressBar(0f, 1f, 0.05f,false, skin);
		volumeBar.setValue(volumeBar.getMaxValue() / 2);

		incTexture = assets.getEntry("incButton",Texture.class);
		decTexture = assets.getEntry("decButton",Texture.class);
		smallWindowTexture = assets.getEntry("smallRes",Texture.class);
		largeWindowTexture = assets.getEntry("largeRes",Texture.class);

		volumeLowButton = new ButtonBox(-3,
				new Rectangle(STANDARD_WIDTH * 22f / 100f, STANDARD_HEIGHT * 66f/100f, decTexture.getWidth(),decTexture.getHeight()),decTexture,this);
		volumeLowButton.available = true;

		volumeHighButton = new ButtonBox(-4,
				new Rectangle(STANDARD_WIDTH * 54f/100f, STANDARD_HEIGHT * 66f/100f, incTexture.getWidth(),incTexture.getHeight()),incTexture,this);
		volumeHighButton.available = true;

		sensitivityBar = new ProgressBar(0f, 2f, 0.1f,false, skin);
		sensitivityBar.setValue(sensitivityBar.getMaxValue() / 2);

		sensitivityLowButton = new ButtonBox(-5,
				new Rectangle(STANDARD_WIDTH * 22f / 100f, STANDARD_HEIGHT * 56f/100f, decTexture.getWidth(),decTexture.getHeight()),decTexture,this);
		sensitivityLowButton.available = true;

		sensitivityHighButton = new ButtonBox(-6,
				new Rectangle(STANDARD_WIDTH * 54f/100f, STANDARD_HEIGHT * 56f/100f, incTexture.getWidth(),incTexture.getHeight()),incTexture,this);
		sensitivityHighButton.available = true;

		smallWindowButton = new ButtonBox(-7,
				new Rectangle(STANDARD_WIDTH * 22f / 100f, STANDARD_HEIGHT * 35f / 100f, smallWindowTexture.getWidth(), smallWindowTexture.getHeight()), smallWindowTexture, this);
		smallWindowButton.available = true;

		largeWindowButton = new ButtonBox(-8,
				new Rectangle(STANDARD_WIDTH * 22f / 100f, STANDARD_HEIGHT * 25f / 100f, largeWindowTexture.getWidth(), largeWindowTexture.getHeight()), largeWindowTexture, this);
		largeWindowButton.available = true;


		exitTexture = assets.getEntry("exitGameButton",Texture.class);
		exitButton = new ButtonBox(-9,new Rectangle((float) (STANDARD_WIDTH*87/100), (float) (STANDARD_HEIGHT*1.5/100),exitTexture.getWidth(),exitTexture.getHeight()),exitTexture,this);
		exitButton.available = true;

		// Load the level button
		// Label of each button represents the level number
		numberOfLevels = 15; // Number of levels
		levelPage = 0;
		pageDirection = 0;
		Texture button1 = assets.getEntry("level1", Texture.class);
		ButtonBox level1Button = new ButtonBox(1,
				new Rectangle(STANDARD_WIDTH * 8 / 100, STANDARD_HEIGHT * 3/18, 7*button1.getWidth()/8, 7*button1.getHeight()/8), button1, this);

		Texture button2 = assets.getEntry("level2", Texture.class);
		ButtonBox level2Button = new ButtonBox(2,
				new Rectangle(STANDARD_WIDTH * 38 / 100, STANDARD_HEIGHT * 3/18, 7*button2.getWidth()/8, 7*button2.getHeight()/8), button2, this);

		Texture button3 = assets.getEntry("level3", Texture.class);
		ButtonBox level3Button = new ButtonBox(3,
				new Rectangle(STANDARD_WIDTH * 68 / 100, STANDARD_HEIGHT * 3/18, 7*button3.getWidth()/8, 7*button3.getHeight()/8), button3, this);

		// TODO: Change scale when we have actual button for level 4, 5
		Texture button4 = assets.getEntry("level4", Texture.class);
		ButtonBox level4Button = new ButtonBox(4,
				new Rectangle(STANDARD_WIDTH * 8 / 100, STANDARD_HEIGHT * 3/18, 7*button4.getWidth()/8, 7*button4.getHeight()/8), button4, this);

		Texture button5 = assets.getEntry("level5", Texture.class);
		ButtonBox level5Button = new ButtonBox(5,
				new Rectangle(STANDARD_WIDTH * 38 / 100, STANDARD_HEIGHT * 3/18, 7*button5.getWidth()/8, 7*button5.getHeight()/8), button5, this);

		Texture button6 = assets.getEntry("level6", Texture.class);
		ButtonBox level6Button = new ButtonBox(6,
				new Rectangle(STANDARD_WIDTH * 68 / 100, STANDARD_HEIGHT * 3/18, 7*button3.getWidth()/8, 7*button3.getHeight()/8), button6, this);

		Texture button7 = assets.getEntry("level7", Texture.class);
		ButtonBox level7Button = new ButtonBox(7,
				new Rectangle(STANDARD_WIDTH * 8 / 100, STANDARD_HEIGHT * 3/18, 7*button1.getWidth()/8, 7*button1.getHeight()/8), button7, this);

		Texture button8 = assets.getEntry("level8", Texture.class);
		ButtonBox level8Button = new ButtonBox(8,
				new Rectangle(STANDARD_WIDTH * 38 / 100, STANDARD_HEIGHT * 3/18, 7*button2.getWidth()/8, 7*button2.getHeight()/8), button8, this);

		Texture button9 = assets.getEntry("level9", Texture.class);
		ButtonBox level9Button = new ButtonBox(9,
				new Rectangle(STANDARD_WIDTH * 68 / 100, STANDARD_HEIGHT * 3/18, 7*button3.getWidth()/8, 7*button3.getHeight()/8), button9, this);

		Texture button10 = assets.getEntry("level10", Texture.class);
		ButtonBox level10Button = new ButtonBox(10,
				new Rectangle(STANDARD_WIDTH * 8 / 100, STANDARD_HEIGHT * 3/18, 7*button1.getWidth()/8, 7*button1.getHeight()/8), button10, this);

		Texture button11 = assets.getEntry("level11", Texture.class);
		ButtonBox level11Button = new ButtonBox(11,
				new Rectangle(STANDARD_WIDTH * 38 / 100, STANDARD_HEIGHT * 3/18, 7*button2.getWidth()/8, 7*button2.getHeight()/8), button11, this);

		Texture button12 = assets.getEntry("level12", Texture.class);
		ButtonBox level12Button = new ButtonBox(12,
				new Rectangle(STANDARD_WIDTH * 68 / 100, STANDARD_HEIGHT * 3/18, 7*button3.getWidth()/8, 7*button3.getHeight()/8), button12, this);

		Texture button13 = assets.getEntry("level13", Texture.class);
		ButtonBox level13Button = new ButtonBox(13,
				new Rectangle(STANDARD_WIDTH * 8 / 100, STANDARD_HEIGHT * 3/18, 7*button1.getWidth()/8, 7*button1.getHeight()/8), button13, this);

		Texture button14 = assets.getEntry("level14", Texture.class);
		ButtonBox level14Button = new ButtonBox(14,
				new Rectangle(STANDARD_WIDTH * 38 / 100, STANDARD_HEIGHT * 3/18, 7*button2.getWidth()/8, 7*button2.getHeight()/8), button14, this);

		Texture button15 = assets.getEntry("level15", Texture.class);
		ButtonBox level15Button = new ButtonBox(15,
				new Rectangle(STANDARD_WIDTH * 68 / 100, STANDARD_HEIGHT * 3/18, 7*button3.getWidth()/8, 7*button3.getHeight()/8), button15, this);

		levels = new Array<>(numberOfLevels);
		levels.add(level1Button);
		levels.add(level2Button);
		levels.add(level3Button);
		levels.add(level4Button);
		levels.add(level5Button);
		levels.add(level6Button);
		levels.add(level7Button);
		levels.add(level8Button);
		levels.add(level9Button);
		levels.add(level10Button);
		levels.add(level11Button);
		levels.add(level12Button);
		levels.add(level13Button);
		levels.add(level14Button);
		levels.add(level15Button);

		// Break up the status bar texture into regions
		font = assets.getEntry("font", BitmapFont.class);
		chalkFont = assets.getEntry("chalk", BitmapFont.class);
		pressState = 0;

		active = true;

		sample = assets.getEntry( "song", Music.class );
		sample.setLooping(true);

		// Let ANY connected controller start the game.
		for (XBoxController controller : Controllers.get().getXBoxControllers()) {
			controller.addListener(this);
			xbox = controller;
		}
		if (xbox != null){
			// TODO: Support XBox
			level1Button.resize("up");
		}
		time = 0;

		game = new SaveFileManager(assets.getEntry("savedata", JsonValue.class));
	}

	public void playmusic(){
		sample.setPosition(0);
		sample.play();
	}
	public void pausemusic(){
		sample.pause();
	}
	/**
	 * Called when this screen should release all resources.
	 */
	public void dispose() {
		assets.unloadAssets();
		assets.dispose();
	}

	/**
	 * Update the status of this player mode.
	 * <p>
	 * We prefer to separate update and draw from one another as separate methods, instead
	 * of using the single render() method that LibGDX does.  We will talk about why we
	 * prefer this in lecture.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	private void update(float delta) {
//		if (playButton == null) {
//			assets.update(budget);
//			this.progress = assets.getProgress();
//			if (progress >= 1.0f) {
//				this.progress = 1.0f;
//				playButton = internal.getEntry("play",Texture.class);
//			}
//		}
	}

	/**
	 * Draw the status of this player mode.
	 * <p>
	 * We prefer to separate update and draw from one another as separate methods, instead
	 * of using the single render() method that LibGDX does.  We will talk about why we
	 * prefer this in lecture.
	 */
	private void draw() {
	canvas.begin();

	if (loading) {
		drawload();
		if (xbox != null){
			gamemode.loadLevel(getLevel(),game);

			// Assets loaded, switch to the main game screen
			listener.exitScreen(this,0);
		}else{
			canvas.drawBackground(loadingscreen,0,0,true);
			processLoad();
			canvas.draw(loadAnimation,Color.WHITE, 0, 0,1040*scale,(float) 150*scale,0,.65f*scale,.65f*scale,false);
		}
	} else if (loadNext) {
			drawload();
			if (loadtime <.3f){
//				System.out.println("waiting");
				loadtime+= Gdx.graphics.getDeltaTime();
			}else{
				gamemode.loadLevel(getLevel(),game);
				listener.exitScreen(this,0);
			}
	} else{
		if (scale != 0) {
			font.getData().setScale(scale);
		}

		if (levelPage == -1) {
			canvas.drawBackground(settingsBackground, 0, 0, true);
		} else {
			switch(levelPage){
				case 0:
					canvas.drawBackground(background0, 0, 0, true);
					canvas.draw(page1,800 * scale,50 * scale,page1.getWidth(),page1.getHeight());
					break;
				case 1:
					canvas.drawBackground(background1, 0, 0, true);
					canvas.draw(page2,800 * scale,50 * scale,page2.getWidth(),page2.getHeight());
					break;
				case 2:
					canvas.drawBackground(background2, 0, 0, true);
					canvas.draw(page3,800 * scale,50 * scale,page3.getWidth(),page3.getHeight());
					break;
				case 3:
					canvas.drawBackground(background3, 0, 0, true);
					canvas.draw(page4,800 * scale,50 * scale,page4.getWidth(),page4.getHeight());
					break;
				case 4:
					canvas.drawBackground(background3, 0, 0, true);
					canvas.draw(page5,800 * scale,50 * scale,page5.getWidth(),page5.getHeight());
					break;
			}

		}
		Rectangle bounds;
		if (xbox == null) {
			if (levelPage == -1) {
				exitButton.hoveringButton(xbox, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = exitButton.getBounds();
				canvas.draw(exitButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

				levelSelectButton.hoveringButton(null, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = levelSelectButton.getBounds();
				canvas.draw(levelSelectButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

//				font.setColor(Color.BLACK);
//				canvas.drawText("VOL: ", font, canvas.getWidth() * 1.5f / 7f, canvas.getHeight() * 3.25f / 5f);
				canvas.drawBar(volumeBar, canvas.getWidth() / 4f, canvas.getHeight() / 20f, canvas.getWidth() * 27f / 100f, canvas.getHeight() * 66f/ 100f);

				volumeLowButton.hoveringButton(null, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = volumeLowButton.getBounds();
				canvas.draw(volumeLowButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

				volumeHighButton.hoveringButton(null, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = volumeHighButton.getBounds();
				canvas.draw(volumeHighButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

//				font.setColor(Color.BLACK);
//				canvas.drawText("SENS: ", font, canvas.getWidth() * 1.5f / 7f, canvas.getHeight() * 2.25f / 5f);
				canvas.drawBar(sensitivityBar, canvas.getWidth() / 4f, canvas.getHeight() / 20f, canvas.getWidth() * 27f/100f, canvas.getHeight() * 56f/100f);

				sensitivityLowButton.hoveringButton(null, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = sensitivityLowButton.getBounds();
				canvas.draw(sensitivityLowButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

				sensitivityHighButton.hoveringButton(null, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = sensitivityHighButton.getBounds();
				canvas.draw(sensitivityHighButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);


				smallWindowButton.hoveringButton(null, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = smallWindowButton.getBounds();
				canvas.draw(smallWindowButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

				largeWindowButton.hoveringButton(null, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = largeWindowButton.getBounds();
				canvas.draw(largeWindowButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

			} else {
				settingsButton.hoveringButton(null, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = settingsButton.getBounds();
				canvas.draw(settingsTexture, bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);
			}
			if (levelPage == 0) {
				forwardButton.hoveringButton(null, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = forwardButton.getBounds();
				canvas.draw(forwardButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);
			} else if (levelPage == (numberOfLevels - 1) / 3) {
				backwardButton.hoveringButton(null, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = backwardButton.getBounds();
				canvas.draw(backwardButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);
			} else if (levelPage != -1) {
				forwardButton.hoveringButton(null, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = forwardButton.getBounds();
				canvas.draw(forwardButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);
				backwardButton.hoveringButton(null, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = backwardButton.getBounds();
				canvas.draw(backwardButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);
			}
			if (levelPage != -1) {
				for (ButtonBox button : levels) {
					int level = button.getLabel();
					if (levelPage * 3 < level && level <= (levelPage + 1) * 3) {
						button.hoveringButton(xbox, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
						bounds = button.getBounds();
						float hoverScale;
						if (button.enlarged){
							hoverScale = (float) 8 /7;
						}else{
							hoverScale = 1;
						}
						canvas.draw(button.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);
						if (button.available){
							canvas.drawCentered(emptyStars, (bounds.x + bounds.width / 2) * scale, 11 * bounds.y * scale *hoverScale/ 6, (float) (emptyStars.getWidth()*.75 * scale*hoverScale), (float) (emptyStars.getHeight()*.75 * scale*hoverScale));
						}
						float score = game.getScore("level" + Integer.toString(button.label));
						float starsScore = game.getStarScore("level" + Integer.toString(button.label));
						float ratio = starsScore / 30;
						if (score != 0) {
							if (scale!=0){
								if (button.enlarged){
									chalkFont.getData().setScale((float) 8/7*scale);
								}else{
									chalkFont.getData().setScale(1*scale);
								}
							}
							chalkFont.setColor(Color.BLACK);
							canvas.drawText(scoreToTime(score), chalkFont, (bounds.x + bounds.width / 2 + 10) * scale, (float) (6 * (bounds.y + bounds.height) * scale / 8));
						}
						if (ratio > 1) {
							ratio = 1;
						}
						if (ratio != 0) {
							stars.setRegionWidth((int) (emptyStars.getWidth() * ratio));
							canvas.drawCentered(stars, ((bounds.x + bounds.width / 2 - (emptyStars.getWidth()*.75f *hoverScale* (1 - ratio) / 2))) * scale, 11 * bounds.y * scale*hoverScale / 6, (float) ((float) stars.getRegionWidth() * .75* scale*hoverScale), (float) (stars.getRegionHeight() * .75* scale*hoverScale));
							stars.setRegionWidth((emptyStars.getWidth()));
						}
						if (!button.available) {
							canvas.draw(lock, (bounds.x) * scale, (bounds.y) * scale, lock.getWidth() *.88f*scale, lock.getHeight() *.88f* scale);
						}
					}
				}
			}
		} else {
			// TODO: Support XBox
			if (levelPage == -1) {
				exitButton.hoveringButton(xbox, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = exitButton.getBounds();
				canvas.draw(exitButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

				levelSelectButton.hoveringButton(xbox, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = levelSelectButton.getBounds();
				canvas.draw(levelSelectButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

//				font.setColor(Color.BLACK);
//				canvas.drawText("VOL: ", font, canvas.getWidth() * 1.5f / 7f, canvas.getHeight() * 3.25f / 5f);
				canvas.drawBar(volumeBar, canvas.getWidth() / 4f, canvas.getHeight() / 20f, canvas.getWidth() * 27f / 100f, canvas.getHeight() * 66f/ 100f);

				volumeLowButton.hoveringButton(xbox, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = volumeLowButton.getBounds();
				canvas.draw(volumeLowButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

				volumeHighButton.hoveringButton(xbox, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = volumeHighButton.getBounds();
				canvas.draw(volumeHighButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

//				font.setColor(Color.BLACK);
//				canvas.drawText("SENS: ", font, canvas.getWidth() * 1.5f / 7f, canvas.getHeight() * 2.25f / 5f);
				canvas.drawBar(sensitivityBar, canvas.getWidth() / 4f, canvas.getHeight() / 20f, canvas.getWidth() * 27f/100f, canvas.getHeight() * 56f/100f);

				sensitivityLowButton.hoveringButton(xbox, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = sensitivityLowButton.getBounds();
				canvas.draw(sensitivityLowButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

				sensitivityHighButton.hoveringButton(xbox, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = sensitivityHighButton.getBounds();
				canvas.draw(sensitivityHighButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);


				smallWindowButton.hoveringButton(xbox, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = smallWindowButton.getBounds();
				canvas.draw(smallWindowButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

				largeWindowButton.hoveringButton(xbox, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = largeWindowButton.getBounds();
				canvas.draw(largeWindowButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);

			} else {
				settingsButton.hoveringButton(xbox, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = settingsButton.getBounds();
				canvas.draw(settingsTexture, bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);
			}
			if (levelPage == 0) {
				forwardButton.hoveringButton(xbox, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = forwardButton.getBounds();
				canvas.draw(forwardButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);
			} else if (levelPage == (numberOfLevels - 1) / 3) {
				backwardButton.hoveringButton(xbox, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = backwardButton.getBounds();
				canvas.draw(backwardButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);
			} else if (levelPage != -1) {
				forwardButton.hoveringButton(xbox, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = forwardButton.getBounds();
				canvas.draw(forwardButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);
				backwardButton.hoveringButton(xbox, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
				bounds = backwardButton.getBounds();
				canvas.draw(backwardButton.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);
			}
			//			for (LevelBox levelBox: levelBoxes){
			//				levelBox.font.setColor(pressState == levelBox.label*2-1 ? Color.GRAY : Color.DARK_GRAY);
			//				levelBox.font.getData().setScale(levelBox.fontScale*scale);
			//				canvas.drawText("level " + Integer.toString(levelBox.label), levelBox.font, levelBox.bounds.x*sx,levelBox.enlarged ? (levelBox.bounds.y+levelBox.glyph.height *1.25f)*sy : (levelBox.bounds.y+levelBox.glyph.height)*sy );
			if (levelPage != -1) {
				for (ButtonBox button : levels) {
					int level = button.getLabel();
					if (levelPage * 3 < level && level <= (levelPage + 1) * 3) {
						button.hoveringButton(xbox, time, levels.size, levels, settingsButton, exitButton, volumeLowButton, volumeHighButton, sensitivityLowButton, sensitivityHighButton, smallWindowButton, largeWindowButton);
						bounds = button.getBounds();
						float hoverScale;
						if (button.enlarged){
							hoverScale = (float) 8 /7;
						}else{
							hoverScale = 1;
						}
						canvas.draw(button.getTexture(), bounds.x * scale, bounds.y * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);
						if (button.available){
							canvas.drawCentered(emptyStars, (bounds.x + bounds.width / 2) * scale, 11 * bounds.y * scale *hoverScale/ 6, (float) (emptyStars.getWidth()*.75 * scale*hoverScale), (float) (emptyStars.getHeight()*.75 * scale*hoverScale));
						}
						float score = game.getScore("level" + Integer.toString(button.label));
						float starsScore = game.getStarScore("level" + Integer.toString(button.label));
						float ratio = starsScore / 30;
						if (score != 0) {
							if (button.enlarged){
								chalkFont.getData().setScale((float) 8/7*scale);
							}else{
								chalkFont.getData().setScale(1*scale);
							}
							chalkFont.setColor(Color.BLACK);
							canvas.drawText(scoreToTime(score), chalkFont, (bounds.x + bounds.width / 2 + 10) * scale, (float) (6 * (bounds.y + bounds.height) * scale / 8));
						}
						if (ratio > 1) {
							ratio = 1;
						}
						if (ratio != 0) {
							stars.setRegionWidth((int) (emptyStars.getWidth() * ratio));
							canvas.drawCentered(stars, ((bounds.x + bounds.width / 2 - (emptyStars.getWidth()*.75f*hoverScale * (1 - ratio) / 2))) * scale, 11 * bounds.y * scale*hoverScale / 6, (float) ((float) stars.getRegionWidth() * .75* scale*hoverScale), (float) (stars.getRegionHeight() * .75* scale*hoverScale));
							stars.setRegionWidth((emptyStars.getWidth()));
						}
						if (!button.available) {
							canvas.draw(lock, (bounds.x) * scale, (bounds.y) * scale, lock.getWidth() *.88f*scale, lock.getHeight() *.88f* scale);
						}
					}
				}
			}
		}
	}
		canvas.end();
	}


	// ADDITIONAL SCREEN METHODS

	/**
	 * Called when the Screen should render itself.
	 * <p>
	 * We defer to the other methods update() and draw().  However, it is VERY important
	 * that we only quit AFTER a draw.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	public void render(float delta) {
		if(loadNext){
			drawloadSneaky();
		}
		Gdx.input.setInputProcessor(this);
		inputController.readInput(null,null);
		sample.setVolume(0.5f * volumeBar.getValue());
//
//		if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
//			volumeSlider.setValue(volumeSlider.getValue() - volumeSlider.getStepSize());
//		}
//
//		if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
//			volumeSlider.setValue(volumeSlider.getValue() + volumeSlider.getStepSize());
//		}
//
//		if (Gdx.input.isKeyPressed(Input.Keys.NUM_9)) {
//			sensitivitySlider.setValue(sensitivitySlider.getValue() - sensitivitySlider.getStepSize());
//		}
//
//		if (Gdx.input.isKeyPressed(Input.Keys.NUM_0)) {
//			sensitivitySlider.setValue(sensitivitySlider.getValue() + sensitivitySlider.getStepSize());
//		}

		if (active) {
			time += Gdx.graphics.getDeltaTime();
//			update(delta);
			draw();
//			System.out.println(levelBoxes.get(0).enlarged);
//			System.out.println(levelBoxes.get(1).enlarged);
			buttonDown(null, 0);
			buttonUp(null,0);
			// We are are ready, notify our listener

			if (Gdx.input.isKeyPressed(Input.Keys.C)){
				game.clearGame();
//				System.out.println("cleared");
			}
			if (Gdx.input.isKeyPressed(Input.Keys.U)){
				game.unlockAll();
			}
			if (isReady() && listener != null) {
				pressState = 0;
				if (xbox == null){
					loading = true;
				}else{
					loadNext = true;
				}
				if (xbox!=null){
					canvas.begin();
					drawload();
					canvas.end();
				}else{
					new Thread(load).start();
//					listener.exitScreen(this, 0);
				}


			}
		}
		//game.update(assets.getEntry("savedata", JsonValue.class));
		for (ButtonBox button: levels){
			button.available = game.getUnlockStatus("level"+Integer.toString(button.label));
		}

	}

	@Override
	public void resize(int i, int i1) {
		sx = ((float)canvas.getWidth())/STANDARD_WIDTH;
		sy = ((float)canvas.getHeight())/STANDARD_HEIGHT;
		scale = (sx < sy ? sx : sy);
	}


	/**
	 * Called when the Screen is paused.
	 * <p>
	 * This is usually when it's not active or visible on screen. An Application is
	 * also paused before it is destroyed.
	 */
	public void pause() {
		// TODO Auto-generated method stub

	}

	/**
	 * Called when the Screen is resumed from a paused state.
	 * <p>
	 * This is usually when it regains focus.
	 */
	public void resume() {
		// TODO Auto-generated method stub

	}

	/**
	 * Called when this screen becomes the current screen for a Game.
	 */
	public void show() {
		// Useless if called in outside animation loop
		active = true;
	}

	/**
	 * Called when this screen is no longer the current screen for a Game.
	 */
	public void hide() {
		// Useless if called in outside animation loop
		active = false;
	}

	/**
	 * Sets the ScreenListener for this mode
	 * <p>
	 * The ScreenListener will respond to requests to quit.
	 */
	public void setScreenListener(ScreenListener listener) {
		this.listener = listener;
	}

	public void populate(AssetDirectory directory) {

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
		screenY = canvas.getHeight() - screenY;

//		for (LevelBox levelBox : levelBoxes) {
//			if (levelBox.bounds.contains(screenX/sx, screenY/sy)) {
//				pressState = levelBox.label *2 -1;
		if (levelPage == -1) {
			if (volumeLowButton.isPressed()) {
				volumeBar.setValue(volumeBar.getValue() - volumeBar.getStepSize());
			}

			if (volumeHighButton.isPressed()) {
				volumeBar.setValue(volumeBar.getValue() + volumeBar.getStepSize());
			}

			if (sensitivityLowButton.isPressed()) {
				sensitivityBar.setValue(sensitivityBar.getValue() - sensitivityBar.getStepSize());
			}

			if (sensitivityHighButton.isPressed()) {
				sensitivityBar.setValue(sensitivityBar.getValue() + sensitivityBar.getStepSize());
			}

			if (smallWindowButton.isPressed()) {
				Gdx.graphics.setWindowedMode(1280, 720);
			}

			if (largeWindowButton.isPressed()) {
				Gdx.graphics.setWindowedMode(1920, 1080);
			}

			if (levelSelectButton.isPressed()) {
				pageDirection = 1;
			}
		}else {
			if (settingsButton.isPressed()) {
				pageDirection = -(levelPage + 1);
			}
			if (exitButton.isPressed() && levelPage == -1) {
				listener.exitScreen(this, 1);
			}
			if (forwardButton.isPressed() && levelPage < (numberOfLevels - 1) / 3) {
				pageDirection = 1;
			} else if (backwardButton.isPressed() && levelPage > 0) {
				pageDirection = -1;
			}
			for (ButtonBox levelButton : levels) {
				int level = levelButton.getLabel();
				if (levelButton.isPressed() && levelPage * 3 < level && level <= (levelPage + 1) * 3 && levelButton.available) {
					pressState = levelButton.getLabel() * 2 - 1;
				}
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
		levelPage += pageDirection;
		pageDirection = 0;
		if (pressState % 2 == 1) {
			pressState += 1;
			selectedLevel = "level" + Integer.toString(pressState/2);
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
//		if (pressState == 0) {
//			if (xbox != null && xbox.getA()) {
//				for (LevelBox levelBox : levelBoxes){
//					if (levelBox.enlarged){
//						pressState = levelBox.label*2-1;
//					}
//				}
//				return false;
//			}
//		}
		if (pressState % 2 == 0 && pressState != 0) {
			return true;
		}
		if (xbox != null) {
			if (levelPage!=-1) {
				if (xbox.getRBumper() && levelPage < (numberOfLevels - 1) / 3 && time > .2f) {
					resetTime();
					pageDirection = 1;
					for (int i = 0; i < 3; i++) {
						try {
							if (levels.get(levelPage * 3 + i).enlarged) {
								levels.get(levelPage * 3 + i).resize("down");
							}
						} catch (Exception e) {

						}
						if (exitButton.enlarged){
							exitButton.resize("down");
						}else if(settingsButton.enlarged){
							settingsButton.resize("down");
						}
					}
					if (levels.get((levelPage + pageDirection) * 3).available) {
						levels.get((levelPage + pageDirection) * 3).resize("up");
					}

				} else if (xbox.getLBumper() && levelPage > 0 && time > .2f) {
					resetTime();
					pageDirection = -1;
					for (int i = 0; i < 3; i++) {
						try {
							if (levels.get(levelPage * 3 + i).enlarged) {
								levels.get(levelPage * 3 + i).resize("down");
							}
						} catch (Exception e) {

						}
						if (exitButton.enlarged){
							exitButton.resize("down");
						}else if(settingsButton.enlarged){
							settingsButton.resize("down");
						}
					}
					if (levels.get((levelPage + pageDirection) * 3 + 2).available) {
						levels.get((levelPage + pageDirection) * 3 + 2).resize("up");
					} else if (levels.get((levelPage + pageDirection) * 3 + 1).available) {
						levels.get((levelPage + pageDirection) * 3 + 1).resize("up");
					} else if (levels.get((levelPage + pageDirection) * 3).available) {
						levels.get((levelPage + pageDirection) * 3).resize("up");
					}
				}
				for (ButtonBox levelButton : levels) {
					int level = levelButton.label;
					if (levelButton.enlarged && levelPage * 3 < level && level <= (levelPage + 1) * 3 && levelButton.available && xbox.getA() && time>.1) {
						pressState = levelButton.label * 2 - 1;
					}
				}
				if (settingsButton.enlarged && xbox.getA() && time >.2){
					resetTime();
					pageDirection = -(levelPage+1);
					levelSelectButton.resize("up");
					for (int i = 0; i < 3; i++) {
						try {
							if (levels.get(levelPage * 3 + i).enlarged) {
								levels.get(levelPage * 3 + i).resize("down");
							}
						} catch (Exception e) {

						}
					}
				}
				if (exitButton.enlarged && xbox.getA()){
					listener.exitScreen(this,1);
				}
			}else{
				if (exitButton.enlarged && xbox.getA()){
					listener.exitScreen(this,1);
				} else if(levelSelectButton.enlarged && xbox.getA() && time > .2){
					levelSelectButton.resize("down");
					pageDirection = 1;
					resetTime();
				} else if (smallWindowButton.enlarged && xbox.getA()) {
					Gdx.graphics.setWindowedMode(1280, 720);
				}
				else if (largeWindowButton.enlarged && xbox.getA()) {
					Gdx.graphics.setWindowedMode(1920, 1080);
				} else if (volumeLowButton.enlarged  && xbox.getA() && time > .1) {
					resetTime();
					volumeBar.setValue(volumeBar.getValue() - volumeBar.getStepSize());
				} else if (volumeHighButton.enlarged && xbox.getA()&& time > .1){
					resetTime();
					volumeBar.setValue(volumeBar.getValue() + volumeBar.getStepSize());
				} else if (sensitivityLowButton.enlarged  && xbox.getA()&& time > .1) {
					resetTime();
				sensitivityBar.setValue(sensitivityBar.getValue() - sensitivityBar.getStepSize());
				} else if (sensitivityHighButton.enlarged && xbox.getA()&& time > .1){
					resetTime();
					sensitivityBar.setValue(sensitivityBar.getValue() + sensitivityBar.getStepSize());
				}
			}
		}

		return true;
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
		levelPage += pageDirection;
		pageDirection = 0;
		if (pressState %2 ==1) {
			if (xbox != null && !xbox.getB()) {
				pressState +=1;
				selectedLevel = "level" + Integer.toString(pressState/2);
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
	public static class ButtonBox {
		int label;
		Rectangle bounds;
		Texture texture;
		boolean enlarged;
		boolean available;
		LevelSelectMode mode;

		ButtonBox(int label, Rectangle bounds, Texture texture, LevelSelectMode mode) {
			this.label = label;
			this.bounds = bounds;
			this.texture = texture;
			this.enlarged = false;
			this.available = false;
			this.mode = mode;
		}

		public Texture getTexture() {return this.texture;}
		public Rectangle getBounds() {return this.bounds;}
		public int getLabel() {return this.label;}
		public boolean getEnlarged(){return this.enlarged;}

		public void hoveringButton(XBoxController xbox, float time, int size, Array<ButtonBox> levels, ButtonBox settings, ButtonBox exit, ButtonBox volumedown, ButtonBox volumeup, ButtonBox sensdown, ButtonBox sensup, ButtonBox resdown, ButtonBox resup){
			if (xbox == null) {
				int x = (int) (Gdx.input.getX() / scale);
				int y = (int) ((Gdx.graphics.getHeight() - Gdx.input.getY()) / scale);
				float centerX = this.bounds.x + this.bounds.width / 2;
				float centerY = this.bounds.y + this.bounds.height / 2;
				if (bounds.contains(x, y) && !this.enlarged && this.available) {
					this.enlarged = true;
					this.bounds.width = this.bounds.width * 8 / 7;
					this.bounds.height = this.bounds.height * 8 / 7;
					this.bounds.x = (int) centerX - this.bounds.width / 2;
					this.bounds.y = (int) centerY - this.bounds.height / 2;
				} else if (!bounds.contains(x, y) && this.enlarged) {
					this.enlarged = false;
					this.bounds.width = this.bounds.width * 7 / 8;
					this.bounds.height = this.bounds.height * 7 / 8;
					this.bounds.x = (int) centerX - this.bounds.width / 2;
					this.bounds.y = (int) centerY - this.bounds.height / 2;
				}
			}else{
				//System.out.println("Controller on");
				if (this.enlarged && time >.2f){
				float x = xbox.getLeftX();
//				System.out.println("got input");
				if (Math.abs(x)<.5){
					x = 0;
				}
				float y = xbox.getLeftY();
//				System.out.println("got input");
				if (Math.abs(y)<.5){
					y = 0;
				}
				try{
					if (mode.levelPage!=-1){
						if (y<0){
							this.resize("down");
							settings.resize("up");
							mode.resetTime();
//							if (levels.get(mode.levelPage*3 + 1).available){
//								levels.get(mode.levelPage*3 + 1).resize("up");
//								exit.resize("down");
//								mode.resetTime();
//							}else{
//								this.resize("down");
//								levels.get(mode.levelPage*3).resize("up");
//								mode.resetTime();
//							}
						}else if (y>0){
								this.resize("down");
								boolean second = false;
								boolean third = false;
								try{
									third = levels.get(mode.levelPage * 3 + 2).available;
								} catch (Exception e){}
								try{
									second = levels.get(mode.levelPage * 3 + 1).available;
								} catch (Exception e){}
								if (third) {
									levels.get(mode.levelPage * 3 + 2).resize("up");
									mode.resetTime();
								}
								else if (second){
									levels.get(mode.levelPage*3 + 1).resize("up");
									mode.resetTime();
								}else {
									levels.get(mode.levelPage*3).resize("up");
									mode.resetTime();
								}

		//					this.resize("down");
						}
						else if (x>0 && levels.get(this.label).available && !settings.enlarged&&!exit.enlarged){
		//					System.out.println("right");
							if (this.label < (mode.levelPage+1)*3){
								this.resize("down");
								levels.get(this.label).resize("up");
								mode.resetTime();
							}
						} else if (x<0&& !settings.enlarged&&!exit.enlarged) {
							if (this.label > (mode.levelPage*3)+1){
								if (levels.get(this.label-2).available){
									this.resize("down");
									levels.get(this.label-2).resize("up");
									mode.resetTime();
								}
							}
							}
					}else{
						switch (this.label){
							case -1:
								if (x>0){
									this.resize("down");
									exit.resize("up");
									mode.resetTime();
								} else if (y<0){
									this.resize("down");
									resup.resize("up");
									mode.resetTime();
								}
								break;
							case -3:
								if (x>0){
									this.resize("down");
									volumeup.resize("up");
									mode.resetTime();
								}else if (y>0){
									this.resize("down");
									sensdown.resize("up");
									mode.resetTime();
								}
								break;
							case -4:
								if (x<0){
									this.resize("down");
									volumedown.resize("up");
									mode.resetTime();
								}else if (y>0){
									this.resize("down");
									sensup.resize("up");
									mode.resetTime();
								}else if (x>0){
									this.resize("down");
									exit.resize("up");
									mode.resetTime();
								}
								break;
							case -5:
								if (x>0){
									this.resize("down");
									sensup.resize("up");
									mode.resetTime();
								}else if (y>0){
									this.resize("down");
									resdown.resize("up");
									mode.resetTime();
								}else if(y<0){
									this.resize("down");
									volumedown.resize("up");
									mode.resetTime();
								}
								break;
							case -6:
								if (x<0){
									this.resize("down");
									sensdown.resize("up");
									mode.resetTime();
								}else if (y>0){
									this.resize("down");
									resdown.resize("up");
									mode.resetTime();
								}else if(y<0){
									this.resize("down");
									volumeup.resize("up");
									mode.resetTime();
								}else if (x>0){
									this.resize("down");
									exit.resize("up");
									mode.resetTime();
								}
								break;
							case -7:
								if (x>0){
									this.resize("down");
									exit.resize("up");
									mode.resetTime();
								} else if (y>0){
									this.resize("down");
									resup.resize("up");
									mode.resetTime();
								}else if(y<0){
									this.resize("down");
									sensdown.resize("up");
									mode.resetTime();
								}
								break;
							case -8:
								if (x>0){
									this.resize("down");
									exit.resize("up");
									mode.resetTime();
								}else if (y>0){
									this.resize("down");
									mode.levelSelectButton.resize("up");
									mode.resetTime();
								}else if(y<0){
									this.resize("down");
									resdown.resize("up");
									mode.resetTime();
								}else if (x<0){
									this.resize("down");
									mode.levelSelectButton.resize("up");
									mode.resetTime();
								}
								break;
							case -9:
								if (x<0){
									this.resize("down");
									mode.levelSelectButton.resize("up");
									mode.resetTime();
								}
								else if (y<0){
									this.resize("down");
									sensup.resize("up");
									mode.resetTime();
								}
						}
					}
				}catch (Exception e){

				}
			}
			}
		}
		public void resize(String direction){
			float centerX = this.bounds.x + this.bounds.width / 2;
			float centerY = this.bounds.y + this.bounds.height / 2;
			if (direction.equals("up")){
				this.enlarged = true;
				this.bounds.width = this.bounds.width * 8 / 7;
				this.bounds.height = this.bounds.height * 8 / 7;
            }else{
				this.enlarged = false;
				this.bounds.width = this.bounds.width * 7 / 8;
				this.bounds.height = this.bounds.height * 7 / 8;
            }
            this.bounds.x = (int) centerX - this.bounds.width / 2;
            this.bounds.y = (int) centerY - this.bounds.height / 2;
        }
		public boolean isPressed(){
			int x = (int) (Gdx.input.getX()/scale);
			int y = (int) ((Gdx.graphics.getHeight()- Gdx.input.getY())/scale);
			return bounds.contains(x, y);
		}
	}
	public String scoreToTime(float score){
		int minutes = (int) score / 60;
		int seconds = (int) score % 60;
		int milliseconds = (int) ((score - (int)score) * 1000);

		return String.format("Time: %d'%02d\"%03d", minutes, seconds, milliseconds);
	}

	private class AssetLoader implements Runnable {
		Screen mode;
		GameMode gamemode;

		public AssetLoader (Screen mode, GameMode gamemode){
			this.mode= mode;
			this.gamemode = gamemode;
		}

		@Override
		public void run() {
			// Load assets here (e.g., textures, sounds, etc.)
			// This code will run in a separate thread

			// Simulate loading time
			if (xbox == null){
				gamemode.loadLevel(getLevel(),game);

				// Assets loaded, switch to the main game screen
				listener.exitScreen(mode,0);
			}

		}
	}
	private void processLoad(){
		time += Gdx.graphics.getDeltaTime();
		int frame = (loadAnimation == null ? 11 : loadAnimation.getFrame());
		if (loadAnimation != null) {
			if (time >= .25) {
				frame++;
				time = 0f;
				if (frame >= loadAnimation.getSize())
					frame = 0;
				loadAnimation.setFrame(frame);
			}
		}
	}
	public void drawload(){
//		canvas.begin();
		canvas.drawBackground(loadingscreen,0,0,true);
		font.setColor(Color.BLACK);
		canvas.drawText("Loading...",font,1700*scale,150*scale);
//		canvas.end();
	}
	public void drawloadSneaky(){
		canvas.drawBackgroundLOAD(loadingscreen,0,0,true);
		font.setColor(Color.BLACK);
		canvas.drawTextLOAD("Loading...",font,1700*scale,150*scale);
	}
}