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

public class EnemyController extends CharactersController implements StateMachine<EnemyModel, EnemyStates> {

    private Vector2 moveDirection = new Vector2();
    private float speedMultiplier = 20f;
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

            Vector2 currentTile =  board.getTileState(model.getPosition().x, model.getPosition().y).getPosition();
//            System.out.println("Current Tile");
//            System.out.println(currentTile);
            moveDirection = new Vector2(nextTile.getPosition().x -32 - model.getPosition().x, nextTile.getPosition().y -32 - model.getPosition().y).nor();
//            System.out.println("Direction");
//            System.out.println(moveDirection.toString());
        }else{
//            System.out.println("SKIP");
        }
    }


    @Override
    public void update() {
        //System.out.println("Target location: " + targetTile.getPosition().toString());
        //System.out.println("Current location: " + model.getPosition().toString());
        Vector2 currentTile =  board.getTileState(model.getPosition().x, model.getPosition().y).getPosition();
        if (currentTile == pathQueue.first().getPosition()){
            reachNextTile();
        }
        setMoveDirection();
        switch (currentState) {
            case PATROL:
//                System.out.println("PATROLLING: " + pathQueue.last().getPosition().toString());
                // Naive check for state transitions (If within certain distance, transition to chase state)
//                    System.out.println("Position:");
//                    System.out.println(currentTile);
//                    if (Vector2.dst(startPatrolTile.getPosition().x, startPatrolTile.getPosition().y, model.getPosition().x, model.getPosition().y) < 50) {
//                        setGoal(endPatrolTile);
//                    }
//                    else if (Vector2.dst(endPatrolTile.getPosition().x, endPatrolTile.getPosition().y, model.getPosition().x, model.getPosition().y) < 50) {
//                        setGoal(startPatrolTile);
//                    }
                    if (currentTile == startPatrolTile.getPosition()) {
                        previousTile = startPatrolTile;
                        setGoal(endPatrolTile);
                    }
                    else if (currentTile == endPatrolTile.getPosition()) {
                        previousTile = endPatrolTile;
                        setGoal(startPatrolTile);
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
        checkCollision();
        model.setPosition(model.getBody().getPosition().scl(10).add(-32, -32));
        currentState = ((EnemyModel) model).getCurrentState();
//        System.out.println("Position:");
//        System.out.println(model.getPosition());
//        System.out.println(targetTile.getPosition());

    }

    private void moveToNextTile() {
        Vector2 vel = moveDirection.cpy();
        vel.scl(speedMultiplier);
        model.getBody().setLinearVelocity(vel);
        model.setVelocity(vel.x,vel.y);
    }

    private TileModel modelPositionToTile(CharactersModel model) {
        return board.getTileState(model.getPosition().x, model.getPosition().y);
    }

    @Override
    public void changeState(EnemyStates enemyState) {
        ((EnemyModel) model).setCurrentState(enemyState);
    }

    public boolean revertToPreviousState() {
        if (((EnemyModel) model).getPrevState() == null) {
            return false;
        }

        ((EnemyModel) model).setCurrentState(((EnemyModel) model).getPrevState());
        return true;
    }

    public void setInitialState(EnemyStates enemyState) {
        initState = enemyState;
        currentState = enemyState;
    }

    public void setGlobalState(EnemyStates enemyState) {

    }

    @Override
    public EnemyStates getCurrentState() {
        return ((EnemyModel) model).getCurrentState();
    }

    public EnemyStates getGlobalState() {
        return null;
    }

    @Override
    public EnemyStates getPreviousState() {
        return ((EnemyModel) model).getPrevState();
    }

    @Override
    public boolean isInState(EnemyStates enemyState) {
        return getCurrentState() == enemyState;
    }

    public boolean handleMessage(Telegram telegram) {
        return false;
    }

}
