package com.redpacts.frostpurge.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class GameCanvas {
    /** While we are not drawing polygons (yet), this spritebatch is more reliable */
    private PolygonSpriteBatch spriteBatch;

    /** Rendering context for the debug outlines */
    private ShapeRenderer debugRender;

    /** Track whether or not we are active (for error checking) */
    private DrawPass active;

    /** The current color blending mode */
    private BlendState blend;

    /** Value to cache window width (if we are currently full screen) */
    int width;
    /** Value to cache window height (if we are currently full screen) */
    int height;

    // CACHE OBJECTS
    /** Affine cache for current sprite to draw */
    private Affine2 local;
    /** Cache object to unify everything under a master draw method */
    private TextureRegion holder;
    /** Cache object to render shape for geometric shape (like vision cone). */
    private ShapeRenderer renderer;

    private Vector2 vertex;

    private Vector3 screen;
    private Vector3 world;

    private Affine2 global;
    private Texture coneTexture;
    /**
     * Creates a new GameCanvas determined by the application configuration.
     *
     * Width, height, and fullscreen are taken from the LWGJApplicationConfig
     * object used to start the application.  This constructor initializes all
     * of the necessary graphics objects.
     */
    public GameCanvas() {
        active = DrawPass.INACTIVE;
        spriteBatch = new PolygonSpriteBatch();
        debugRender = new ShapeRenderer();

        // Set the projection matrix (for proper scaling)
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, getWidth(), getHeight());
        debugRender.getProjectionMatrix().setToOrtho2D(0, 0, getWidth(), getHeight());
        // Initialize the cache objects
        local  = new Affine2();
        holder = new TextureRegion();
        renderer = new ShapeRenderer();

        screen = new Vector3();
        world = new Vector3();
        vertex = new Vector2();

        int BLANK_SIZE = 1;
        Pixmap map = new Pixmap(BLANK_SIZE,BLANK_SIZE,Pixmap.Format.RGBA4444);
        map.setColor(Color.RED);
        map.fillRectangle(0, 0, BLANK_SIZE, BLANK_SIZE);
        coneTexture = new Texture(map);
    }
    /**
     * Center the camera around the player
     * @param camera The camera object to move
     */
    public void center(OrthographicCamera camera, float x, float y){
        camera.position.set(x, y, 0);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
    }

    /**
     * Eliminate any resources that should be garbage collected manually.
     */
    public void dispose() {
        active = DrawPass.INACTIVE;

        if (active != DrawPass.INACTIVE) {
            Gdx.app.error("GameCanvas", "Cannot dispose while drawing active", new IllegalStateException());
            return;
        }
        spriteBatch.dispose();
        spriteBatch = null;
        local  = null;
        holder = null;
        vertex = null;
    }

    /**
     * Returns the width of this canvas
     *
     * This currently gets its value from Gdx.graphics.getWidth()
     *
     * @return the width of this canvas
     */
    public int getWidth() {
        return Gdx.graphics.getWidth();
    }

    /**
     * Changes the width of this canvas
     *
     * This method raises an IllegalStateException if called while drawing is
     * active (e.g. in-between a begin-end pair).
     *
     * @param width the canvas width
     */
    public void setWidth(int width) {
        if (active != DrawPass.INACTIVE) {
            Gdx.app.error("GameCanvas", "Cannot alter property while drawing active", new IllegalStateException());
            return;
        }
        this.width = width;
        if (!isFullscreen()) {
            Gdx.graphics.setWindowedMode(width, getHeight());
        }
        resize();
    }

    /**
     * Returns the height of this canvas
     *
     * This currently gets its value from Gdx.graphics.getHeight()
     *
     * @return the height of this canvas
     */
    public int getHeight() {
        return Gdx.graphics.getHeight();
    }

    /**
     * Changes the height of this canvas
     *
     * This method raises an IllegalStateException if called while drawing is
     * active (e.g. in-between a begin-end pair).
     *
     * @param height the canvas height
     */
    public void setHeight(int height) {
        if (active != DrawPass.INACTIVE) {
            Gdx.app.error("GameCanvas", "Cannot alter property while drawing active", new IllegalStateException());
            return;
        }
        this.height = height;
        if (!isFullscreen()) {
            Gdx.graphics.setWindowedMode(getWidth(), height);
        }
        resize();
    }

    /**
     * Returns the dimensions of this canvas
     *
     * @return the dimensions of this canvas
     */
    public Vector2 getSize() {
        return new Vector2(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
    }

    /**
     * Changes the width and height of this canvas
     *
     * This method raises an IllegalStateException if called while drawing is
     * active (e.g. in-between a begin-end pair).
     *
     * @param width the canvas width
     * @param height the canvas height
     */
    public void setSize(int width, int height) {
        if (active != DrawPass.INACTIVE) {
            Gdx.app.error("GameCanvas", "Cannot alter property while drawing active", new IllegalStateException());
            return;
        }
        this.width = width;
        this.height = height;
        if (!isFullscreen()) {
            Gdx.graphics.setWindowedMode(width, height);
        }
        resize();
    }

    /**
     * Returns whether this canvas is currently fullscreen.
     *
     * @return whether this canvas is currently fullscreen.
     */
    public boolean isFullscreen() {
        return Gdx.graphics.isFullscreen();
    }

    /**
     * Sets whether or not this canvas should change to fullscreen.
     *
     * If desktop is true, it will use the current desktop resolution for
     * fullscreen, and not the width and height set in the configuration
     * object at the start of the application. This parameter has no effect
     * if fullscreen is false.
     *
     * This method raises an IllegalStateException if called while drawing is
     * active (e.g. in-between a begin-end pair).
     *
     * @param value Whether this canvas should change to fullscreen.
     */
    public void setFullscreen(boolean value) {
        if (active != DrawPass.INACTIVE) {
            Gdx.app.error("GameCanvas", "Cannot alter property while drawing active", new IllegalStateException());
            return;
        }
        if (value) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(width, height);
        }
    }

    /**
     * Resets the SpriteBatch camera when this canvas is resized.
     *
     * If you do not call this when the window is resized, you will get
     * weird scaling issues.
     */
    public void resize() {
        // Resizing screws up the spriteBatch projection matrix
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, getWidth(), getHeight());
    }

    /**
     * Returns the current color blending state for this canvas.
     *
     * Textures draw to this canvas will be composited according
     * to the rules of this blend state.
     *
     * @return the current color blending state for this canvas
     */
    public BlendState getBlendState() {
        return blend;
    }

    /**
     * Sets the color blending state for this canvas.
     *
     * Any texture draw subsequent to this call will use the rules of this blend
     * state to composite with other textures.  Unlike the other setters, if it is
     * perfectly safe to use this setter while  drawing is active (e.g. in-between
     * a begin-end pair).
     *
     * @param state the color blending rule
     */
    public void setBlendState(BlendState state) {
        if (state == blend) {
            return;
        }
        switch (state) {
            case NO_PREMULT:
                spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
                break;
            case ALPHA_BLEND:
                spriteBatch.setBlendFunction(GL20.GL_ONE,GL20.GL_ONE_MINUS_SRC_ALPHA);
                break;
            case ADDITIVE:
                spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA,GL20.GL_ONE);
                break;
            case OPAQUE:
                spriteBatch.setBlendFunction(GL20.GL_ONE,GL20.GL_ZERO);
                break;
        }
        blend = state;
    }

    /**
     * Start and active drawing sequence with the identity transform.
     *
     * Nothing is flushed to the graphics card until the method end() is called.
     */
    public void begin() {
        spriteBatch.begin();
        active = DrawPass.STANDARD;

        // Clear the screen
        Gdx.gl.glClearColor(1f, 1f, 1f, 1.0f);  // Homage to the XNA years
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /**
     * Ends a drawing sequence, flushing textures to the graphics card.
     */
    public void end() {
        spriteBatch.end();
        active = DrawPass.INACTIVE;
    }

    /**
     * Draw the seamless background image.
     *
     * The background image is drawn (with NO SCALING) at position x, y.  Width-wise,
     * the image is seamlessly scrolled; when we reach the image we draw a second copy.
     *
     * To work properly, the image should be wide and high enough to fill the screen.
     *
     * @param image  Texture to draw as an overlay
     * @param x      The x-coordinate of the bottom left corner
     * @param y 	 The y-coordinate of the bottom left corner
     */
    public void drawBackground(Texture image, float x, float y, boolean fill) {
        float w, h;
        if (fill) {
            w = getWidth();
            h = getHeight();
        } else {
            w = image.getWidth();
            h = image.getHeight();
        }
        spriteBatch.setColor(Color.WHITE);
        spriteBatch.draw(image, x, y, w, h);
    }

    /**
     * Draws the tinted texture at the given position.
     * <p>
     * The texture colors will be multiplied by the given color.  This will turn
     * any white into the given color.  Other colors will be similarly affected.
     * <p>
     * Unless otherwise transformed by the global transform (@see begin(Affine2)),
     * the texture will be unscaled.  The bottom left of the texture will be positioned
     * at the given coordinates.
     *
     * @param image         The texture to draw
     * @param obstacleColor
     * @param x             The x-coordinate of the bottom left corner
     * @param y             The y-coordinate of the bottom left corner
     * @param sx
     * @param sy
     * @param i
     * @param scale
     * @param v
     */
    public void draw(Texture image, Color obstacleColor, float x, float y, float sx, float sy, int i, float scale, float v) {
        if (active != DrawPass.STANDARD) {
            Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
            return;
        }

        // Unlike Lab 1, we can shortcut without a master drawing method
        spriteBatch.setColor(Color.WHITE);
        spriteBatch.draw(image, x,  y);
    }

    public void draw(Texture image, float x, float y, float width, float height) {
        if (active != DrawPass.STANDARD) {
            Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
            return;
        }

        // Unlike Lab 1, we can shortcut without a master drawing method
        spriteBatch.setColor(Color.WHITE);
        spriteBatch.draw(image, x,  y, width, height);
    }

    /**
     * Draws the tinted texture with the given transformations
     *
     * The texture colors will be multiplied by the given color.  This will turn
     * any white into the given color.  Other colors will be similarly affected.
     *
     * The transformations are BEFORE after the global transform (@see begin(Affine2)).
     * As a result, the specified texture origin will be applied to all transforms
     * (both the local and global).
     *
     * The local transformations in this method are applied in the following order:
     * scaling, then rotation, then translation (e.g. placement at (sx,sy)).
     *
     * @param image The texture to draw
     * @param tint  The color tint
     * @param ox 	The x-coordinate of texture origin (in pixels)
     * @param oy 	The y-coordinate of texture origin (in pixels)
     * @param x 	The x-coordinate of the texture origin
     * @param y 	The y-coordinate of the texture origin
     * @param angle The rotation angle (in degrees) about the origin.
     * @param sx 	The x-axis scaling factor
     * @param sy 	The y-axis scaling factor
     */
    public void draw(Texture image, Color tint, float ox, float oy,
                     float x, float y, float angle, float sx, float sy,boolean flip) {
        if (active != DrawPass.STANDARD) {
            Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
            return;
        }

        // Call the master drawing method (we have to for transforms)
        holder.setRegion(image);
        draw(holder,tint,ox,oy,x,y,angle,sx,sy,flip);
    }

    /**
     * Draws the tinted texture region (filmstrip) at the given position.
     *
     * A texture region is a single texture file that can hold one or more textures.
     * It is used for filmstrip animation.
     *
     * The texture colors will be multiplied by the given color.  This will turn
     * any white into the given color.  Other colors will be similarly affected.
     *
     * Unless otherwise transformed by the global transform (@see begin(Affine2)),
     * the texture will be unscaled.  The bottom left of the texture will be positioned
     * at the given coordinates.
     *
     * @param region The texture to draw
     * @param x 	The x-coordinate of the bottom left corner
     * @param y 	The y-coordinate of the bottom left corner
     */
    public void draw(TextureRegion region, float x, float y) {
        if (active != DrawPass.STANDARD) {
            Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
            return;
        }

        // Unlike Lab 1, we can shortcut without a master drawing method
        spriteBatch.setColor(Color.WHITE);
        if (region == null){
            System.out.println("NULL");
        }
        spriteBatch.draw(region, x,  y);
    }

    public void draw(TextureRegion region, float x, float y, float width, float height) {
        if (active != DrawPass.STANDARD) {
            Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
            return;
        }

        // Unlike Lab 1, we can shortcut without a master drawing method
        spriteBatch.setColor(Color.WHITE);
        spriteBatch.draw(region, x,  y, width, height);
    }

    /**
     * Draws the tinted texture region (filmstrip) with the given transformations
     *
     * THIS IS THE MASTER DRAW METHOD (Modify for exercise 4)
     *
     * A texture region is a single texture file that can hold one or more textures.
     * It is used for filmstrip animation.
     *
     * The texture colors will be multiplied by the given color.  This will turn
     * any white into the given color.  Other colors will be similarly affected.
     *
     * The transformations are BEFORE after the global transform (@see begin(Affine2)).
     * As a result, the specified texture origin will be applied to all transforms
     * (both the local and global).
     *
     * The local transformations in this method are applied in the following order:
     * scaling, then rotation, then translation (e.g. placement at (sx,sy)).
     *
     * @param region The texture to draw
     * @param tint  The color tint
     * @param ox 	The x-coordinate of texture origin (in pixels)
     * @param oy 	The y-coordinate of texture origin (in pixels)
     * @param x 	The x-coordinate of the texture origin
     * @param y 	The y-coordinate of the texture origin
     * @param angle The rotation angle (in degrees) about the origin.
     * @param sx 	The x-axis scaling factor
     * @param sy 	The y-axis scaling factor
     */
    public void draw(TextureRegion region, Color tint, float ox, float oy,
                     float x, float y, float angle, float sx, float sy, boolean flip) {
        if (active != DrawPass.STANDARD) {
            Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
            return;
        }

        computeTransform(ox,oy,x,y,angle,sx,sy);
        spriteBatch.setColor(tint);
        if (flip){
            region.flip(true, false);
        }
        spriteBatch.draw(region,region.getRegionWidth(),region.getRegionHeight(),local);
        if (flip){
            region.flip(true, false);
        }

    }


    /**
     * Compute the affine transform (and store it in local) for this image.
     *
     * This helper is meant to simplify all of the math in the above draw method
     * so that you do not need to worry about it when working on Exercise 4.
     *
     * @param ox 	The x-coordinate of texture origin (in pixels)
     * @param oy 	The y-coordinate of texture origin (in pixels)
     * @param x 	The x-coordinate of the texture origin
     * @param y 	The y-coordinate of the texture origin
     * @param angle The rotation angle (in degrees) about the origin.
     * @param sx 	The x-axis scaling factor
     * @param sy 	The y-axis scaling factor
     */
    private void computeTransform(float ox, float oy, float x, float y, float angle, float sx, float sy) {
        local.setToTranslation(x,y);
        local.rotate(angle);
        local.scale(sx,sy);
        local.translate(-ox,-oy);
    }

    /**
     * Draws text on the screen.
     *
     * @param text The string to draw
     * @param font The font to use
     * @param x The x-coordinate of the lower-left corner
     * @param y The y-coordinate of the lower-left corner
     */
    public void drawText(String text, BitmapFont font, float x, float y) {
        if (active != DrawPass.STANDARD) {
            Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
            return;
        }
        GlyphLayout layout = new GlyphLayout(font,text);
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, layout, x, y);
    }
    /**
     * Draws text on the screen.
     *
     * @param text The string to draw
     * @param font The font to use
     * @param x The x-coordinate of the lower-left corner
     * @param y The y-coordinate of the lower-left corner
     */
    public void drawTextHUD(String text, BitmapFont font, float x, float y, OrthographicCamera camera) {
        spriteBatch.begin();
        active = DrawPass.STANDARD;
        if (active != DrawPass.STANDARD) {
            Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
            return;
        }
        // Call the master drawing method (we have to for transforms)

        //Update HUD camera
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        GlyphLayout layout = new GlyphLayout(font,text);
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, layout, x, y);

        active = DrawPass.STANDARD;
        spriteBatch.end();
    }

    /**
     * Draws text centered on the screen.
     *
     * @param text The string to draw
     * @param font The font to use
     * @param offset The y-value offset from the center of the screen.
     */
    public void drawTextCentered(String text, BitmapFont font, float offset) {
        if (active != DrawPass.STANDARD) {
            Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
            return;
        }

        GlyphLayout layout = new GlyphLayout(font,text);
        float x = (getWidth()  - layout.width) / 2.0f;
        float y = (getHeight() + layout.height) / 2.0f;
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, layout, x, y+offset);
    }
    public void drawTextCenteredHUD(String text, BitmapFont font, float offset, OrthographicCamera camera) {
        spriteBatch.begin();
        active = DrawPass.STANDARD;
        if (active != DrawPass.STANDARD) {
            Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
            return;
        }

        GlyphLayout layout = new GlyphLayout(font,text);
        float x = (getWidth()  - layout.width) / 2.0f;
        float y = (getHeight() + layout.height) / 2.0f;
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, layout, x, y+offset);
        active = DrawPass.STANDARD;
        spriteBatch.end();
    }

    /**Draw the UI for the game which is different because it needs to follow the player.
     *
     * @param image
     * @param tint
     * @param x
     * @param y
     * @param angle
     * @param sx
     * @param sy
     */
    public void drawUI(Texture image, Color tint,
                     float x, float y, float angle, float sx, float sy, OrthographicCamera camera) {
        spriteBatch.begin();
        active = DrawPass.STANDARD;
        if (active != DrawPass.STANDARD) {
            Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
            return;
        }
        // Call the master drawing method (we have to for transforms)

        //Update HUD camera
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        holder.setRegion(image);
        draw(holder,tint,x,height-y,x,height- y,angle,sx,sy, false);
        active = DrawPass.STANDARD;
        spriteBatch.end();
    }

    /**
     * Draws the tinted polygon with the given transformations
     *
     * The texture of the polygon will be ignored.  This method will always use
     * a blank texture.
     *
     * The resulting polygon will be scaled (both position and size) by the global
     * scaling factor.
     *
     * @param poly  The polygon to draw
     * @param tint  The color tint
     * @param x 	The x-coordinate of the screen location
     * @param y 	The y-coordinate of the screen location
     * @param angle The rotation angle (in radians) about the origin.
     */
    public void draw(PolygonRegion poly, Color tint, float x, float y, float angle) {
        if (active == DrawPass.INACTIVE) {
            Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
            return;
        }

        // Vision cone

        // Convert angle to degrees
        float rotate = angle*180.0f/(float)Math.PI;

        // Put in a blank texture
        // TO DO: In future years I will write my own PolygonBatch to fix these issues.
        TextureRegion region = poly.getRegion();
        Texture orig = region.getTexture();
        int rx = 0; int ry = 0;
        int rw = 0; int rh = 0;
        if (orig != null) {
            rx = region.getRegionX(); ry = region.getRegionY();
            rw = region.getRegionWidth(); rh = region.getRegionHeight();
        }
        int BLANK_SIZE = 1;
        region.setTexture(coneTexture);
        region.setRegion(0, 0, BLANK_SIZE, BLANK_SIZE);
        spriteBatch.setColor(tint);
        spriteBatch.draw(poly, x, y, 0.0f, 0.0f, BLANK_SIZE, BLANK_SIZE, 1, 1, rotate);
        region.setTexture(orig);
        if (orig != null) {
            region.setRegion(rx,ry,rw,rh);
        }
    }
    /**
     * Start the debug drawing sequence.
     *
     * Nothing is flushed to the graphics card until the method end() is called.
     */
    public void beginDebug() {
        debugRender.setProjectionMatrix(spriteBatch.getProjectionMatrix());
        debugRender.begin(ShapeRenderer.ShapeType.Filled);
        debugRender.setColor(Color.RED);
        debugRender.circle(0, 0, 10);
        debugRender.end();

        debugRender.begin(ShapeRenderer.ShapeType.Line);
        active = DrawPass.DEBUG;
    }

    /**
     * Ends the debug drawing sequence, flushing textures to the graphics card.
     */
    public void endDebug() {
        debugRender.end();
        active = DrawPass.INACTIVE;
    }

    /**
     * Draws the outline of the given shape in the specified color
     *
     * @param shape The Box2d shape
     * @param color The outline color
     * @param x  The x-coordinate of the shape position
     * @param y  The y-coordinate of the shape position
     * @param angle  The shape angle of rotation
     * @param sx The amount to scale the x-axis
     * @param sx The amount to scale the y-axis
     */
    public void drawPhysics(Shape shape, Color color, float x, float y, float angle, float sx, float sy) {
        if (active != DrawPass.DEBUG) {
            Gdx.app.error("GameCanvas", "Cannot draw without active beginDebug()", new IllegalStateException());
            return;
        }

        local.setToScaling(sx * 10,sy * 10);
        local.translate(x/10,y/10);
        local.rotateRad(angle);
        debugRender.setColor(color);

        if (shape instanceof PolygonShape) {
            float x0, y0, x1, y1;
            PolygonShape polyShape = (PolygonShape) shape;
            for (int ii = 0; ii < polyShape.getVertexCount() - 1; ii++) {
                polyShape.getVertex(ii, vertex);
                local.applyTo(vertex);
                x0 = vertex.x; y0 = vertex.y;
                polyShape.getVertex(ii + 1, vertex);
                local.applyTo(vertex);
                x1 = vertex.x; y1 = vertex.y;
                debugRender.line(x0, y0, x1, y1);
            }
            // Close the loop
            polyShape.getVertex(polyShape.getVertexCount() - 1, vertex);
            local.applyTo(vertex);
            x0 = vertex.x; y0 = vertex.y;
            polyShape.getVertex(0, vertex);
            local.applyTo(vertex);
            x1 = vertex.x; y1 = vertex.y;
            debugRender.line(x0, y0, x1, y1);
        } else if (shape instanceof CircleShape) {
            CircleShape circleShape = (CircleShape) shape;
            float radius = circleShape.getRadius();
            Vector2 vertex = circleShape.getPosition();
            local.applyTo(vertex);
            debugRender.circle(vertex.x, vertex.y, radius * 10, 30);
        }
    }

    /**
     * Draws the tinted texture at the given position.
     *
     * The texture colors will be multiplied by the given color.  This will turn
     * any white into the given color.  Other colors will be similarly affected.
     *
     * Unless otherwise transformed by the global transform (@see begin(Affine2)),
     * the texture will be unscaled.  The bottom left of the texture will be positioned
     * at the given coordinates.
     *region
     * @param tint  The color tint
     * @param x 	The x-coordinate of the bottom left corner
     * @param y 	The y-coordinate of the bottom left corner
     * @param width	The texture width
     * @param height The texture height
     */
    public void draw(TextureRegion region, Color tint, float x, float y, float width, float height) {
        if (active != DrawPass.STANDARD) {
            Gdx.app.error("GameCanvas", "Cannot draw without active beginDebug()", new IllegalStateException());
            return;
        }

        // Unlike Lab 1, we can shortcut without a master drawing method
        spriteBatch.setColor(tint);
        spriteBatch.draw(region, x,  y, width, height);
    }
    /**
     * Enumeration of supported BlendStates.
     *
     * For reasons of convenience, we do not allow user-defined blend functions.
     * 99% of the time, we find that the following blend modes are sufficient
     * (particularly with 2D games).
     */
    public enum BlendState {
        /** Alpha blending on, assuming the colors have pre-multipled alpha (DEFAULT) */
        ALPHA_BLEND,
        /** Alpha blending on, assuming the colors have no pre-multipled alpha */
        NO_PREMULT,
        /** Color values are added together, causing a white-out effect */
        ADDITIVE,
        /** Color values are draw on top of one another with no transparency support */
        OPAQUE
    }

    /** Enumeration to track which pass we are in */
    private enum DrawPass {
        /** We are not drawing */
        INACTIVE,
        /** We are drawing sprites */
        STANDARD,
        /** We are drawing outlines */
        DEBUG
    }
}