package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.ai.fsm.*;

import com.badlogic.gdx.math.Vector2;
import com.redpacts.frostpurge.game.models.EnemyModel;
import com.redpacts.frostpurge.game.util.EnemyStates;
import com.redpacts.frostpurge.game.views.GameCanvas;
import com.sun.tools.javac.util.Pair;

public class EnemyController extends CharactersController implements StateMachine<EnemyController, EnemyStates> {
    Pair<Vector2, Vector2> patrolPoints;
    EnemyStates initState;
    EnemyStates currentState;
    EnemyStates prevState = null;


    EnemyController(EnemyModel enemy, Vector2 startPatrolPoint, Vector2 endPatrolPoint) {
        this.model = enemy;
        setInitialState(EnemyStates.PATROL);
        patrolPoints = new Pair<>(startPatrolPoint, endPatrolPoint);
        // TODO: Find valid path between patrol points and store as patrolPath
    }

    public void patrol() {
        // TODO: Move along patrolPath
    }

    public void draw(GameCanvas canvas){
        model.drawCharacter(canvas, (float) Math.toDegrees(model.getRotation()), Color.RED);
    }

    @Override
    public void update() {
        switch (currentState) {
            case PATROL:
                System.out.println("PATROLLING");

                // TODO: Check if player is in sight
                boolean playerFound = false;
                if (!playerFound) {
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
