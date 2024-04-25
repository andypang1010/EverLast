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
	private float scale;
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
	int numberOfLevels;
	int levelPage;
	/**
	 * pageDirection = 1 means go to right, = -1 means go to left, 0 means go nowhere
	 */
	int pageDirection;

	public String getLevel(){
		return selectedLevel;
	}
	public void resetPressState(){pressState = 0;}


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

		// Load the direction button
		// TODO: Change Scale?
		float scale = 8/7f;

		forwardButton = assets.getEntry("forwardButton", Texture.class);
		forward = new ButtonBox(0, scale,
				new Rectangle(canvas.getWidth() * 85 / 100, canvas.getHeight() * 2/15, forwardButton.getWidth(), forwardButton.getHeight()), forwardButton);
		backwardButton = assets.getEntry("backwardButton", Texture.class);
		backward = new ButtonBox(0, scale,
				new Rectangle(canvas.getWidth() * 6 / 100, canvas.getHeight() * 2/15, backwardButton.getWidth(), backwardButton.getHeight()), backwardButton);

		// Load the level button
		// Label of each button represents the level number
		numberOfLevels = 5; // Number of levels
		levelPage = 0;
		pageDirection = 0;
		Texture button1 = assets.getEntry("level1", Texture.class);
		ButtonBox level1Button = new ButtonBox(1, scale,
				new Rectangle(canvas.getWidth() * 5 / 100, canvas.getHeight() * 5/18, button1.getWidth(), button1.getHeight()), button1);

		Texture button2 = assets.getEntry("level2", Texture.class);
		ButtonBox level2Button = new ButtonBox(2, scale,
				new Rectangle(canvas.getWidth() * 33 / 100, canvas.getHeight() * 5/18, button2.getWidth(), button2.getHeight()), button2);

		Texture button3 = assets.getEntry("level3", Texture.class);
		ButtonBox level3Button = new ButtonBox(3, scale,
				new Rectangle(canvas.getWidth() * 58 / 100, canvas.getHeight() * 5/18, button3.getWidth(), button3.getHeight()), button3);

		// TODO: Change scale when we have actual button for level 4, 5
		Texture button4 = assets.getEntry("level4", Texture.class);
		ButtonBox level4Button = new ButtonBox(4, scale,
				new Rectangle(canvas.getWidth() * 5 / 100, canvas.getHeight() * 5/18, button4.getWidth(), button4.getHeight()), button4);

		Texture button5 = assets.getEntry("level5", Texture.class);
		ButtonBox level5Button = new ButtonBox(5, scale,
				new Rectangle(canvas.getWidth() * 33 / 100, canvas.getHeight() * 5/18, button5.getWidth(), button5.getHeight()), button5);

		levels = new Array<>(numberOfLevels);
		levels.add(level1Button);
		levels.add(level2Button);
		levels.add(level3Button);
		levels.add(level4Button);
		levels.add(level5Button);

		// Break up the status bar texture into regions
		font = assets.getEntry("font", BitmapFont.class);
		pressState = 0;

		active = true;

		// Let ANY connected controller start the game.
		for (XBoxController controller : Controllers.get().getXBoxControllers()) {
			controller.addListener(this);
			xbox = controller;
		}
		if (xbox != null){
			// TODO: Support XBox
//			level1.enlarged=true;
//			level1.fontScale = 1.25f;
		}
		time = 0;
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
				forward.hoveringButton();
				bounds = forward.getBounds();
				canvas.draw(forward.getTexture(), bounds.x, bounds.y, bounds.getWidth(), bounds.getHeight());
			} else if (levelPage == (numberOfLevels - 1)/3) {
				backward.hoveringButton();
				bounds = backward.getBounds();
				canvas.draw(backward.getTexture(), bounds.x, bounds.y, bounds.getWidth(), bounds.getHeight());
			} else {
				forward.hoveringButton();
				bounds = forward.getBounds();
				canvas.draw(forward.getTexture(), bounds.x, bounds.y, bounds.getWidth(), bounds.getHeight());
				backward.hoveringButton();
				bounds = backward.getBounds();
				canvas.draw(backward.getTexture(), bounds.x, bounds.y, bounds.getWidth(), bounds.getHeight());
			}
			for(ButtonBox button : levels) {
				int level = button.getLabel();
				if(levelPage * 3 < level && level <= (levelPage + 1) * 3){
					button.hoveringButton();
					bounds = button.getBounds();
					canvas.draw(button.getTexture(), bounds.x, bounds.y, bounds.getWidth(), bounds.getHeight());
				}
			}
		} else{
			// TODO: Support XBox
			if (levelPage == 0){
				forward.hoveringButton();
				bounds = forward.getBounds();
				canvas.draw(forward.getTexture(), bounds.x, bounds.y, bounds.getWidth(), bounds.getHeight());
			} else if (levelPage == (numberOfLevels - 1)/3) {
				backward.hoveringButton();
				bounds = backward.getBounds();
				canvas.draw(backward.getTexture(), bounds.x, bounds.y, bounds.getWidth(), bounds.getHeight());
			} else {
				forward.hoveringButton();
				bounds = forward.getBounds();
				canvas.draw(forward.getTexture(), bounds.x, bounds.y, bounds.getWidth(), bounds.getHeight());
				backward.hoveringButton();
				bounds = backward.getBounds();
				canvas.draw(backward.getTexture(), bounds.x, bounds.y, bounds.getWidth(), bounds.getHeight());
			}
//			for (LevelBox levelBox: levelBoxes){
//				levelBox.font.setColor(pressState == levelBox.label*2-1 ? Color.GRAY : Color.DARK_GRAY);
//				levelBox.font.getData().setScale(levelBox.fontScale*scale);
//				canvas.drawText("level " + Integer.toString(levelBox.label), levelBox.font, levelBox.bounds.x*sx,levelBox.enlarged ? (levelBox.bounds.y+levelBox.glyph.height *1.25f)*sy : (levelBox.bounds.y+levelBox.glyph.height)*sy );
			for(ButtonBox button : levels) {
				int level = button.getLabel();
				if(levelPage * 3 < level && level <= (levelPage + 1) * 3){
					button.hoveringButton();
					bounds = button.getBounds();
					canvas.draw(button.getTexture(), bounds.x, bounds.y, bounds.getWidth(), bounds.getHeight());
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
		Gdx.input.setInputProcessor(this);
		inputController.readInput(null,null);
		if(inputController.didDecelerate()){
			//Gdx.graphics.setWindowedMode(1280, 720);
		}else if(inputController.didBoost()){
			//Gdx.graphics.setWindowedMode(1920, 1080);
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
			if (isReady() && listener != null) {
				listener.exitScreen(this, 0);
			}
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
			int level = levelButton.getLabel();
			if(levelButton.isPressed() && levelPage * 3 < level && level <= (levelPage + 1) * 3){
				pressState = levelButton.getLabel() * 2 - 1;
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
}