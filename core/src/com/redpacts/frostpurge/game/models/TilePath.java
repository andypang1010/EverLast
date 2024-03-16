package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.ai.pfa.Connection;

public class TilePath implements Connection<TileModel> {
    float cost;
    TileModel fromTile;
    TileModel toTile;

    public TilePath(TileModel fromTile, TileModel toTile) {
        this.fromTile = fromTile;
        this.toTile = toTile;

        // Cost of path should be Float.MAX_VALUE if any tile between path is an obstacle and 0 otherwise
        cost = (fromTile.getType() == TileModel.TileType.OBSTACLE || toTile.getType() == TileModel.TileType.OBSTACLE) ? Float.MAX_VALUE : 1;
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
