package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.ai.fsm.*;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.Queue.*;

import com.badlogic.gdx.math.Vector2;
import com.redpacts.frostpurge.game.models.enemyModel;
import com.redpacts.frostpurge.game.models.PlayerModel;
import com.redpacts.frostpurge.game.models.TileModel;
import com.redpacts.frostpurge.game.util.EnemyStates;
import com.redpacts.frostpurge.game.views.GameCanvas;


public class EnemyController extends CharactersController implements StateMachine<EnemyController, EnemyStates> {

    PlayerModel playerModel;
    TileModel startPatrolTile, endPatrolTile;
    EnemyStates initState;
    EnemyStates currentState;
    EnemyStates prevState = null;

    Queue<TileModel> pathQueue = new Queue<TileModel>();

    EnemyController(enemyModel enemy, PlayerModel targetPlayerModel, TileModel startPatrolTile, TileModel endPatrolTile, EnemyStates initState) {
        this.model = enemy;
        playerModel = targetPlayerModel;
        this.startPatrolTile = startPatrolTile;
        this.endPatrolTile = endPatrolTile;
        setInitialState(initState);
        // TODO: Find valid path between patrol points and store as patrolPath
    }

    public void patrol() {
        // TODO: Move along patrolPath
    }

    public void chase() {
        // TODO: Move along chasePath
    }

    public void draw(GameCanvas canvas){
        model.drawCharacter(canvas, (float) Math.toDegrees(model.getRotation()), Color.RED, "idle", false);
    }

    @Override
    public void update() {
        switch (currentState) {
            case PATROL:
                System.out.println("PATROLLING");

                // TODO: Check if player is in sight

                // Naive check for state transitions (If within certain distance, transition to chase state)
                if (Vector2.dst(playerModel.getPosition().x, playerModel.getPosition().y, model.getPosition().x, model.getPosition().y) > 100) {
                    patrol();
                }
                else {
                    changeState(EnemyStates.CHASE);
                }

                break;
            case CHASE:
                System.out.println("CHASING");
                // TODO: Take in LevelModel as input
                // TODO: Find all traversable tiles
                // TODO: Use pathfinding to find valid path to target tile
                // TODO: Move towards the next node in path

                // Naive check for state transitions (If within certain distance, transition to chase state)
                if (Vector2.dst(playerModel.getPosition().x, playerModel.getPosition().y, model.getPosition().x, model.getPosition().y) < 200) {
                    chase();
                }
                else {
                    changeState(EnemyStates.PATROL);
                }
                break;
        }
    }

    @Override
    public void changeState(EnemyStates enemyState) {
        prevState = currentState;
        currentState = enemyState;
    }

    public boolean revertToPreviousState() {
        if (prevState == null) {
            return false;
        }

        currentState = prevState;
        return true;
    }

    @Override
    public void setInitialState(EnemyStates enemyState) {
        initState = enemyState;
        currentState = enemyState;
    }

    public void setGlobalState(EnemyStates enemyState) {

    }

    @Override
    public EnemyStates getCurrentState() {
        return currentState;
    }

    public EnemyStates getGlobalState() {
        return null;
    }

    @Override
    public EnemyStates getPreviousState() {
        return prevState;
    }

    @Override
    public boolean isInState(EnemyStates enemyState) {
        return currentState == enemyState;
    }

    public boolean handleMessage(Telegram telegram) {
        return false;
    }

}
