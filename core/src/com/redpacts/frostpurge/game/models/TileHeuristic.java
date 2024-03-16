package com.redpacts.frostpurge.game.models;

import com.badlogic.gdx.ai.pfa.Heuristic;

public class TileHeuristic implements Heuristic<TileModel> {
    @Override
    public float estimate(TileModel startTile, TileModel endTile) {
        return 0;
    }
}
