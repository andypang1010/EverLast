package com.redpacts.frostpurge.game.util;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.redpacts.frostpurge.game.models.TileModel;

public class TileGraph implements IndexedGraph<TileModel> {
    TileHeuristic tileHeuristic = new TileHeuristic();
    Array<TileModel> tiles = new Array<>();
    Array<TilePath> paths = new Array<>();
    ObjectMap<TileModel, Array<Connection<TileModel>>> tilesMap = new ObjectMap<>();

    private int lastTileIndex = 0;

    public void addTile(TileModel tileModel) {
        tileModel.index = lastTileIndex;
        lastTileIndex++;

        tiles.add(tileModel);
    }

    public void connectTiles(TileModel fromTile, TileModel toTile){
        TilePath path = new TilePath(fromTile, toTile);

        if (!tilesMap.containsKey(fromTile)) {
            tilesMap.put(fromTile, new Array<Connection<TileModel>>());
        }
        tilesMap.get(fromTile).add(path);
        paths.add(path);
    }

    public GraphPath<TileModel> findPath(TileModel startTile, TileModel endTile){
        GraphPath<TileModel> path = new DefaultGraphPath<>();
        new IndexedAStarPathFinder<>(this).searchNodePath(startTile, endTile, tileHeuristic, path);
        return path;
    }

    @Override
    public int getIndex(TileModel tileModel) {
        return tileModel.index;
    }

    @Override
    public int getNodeCount() {
        return lastTileIndex;
    }

    @Override
    public Array<Connection<TileModel>> getConnections(TileModel tileModel) {
        if (tilesMap.containsKey(tileModel)) {
            return tilesMap.get(tileModel);
        }
        return null;
    }
}
