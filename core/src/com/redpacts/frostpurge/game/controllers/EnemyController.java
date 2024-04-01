package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.ai.fsm.*;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Queue;

import com.badlogic.gdx.math.Vector2;
import com.redpacts.frostpurge.game.models.*;
import com.redpacts.frostpurge.game.util.EnemyStates;
import com.redpacts.frostpurge.game.util.TileGraph;
import com.redpacts.frostpurge.game.views.GameCanvas;

import java.util.ArrayList;
import java.util.List;


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
    MapModel board;
    TileGraph tileGraph;
    TileModel previousTile, targetTile;
    Queue<TileModel> pathQueue = new Queue<>();

    EnemyController(EnemyModel enemy, PlayerModel targetPlayerModel, TileModel startPatrolTile, TileModel endPatrolTile, EnemyStates initState, TileGraph tileGraph, MapModel board) {
        this.model = enemy;
        playerModel = targetPlayerModel;
        this.startPatrolTile = startPatrolTile;
        this.endPatrolTile = endPatrolTile;
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
            if (Vector2.dst(model.getPosition().x, model.getPosition().y, targetTile.getPosition().x, targetTile.getPosition().y) < targetTile.getTexture().getWidth()) {
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

    public void draw(GameCanvas canvas){
        if (model.getVelocity().x<0){
            flip = true;
        }else if (model.getVelocity().x >0){
            flip = false;
        }
        processRun();
        model.drawCharacter(canvas, (float) Math.toDegrees(model.getRotation()), Color.WHITE, "running", flip);

        // Draw vision cones
        for (EnemyModel.Vector2Triple t : ((EnemyModel) model).getTriangles()) {
            float[] vertices = {t.first.x, t.first.y, t.second.x, t.second.y, t.third.x, t.third.y};
            short[] indices = new short[3];
            indices[0] = 0;
            indices[1] = 1;
            indices[2] = 2;
            PolygonRegion cone = new PolygonRegion(new TextureRegion(), vertices, indices);
            canvas.draw(cone, new Color(1f, 1f, 1f, 0.5f), 100, 100 ,0);
        }
        ((EnemyModel) model).getTriangles().clear();
    }

    private void reachDestination() {
        stop();
    }

    private void setMoveDirection() {
        if (pathQueue.notEmpty()) {
            TileModel nextTile = pathQueue.first();
            moveDirection = new Vector2(nextTile.getPosition().x - model.getPosition().x, nextTile.getPosition().y - model.getPosition().y).nor();
            System.out.println(moveDirection.toString());
        }
    }


    @Override
    public void update() {
        System.out.println("Target location: " + targetTile.getPosition().toString());
        System.out.println("Current location: " + model.getPosition().toString());
        switch (currentState) {
            case PATROL:
//                System.out.println("PATROLLING: " + pathQueue.last().getPosition().toString());
                // Naive check for state transitions (If within certain distance, transition to chase state)
                if (Vector2.dst(playerModel.getPosition().x, playerModel.getPosition().y, model.getPosition().x, model.getPosition().y) > 400) {
                    if (Vector2.dst(startPatrolTile.getPosition().x, startPatrolTile.getPosition().y, model.getPosition().x, model.getPosition().y) < 100) {
                        setGoal(endPatrolTile);
                    }
                    else if (Vector2.dst(endPatrolTile.getPosition().x, endPatrolTile.getPosition().y, model.getPosition().x, model.getPosition().y) < 100) {
                        setGoal(startPatrolTile);
                    }
                }
                else {
                    changeState(EnemyStates.CHASE);
                }
                break;
            case CHASE:
                System.out.println("CHASING: " + modelPositionToTile(playerModel).getPosition().toString());
                setGoal(modelPositionToTile(playerModel));
                break;
        };

        if (Vector2.dst(playerModel.getPosition().x, playerModel.getPosition().y, model.getPosition().x, model.getPosition().y) < 50) {
            stop();
        }
        else {
//        moveToNextTile();
        }
        checkCollision();
    }

    private void moveToNextTile() {
        Vector2 vel = moveDirection;
        vel.scl(speedMultiplier);
        accelerate(vel.x, -vel.y);

        Vector2 newLocation = model.getPosition().add(model.getVelocity());
        model.setPosition(newLocation.x, newLocation.y);
    }

    private TileModel modelPositionToTile(CharactersModel model) {
        return board.getTileState(board.screenToBoard(model.getPosition().x), board.screenToBoard(model.getPosition().y));
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
