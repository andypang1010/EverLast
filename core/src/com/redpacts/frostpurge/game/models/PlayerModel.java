package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.Gdx;
import com.redpacts.frostpurge.game.assets.AssetDirectory;
import com.redpacts.frostpurge.game.util.FilmStrip;
import com.redpacts.frostpurge.game.views.GameCanvas;

public class PlayerModel extends CharactersModel {

    /**
     * Instantiates the player with their starting location and angle and with their texture
     *
     * @param position vector2 representing the starting location
     * @param rotation float representing angle the player is facing
     */
    public PlayerModel(Vector2 position, float rotation, AssetDirectory directory) {
        this.position = position;
        this.rotation = rotation;
        this.velocity = new Vector2(0, 0);
        texture = new TextureRegion(directory.getEntry("Liv", Texture.class)).getTexture();
        Texture run = new TextureRegion(directory.getEntry("Liv_Run", Texture.class)).getTexture();
        running = new FilmStrip(run, 1, 12, 12);
    }
}

