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
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.redpacts.frostpurge.game.util.Controllers;
import com.redpacts.frostpurge.game.util.ScreenListener;
import com.redpacts.frostpurge.game.util.XBoxController;
import com.redpacts.frostpurge.game.views.GameCanvas;
import com.badlogic.gdx.math.Rectangle;

import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.List;

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
	private int initialWidth  = 1280;
	/** Standard window height (for scaling) */
	private int initialHeight = 720;
	/**
	 * The actual assets to be loaded
	 */
	private AssetDirectory assets;

	/**
	 * Background texture for level-select
	 */
	private Texture background;
	/**
	 * Texture for forward button
	 */
	private Texture forwardButton;
	private ButtonBox forward;
	/**
	 * Texture for backward button
	 */
	private Texture backwardButton;
	private ButtonBox backward;
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
	private XBoxController xbox;
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


	public String getLevel(){
		return selectedLevel;
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
	public LevelSelectMode(GameCanvas canvas) {
		this.canvas = canvas;
		inputController = new InputController();

		// Compute the dimensions from the canvas
		resize(canvas.getWidth(), canvas.getHeight());
		initialWidth = canvas.getWidth();
		initialHeight = canvas.getHeight();

		// We need these files loaded immediately
		assets = new AssetDirectory( "levelselect.json" );
		assets.loadAssets();
		assets.finishLoading();

		// Load the background
		background = assets.getEntry("background", Texture.class);
		background.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		emptyStars = assets.getEntry("emptystars",Texture.class);
		stars = new TextureRegion(assets.getEntry("filledstars",Texture.class));
		lock = assets.getEntry("lock",Texture.class);
		// Load the direction button
		forwardButton = assets.getEntry("forwardButton", Texture.class);
		forward = new ButtonBox(0,
				new Rectangle(canvas.getWidth() * 85 / 100, canvas.getHeight() * 2/15, forwardButton.getWidth(), forwardButton.getHeight()), forwardButton, this);
		backwardButton = assets.getEntry("backwardButton", Texture.class);
		backward = new ButtonBox(0,
				new Rectangle(canvas.getWidth() * 6 / 100, canvas.getHeight() * 2/15, backwardButton.getWidth(), backwardButton.getHeight()), backwardButton, this);

		// Load the level button
		// Label of each button represents the level number
		numberOfLevels = 5; // Number of levels
		levelPage = 0;
		pageDirection = 0;
		Texture button1 = assets.getEntry("level1", Texture.class);
		ButtonBox level1Button = new ButtonBox(1,
				new Rectangle(canvas.getWidth() * 5 / 100, canvas.getHeight() * 5/18, button1.getWidth(), button1.getHeight()), button1, this);

		Texture button2 = assets.getEntry("level2", Texture.class);
		ButtonBox level2Button = new ButtonBox(2,
				new Rectangle(canvas.getWidth() * 33 / 100, canvas.getHeight() * 5/18, button2.getWidth(), button2.getHeight()), button2, this);

		Texture button3 = assets.getEntry("level3", Texture.class);
		ButtonBox level3Button = new ButtonBox(3,
				new Rectangle(canvas.getWidth() * 58 / 100, canvas.getHeight() * 5/18, button3.getWidth(), button3.getHeight()), button3, this);

		// TODO: Change scale when we have actual button for level 4, 5
		Texture button4 = assets.getEntry("level4", Texture.class);
		ButtonBox level4Button = new ButtonBox(4,
				new Rectangle(canvas.getWidth() * 5 / 100, canvas.getHeight() * 5/18, button4.getWidth(), button4.getHeight()), button4, this);

		Texture button5 = assets.getEntry("level5", Texture.class);
		ButtonBox level5Button = new ButtonBox(5,
				new Rectangle(canvas.getWidth() * 33 / 100, canvas.getHeight() * 5/18, button5.getWidth(), button5.getHeight()), button5, this);

		levels = new Array<>(numberOfLevels);
		levels.add(level1Button);
		levels.add(level2Button);
		levels.add(level3Button);
		levels.add(level4Button);
		levels.add(level5Button);

		// Break up the status bar texture into regions
		font = assets.getEntry("font", BitmapFont.class);
		pressState = 0;

		Gdx.input.setInputProcessor(this);
		active = true;

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
		canvas.drawBackground(background, 0, 0, true);
		Rectangle bounds;
		if (xbox==null){

//			for (LevelBox levelBox: levelBoxes){
//				levelBox.font.setColor(pressState == levelBox.label*2-1 ? Color.GRAY : Color.DARK_GRAY);
//				hoveringBox(levelBox);
//				font.getData().setScale(levelBox.fontScale*scale);
//				canvas.drawText("level " + Integer.toString(levelBox.label), levelBox.font, levelBox.bounds.x*sx,levelBox.enlarged ? levelBox.bounds.y*sy+levelBox.glyph.height*scale*1.25f : levelBox.bounds.y*sy+levelBox.glyph.height*scale);
			if (levelPage == 0){
				forward.hoveringButton(null,time,levels.size, levels);
				bounds = forward.getBounds();
				canvas.draw(forward.getTexture(), bounds.x*scale, bounds.y*scale, bounds.getWidth()*scale, bounds.getHeight()*scale);
			} else if (levelPage == (numberOfLevels - 1)/3) {
				backward.hoveringButton(null,time,levels.size, levels);
				bounds = backward.getBounds();
				canvas.draw(backward.getTexture(), bounds.x*scale, bounds.y*scale, bounds.getWidth()*scale, bounds.getHeight()*scale);
			} else {
				forward.hoveringButton(null,time,levels.size, levels);
				bounds = forward.getBounds();
				canvas.draw(forward.getTexture(), bounds.x*scale, bounds.y*scale, bounds.getWidth()*scale, bounds.getHeight()*scale);
				backward.hoveringButton(null,time,levels.size, levels);
				bounds = backward.getBounds();
				canvas.draw(backward.getTexture(), bounds.x*scale, bounds.y*scale, bounds.getWidth()*scale, bounds.getHeight()*scale);
			}
			for(ButtonBox button : levels) {
				int level = button.label;
				if(levelPage * 3 < level && level <= (levelPage + 1) * 3){
					button.hoveringButton(null,time,levels.size, levels);
					bounds = button.getBounds();
					canvas.draw(button.getTexture(), bounds.x*scale, bounds.y*scale, bounds.getWidth()*scale, bounds.getHeight()*scale);
					canvas.drawCentered(emptyStars, (bounds.x+bounds.width/2)*scale, 5*bounds.y*scale/6,emptyStars.getWidth(),emptyStars.getHeight());
					float ratio = (float) game.getScore("level" + Integer.toString(button.label)) /2000;
					if (ratio>1){
						ratio = 1;
					}
					if (ratio!=0){
						stars.setRegionWidth((int) (stars.getRegionWidth()*ratio));
						canvas.drawCentered(stars, (bounds.x+bounds.width/2)*scale, 5*bounds.y*scale/6, (float) stars.getRegionWidth(),stars.getRegionHeight());
						stars.setRegionWidth((emptyStars.getWidth()));
					}
					if (!button.available){
						canvas.draw(lock, bounds.x*scale,bounds.y*scale,lock.getWidth()*scale,lock.getHeight()*scale);
					}
				}
			}
		} else{
			// TODO: Support XBox
			if (levelPage == 0){
				forward.hoveringButton(xbox,time,levels.size, levels);
				bounds = forward.getBounds();
				canvas.draw(forward.getTexture(), bounds.x*scale, bounds.y*scale, bounds.getWidth()*scale, bounds.getHeight()*scale);
			} else if (levelPage == (numberOfLevels - 1)/3) {
				backward.hoveringButton(xbox,time,levels.size, levels);
				bounds = backward.getBounds();
				canvas.draw(backward.getTexture(), bounds.x*scale, bounds.y*scale, bounds.getWidth()*scale, bounds.getHeight()*scale);
			} else {
				forward.hoveringButton(xbox,time,levels.size, levels);
				bounds = forward.getBounds();
				canvas.draw(forward.getTexture(), bounds.x*scale, bounds.y*scale, bounds.getWidth()*scale, bounds.getHeight()*scale);
				backward.hoveringButton(xbox,time,levels.size, levels);
				bounds = backward.getBounds();
				canvas.draw(backward.getTexture(), bounds.x*scale, bounds.y*scale, bounds.getWidth()*scale, bounds.getHeight()*scale);
			}
//			for (LevelBox levelBox: levelBoxes){
//				levelBox.font.setColor(pressState == levelBox.label*2-1 ? Color.GRAY : Color.DARK_GRAY);
//				levelBox.font.getData().setScale(levelBox.fontScale*scale);
//				canvas.drawText("level " + Integer.toString(levelBox.label), levelBox.font, levelBox.bounds.x*sx,levelBox.enlarged ? (levelBox.bounds.y+levelBox.glyph.height *1.25f)*sy : (levelBox.bounds.y+levelBox.glyph.height)*sy );
			for(ButtonBox button : levels) {
				int level = button.label;
				if(levelPage * 3 < level && level <= (levelPage + 1) * 3){
					button.hoveringButton(xbox,time, levels.size, levels);
					bounds = button.getBounds();
					canvas.draw(button.getTexture(), bounds.x*scale, bounds.y*scale, bounds.getWidth()*scale, bounds.getHeight()*scale);
					canvas.drawCentered(emptyStars, (bounds.x+bounds.width/2)*scale, 5*bounds.y*scale/6,emptyStars.getWidth(),emptyStars.getHeight());
					float ratio = (float) game.getScore("level" + Integer.toString(button.label)) /2000;
					if (ratio>1){
						ratio = 1;
					}
					if (ratio!=0){
						stars.setRegionWidth((int) (stars.getRegionWidth()*ratio));
						canvas.drawCentered(stars, (bounds.x+bounds.width/2)*scale, 5*bounds.y*scale/6, (float) stars.getRegionWidth(),stars.getRegionHeight());
						stars.setRegionWidth((emptyStars.getWidth()));
					}
					if (!button.available){
						canvas.draw(lock, bounds.x*scale,bounds.y*scale,lock.getWidth()*scale,lock.getHeight()*scale);
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
		inputController.readInput(null,null);
		if(inputController.didDecelerate()){
			Gdx.graphics.setWindowedMode(1280, 720);
		}else if(inputController.didBoost()){
			Gdx.graphics.setWindowedMode(1920, 1080);
		}
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
				listener.exitScreen(this, 0);
			}
		}
		//game.update(assets.getEntry("savedata", JsonValue.class));
		for (ButtonBox button: levels){
			button.available = game.getUnlockStatus("level"+Integer.toString(button.label));
		}

	}

	@Override
	public void resize(int i, int i1) {
		sx = ((float)canvas.getWidth())/initialWidth;
		sy = ((float)canvas.getHeight())/initialHeight;
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

		if(forward.isPressed() && levelPage < (numberOfLevels - 1)/3 ){
			pageDirection = 1;
		} else if(backward.isPressed() && levelPage > 0){
			pageDirection = -1;
		}
		for(ButtonBox levelButton: levels){
			int level = levelButton.label;
			if(levelButton.isPressed() && levelPage * 3 < level && level <= (levelPage + 1) * 3 && levelButton.available){
				pressState = levelButton.label * 2 - 1;
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
			if (xbox.getDPadRight() && levelPage < (numberOfLevels - 1) / 3) {
				pageDirection = 1;
				for (int i = 0; i < 3; i++) {
					try {
						if (levels.get(levelPage * 3 + i).enlarged){
							levels.get(levelPage * 3 + i).resize("down");
						}
					} catch (Exception e) {

					}
				}
				System.out.println(levels.get((levelPage + pageDirection) * 3).enlarged);
				if (levels.get((levelPage + pageDirection) * 3).available){
					System.out.println("unlocked");
					levels.get((levelPage + pageDirection) * 3).enlarged = true;
				}

			} else if (xbox.getDPadLeft() && levelPage > 0) {
				pageDirection = -1;
				for (int i = 0; i < 3; i++) {
					try {
						if (levels.get(levelPage * 3 + i).enlarged){
							levels.get(levelPage * 3 + i).resize("down");
						}
					} catch (Exception e) {

					}
				}
				if (levels.get((levelPage + pageDirection) * 3 + 2).available){
					levels.get((levelPage + pageDirection) * 3 + 2).resize("up");
				} else if (levels.get((levelPage + pageDirection) * 3 + 1).available) {
					levels.get((levelPage + pageDirection) * 3 + 1).resize("up");
				}else if (levels.get((levelPage + pageDirection) * 3).available) {
					levels.get((levelPage + pageDirection) * 3).resize("up");
				}
			}
		}
		if (xbox != null) {
			for (ButtonBox levelButton : levels) {
				int level = levelButton.label;
				if (levelButton.enlarged && levelPage * 3 < level && level <= (levelPage + 1) * 3 && levelButton.available && xbox.getStart()) {
					pressState = levelButton.label * 2 - 1;
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

//	public void hoveringBox(LevelBox levelBox) {
//		if (xbox ==null){
//			int x = Gdx.input.getX();
//			int y = Gdx.graphics.getHeight() - Gdx.input.getY();
//			if (levelBox.bounds.contains(x/sx, y/sy) && pressState != levelBox.label*2-1){
//				levelBox.font.setColor(Color.BLACK); // Change color if hovering
//				if (!levelBox.enlarged){
//					levelBox.enlarged = true;
//					levelBox.fontScale = 1.25f;
//					levelBox.resize("up");
//				}
//			}else if (levelBox.enlarged && !levelBox.bounds.contains(x/sx, y/sy)){
//				levelBox.enlarged = false;
//				levelBox.fontScale = 1f;
//				levelBox.resize("down");
//			}
//			levelBox.font.getData().setScale(levelBox.fontScale);
//		} else{
//			if (levelBox.enlarged && time >.2f){
//				float x = xbox.getLeftX();
//				if (Math.abs(x)<.5){
//					x = 0;
//				}
//				if (x>0){
////					System.out.println("right");
//					if (levelBox.label < levelBoxes.size()){
//						levelBox.enlarged = false;
//						levelBox.resize("down");
//						levelBox.fontScale = 1;
//						levelBoxes.get(levelBox.label).enlarged = true;
//						levelBoxes.get(levelBox.label).resize("up");
//						levelBoxes.get(levelBox.label).fontScale = 1.25f;
//						time =0;
//					}
//				} else if (x<0) {
////					System.out.println("left");
//					if (levelBox.label > 1){
//						levelBox.enlarged = false;
//						levelBox.resize("down");
//						levelBox.fontScale = 1;
//						levelBoxes.get(levelBox.label-2).enlarged = true;
//						levelBoxes.get(levelBox.label-2).resize("up");
//						levelBoxes.get(levelBox.label-2).fontScale = 1.25f;
//						time =0;
//					}
//				}
//				levelBox.font.getData().setScale(levelBox.fontScale);
//			}
//		}
//	}

	//private static class LevelBox {
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

		public void hoveringButton(XBoxController xbox, float time, int size,Array<ButtonBox> levels){
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
				if (x>0 && levels.get(this.label).available){
//					System.out.println("right");
					if (this.label < (mode.levelPage+1)*3){
						this.resize("down");
						levels.get(this.label).resize("up");
						mode.resetTime();
					}
				} else if (x<0) {
					if (this.label > (mode.levelPage*3)+1){
						if (levels.get(this.label-2).available){
							this.resize("down");
							levels.get(this.label-2).resize("up");
							mode.resetTime();
						}
					}
//					System.out.println("left");
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
}