package com.redpacts.frostpurge.game.util;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;
import com.redpacts.frostpurge.game.models.TileModel;

public class TileHeuristic implements Heuristic<TileModel> {
    @Override
    public float estimate(TileModel startTile, TileModel endTile) {
        return Vector2.dst(startTile.getPosition().x, startTile.getPosition().y, endTile.getPosition().x, endTile.getPosition().y);
    }
}
