package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.ai.fsm.*;
import com.badlogic.gdx.utils.Queue;

import com.badlogic.gdx.math.Vector2;
import com.redpacts.frostpurge.game.models.*;
import com.redpacts.frostpurge.game.util.EnemyStates;
import com.redpacts.frostpurge.game.util.TileGraph;
import com.redpacts.frostpurge.game.views.GameCanvas;


public class EnemyController extends CharactersController implements StateMachine<EnemyController, EnemyStates> {

    private Vector2 moveDirection = new Vector2();
    private float speedMultiplier = 0.5f;
    /*
    FSM
    */
    PlayerModel playerModel;
    TileModel startPatrolTile, endPatrolTile;
    EnemyStates initState;
    EnemyStates currentState;
    EnemyStates prevState = null;

    /*
    PATHFINDING
    */
    LevelModel board;
    TileGraph tileGraph;
    TileModel previousTile, targetTile;
    Queue<TileModel> pathQueue = new Queue<>();

    EnemyController(EnemyModel enemy, PlayerModel targetPlayerModel, EnemyStates initState, TileGraph tileGraph, LevelModel board) {
        this.model = enemy;
        playerModel = targetPlayerModel;
        this.startPatrolTile = board.getTileState(enemy.getStartPatrol()[0],enemy.getStartPatrol()[1]);
        this.endPatrolTile = board.getTileState(enemy.getEndPatrol()[0],enemy.getEndPatrol()[1]);
        setInitialState(initState);
        this.tileGraph = tileGraph;
        model.setPosition(startPatrolTile.getPosition().x, startPatrolTile.getPosition().y);
        previousTile = startPatrolTile;
        this.board = board;

        if (initState == EnemyStates.PATROL) {
            targetTile = endPatrolTile;
        }
        else {
            targetTile = modelPositionToTile(playerModel);
        }

        setGoal(targetTile);
    }

    public void setGoal(TileModel goalTile) {
        pathQueue.clear();
        GraphPath<TileModel> graphPath = tileGraph.findPath(previousTile, goalTile);
        for (int i = 1; i < graphPath.getCount(); i++) {
            pathQueue.addLast(graphPath.get(i));
//            System.out.println(graphPath.get(i).getCenter());
        }

        setMoveDirection();
        targetTile = goalTile;
    }

    private void checkCollision() {
        if (pathQueue.size > 0) {
            TileModel targetTile = pathQueue.first();
            if (Vector2.dst(model.getPosition().x, model.getPosition().y, targetTile.getOrigin().x, targetTile.getPosition().y) < 3) {
                reachNextTile();
            }
        }
    }

    private void reachNextTile() {
        this.previousTile = pathQueue.first();
        pathQueue.removeFirst();

        if (pathQueue.size == 0) {
            reachDestination();
        } else {
            setMoveDirection();
        }
    }

    public void draw(GameCanvas canvas, EnemyModel enemy){
        String direction = getDirection(enemy.getVelocity().x,enemy.getVelocity().y, previousDirection);
        processRun(direction);
        if (enemy.getVelocity().x == 0 && enemy.getVelocity().y ==0){
            enemy.drawCharacter(canvas, (float) Math.toDegrees(model.getRotation()), Color.WHITE, "idle", direction);
        } else{
            enemy.drawCharacter(canvas, (float) Math.toDegrees(model.getRotation()), Color.WHITE, "running", direction);
        }
        previousDirection = direction;
    }

    private void reachDestination() {
        stop();
    }

    private void setMoveDirection() {
        if (pathQueue.notEmpty()) {
            TileModel nextTile = pathQueue.first();
            moveDirection = new Vector2(nextTile.getPosition().x - model.getPosition().x, nextTile.getPosition().y - model.getPosition().y).nor();
            //System.out.println(moveDirection.toString());
        }
    }


    @Override
    public void update() {
        //System.out.println("Target location: " + targetTile.getPosition().toString());
        //System.out.println("Current location: " + model.getPosition().toString());
        switch (currentState) {
            case PATROL:
//                System.out.println("PATROLLING: " + pathQueue.last().getPosition().toString());
                // Naive check for state transitions (If within certain distance, transition to chase state)
                if (Vector2.dst(playerModel.getPosition().x, playerModel.getPosition().y, model.getPosition().x, model.getPosition().y) > 400) {
                    if (Vector2.dst(startPatrolTile.getPosition().x, startPatrolTile.getPosition().y, model.getPosition().x, model.getPosition().y) < 50) {
                        setGoal(endPatrolTile);
                    }
                    else if (Vector2.dst(endPatrolTile.getPosition().x, endPatrolTile.getPosition().y, model.getPosition().x, model.getPosition().y) < 50) {
                        setGoal(startPatrolTile);
                    }
                }
                else {
                    changeState(EnemyStates.CHASE);
                }
                break;
            case CHASE:
                //System.out.println("CHASING: " + modelPositionToTile(playerModel).getPosition().toString());
                setGoal(modelPositionToTile(playerModel));
                break;
        }

        if (Vector2.dst(playerModel.getPosition().x, playerModel.getPosition().y, model.getPosition().x, model.getPosition().y) < 50) {
            stop();
        }
        else {
        moveToNextTile();
        }
        model.getBody().setLinearVelocity(model.getVelocity());
        checkCollision();
        model.setPosition(model.getBody().getPosition().scl(10).add(-32, -32));
    }

    private void moveToNextTile() {
        Vector2 vel = moveDirection;
        vel.scl(speedMultiplier);
        accelerate(vel.x, -vel.y, 1);
    }

    private TileModel modelPositionToTile(CharactersModel model) {
        return board.getTileState(model.getPosition().x, model.getPosition().y);
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
