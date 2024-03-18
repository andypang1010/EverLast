package com.redpacts.frostpurge.game.util;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;
import com.redpacts.frostpurge.game.models.TileModel;

public class TilePath implements Connection<TileModel> {
    float cost;
    TileModel fromTile;
    TileModel toTile;

    public TilePath(TileModel fromTile, TileModel toTile) {
        this.fromTile = fromTile;
        this.toTile = toTile;

        // Cost of path should be Float.MAX_VALUE if any tile between path is an obstacle and 0 otherwise
//        cost = (fromTile.getType() == TileModel.TileType.OBSTACLE || toTile.getType() == TileModel.TileType.EMPTY) ? Float.MAX_VALUE : 1;
        cost = 1;
    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public TileModel getFromNode() {
        return fromTile;
    }

    @Override
    public TileModel getToNode() {
        return toTile;
    }
}
