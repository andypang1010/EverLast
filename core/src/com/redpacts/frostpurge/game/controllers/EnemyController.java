package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.Gdx;
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

public class EnemyController extends CharactersController implements StateMachine<EnemyModel, EnemyStates> {

    private Vector2 moveDirection = new Vector2();
    private float speedMultiplier = 40f;
    private float listenRadius = 20f;
    private float alertRadius = 30f;
    private float currentListenInterval = 0f;
    private final float notHeardToPatrolInterval = 3f;

    /*
    FSM
    */
    PlayerModel playerModel;
    int nextWaypointIndex;
    TileModel[] waypoints;
    EnemyStates initState;
    EnemyStates currentState;
    /*
    PATHFINDING
    */
    LevelModel board;
    TileGraph tileGraph;
    TileModel targetTile;
    TileModel currentTile;
    Queue<TileModel> pathQueue = new Queue<>();
    GraphPath<TileModel> graphPath;
    PolygonRegion cone;
    TextureRegion textureRegion;
    Color coneColor;
    boolean reachedDestination = false;
    int updatePathCounter = 0;
    EnemyController(EnemyModel enemy, PlayerModel targetPlayerModel, EnemyStates initState, TileGraph tileGraph, LevelModel board, ArrayList<int[]> waypoints) {
        this.model = enemy;
        playerModel = targetPlayerModel;
        this.waypoints = new TileModel[waypoints.size()];

        for (int i = 0; i<waypoints.size();i++){
            this.waypoints[i] = board.getTileState(waypoints.get(i)[0],waypoints.get(i)[1]);
        }

        this.nextWaypointIndex = 1;
        setInitialState(initState);
        this.tileGraph = tileGraph;
        currentTile = this.waypoints[0];
        this.board = board;

        if (initState == EnemyStates.PATROL) {
            setGoal(this.waypoints[this.nextWaypointIndex]);
        }
        else {
            setGoal(modelPositionToTile(playerModel));
        }

        textureRegion = new TextureRegion();
        coneColor = new Color(1f,1f,1f,.5f);
//        cone = new PolygonRegion(new TextureRegion(), null,null);
    }

    public void setGoal(TileModel goalTile) {
        pathQueue.clear();
//        System.out.println("!!!Path Queue Cleared!!!");
        graphPath = tileGraph.findPath(currentTile, goalTile);
        for (int i = 1; i < graphPath.getCount(); i++) {
            pathQueue.addLast(graphPath.get(i));
        }

        setMoveDirection();
        targetTile = goalTile;
    }

    private void checkCollision() {
        if (pathQueue.size > 0) {
            if (currentTile == pathQueue.first()) {
                pathQueue.removeFirst();


            }
        }
    }

    public void draw(GameCanvas canvas, EnemyModel enemy){
        // Draw shadow
        short[] indices = new short[3];
        indices[0] = 0;
        indices[1] = 1;
        indices[2] = 2;

        Vector2 rayStart = model.getBody().getPosition().cpy().add(3.5f, 4.5f);
        int numRays = 20; // Number of segments for circle
        float deltaAngle = 360f / (numRays - 1); // Angle between each segment

        float angle = 0;
        Vector2 dir = new Vector2(1, 0);
        Vector2 rayDirection = dir.cpy().rotateDeg(angle);
        Vector2 rayEnd = rayStart.cpy().add(rayDirection.scl(((EnemyModel)model).getRadius())); // Calculate end point of the ray
        Vector2 rayPrevious = rayEnd.cpy();
        Vector2 ray1, ray2, ray3;

        for (int i = 1; i < numRays; i++) {
            angle += deltaAngle;
            rayDirection = dir.cpy().rotateDeg(angle);
            rayEnd = rayStart.cpy().add(rayDirection.scl(((EnemyModel)model).getRadius()));

            ray1 = rayStart.cpy().scl(10).add(-100, -100);
            ray2 = rayPrevious.cpy().scl(10).add(-100, -100);
            ray3 = rayEnd.cpy().scl(10).add(-100, -100);

            float[] vertices = {ray1.x, ray1.y, ray2.x, ray2.y, ray3.x, ray3.y};
            PolygonRegion cone = new PolygonRegion(new TextureRegion(), vertices, indices);
            canvas.draw(cone, new Color(0f, 0f, 0f, 0.5f), 70, 50 ,0);

            rayPrevious = rayEnd.cpy();
        }
        // Draw vision cones
//        for (EnemyModel.Vector2Triple t : ((EnemyModel) model).getTriangles()) {
//            float[] vertices = {t.first.x, t.first.y, t.second.x, t.second.y, t.third.x, t.third.y};
//            short[] indices_c = new short[3];
//            indices_c[0] = 0;
//            indices_c[1] = 1;
//            indices_c[2] = 2;
//            cone = new PolygonRegion(textureRegion, vertices, indices_c);
//            canvas.draw(cone, coneColor, 100, 100 ,0);
//        }
//        ((EnemyModel) model).getTriangles().clear();

        // Draw enemy
        String direction = getDirection(enemy.getBody().getLinearVelocity().x,enemy.getBody().getLinearVelocity().y, previousDirection);
        processRun(direction);
        if (enemy.getVelocity().x == 0 && enemy.getVelocity().y ==0){
            enemy.drawCharacter(canvas, (float) Math.toDegrees(model.getRotation()), Color.WHITE, "idle", direction);
        } else{
            enemy.drawCharacter(canvas, (float) Math.toDegrees(model.getRotation()), Color.WHITE, "running", direction);
        }
        previousDirection = direction;
    }

//    private void reachDestination() {
//        model.getBody().setLinearVelocity(new Vector2(0, 0));
//        model.setVelocity(0, 0);
//    }

    private void setMoveDirection() {
        if (pathQueue.notEmpty()) {
            TileModel nextTile = pathQueue.first();
            moveDirection = new Vector2(nextTile.getPosition().x - 32 - model.getPosition().x, nextTile.getPosition().y - 32 - model.getPosition().y).nor();
        }

        else {
            reachedDestination = true;
        }
    }


    @Override
    public void update() {

        // Update enemy's current tile
        currentTile = board.getTileState(model.getPosition().x, model.getPosition().y);

        switch (currentState) {
            case PATROL:
//                System.out.println("IN PATROL");

                // When reaches next patrol waypoint
                if (currentTile == waypoints[nextWaypointIndex]) {

                    // Increment waypoint index and update targetTile
                    nextWaypointIndex = (nextWaypointIndex + 1) % waypoints.length;
                    setGoal(waypoints[nextWaypointIndex]);

//                    System.out.println("Next waypoint index: " + nextWaypointIndex);
//                    System.out.println("Next waypoint position: " + waypoints[nextWaypointIndex].getPosition());
                }

//                System.out.println("ENEMY TO PLAYER DISTANCE: " + Vector2.dst(
//                        model.getBody().getPosition().x,
//                        model.getBody().getPosition().y,
//                        playerModel.getBody().getPosition().x,
//                        playerModel.getBody().getPosition().y));

                // If the player is within listen radius, go to question state
                if (isPlayerWithinListenRadius()) {
                    changeState(EnemyStates.QUESTION);
                }

                break;

            case QUESTION:
                System.out.println("IN QUESTION STATE!");

                if (isPlayerWithinListenRadius()) {
                    currentListenInterval = notHeardToPatrolInterval;
                    setGoal(modelPositionToTile(playerModel));
                }

                else if (reachedDestination) {
                    changeState(EnemyStates.SEARCH);
                }

                break;

            case SEARCH:
                System.out.println("IN SEARCH STATE!");

                // If the player is within listen radius, go to question state
                if (isPlayerWithinListenRadius()) {
                    changeState(EnemyStates.QUESTION);
                }

                else {
                    currentListenInterval -= Gdx.graphics.getDeltaTime();

                    lookAround();

                    // If player not found within x seconds, go back to patrol state
                    if (currentListenInterval <= 0f) {
                        changeState(EnemyStates.PATROL);
                        nextWaypointIndex = 0;
                        setGoal(waypoints[0]);
                    }
                }

                break;

            case CHASE:
                System.out.println("IN CHASE STATE!");

                // Update path to player every 0.5 seconds
                if (updatePathCounter > 30){
                    setGoal(modelPositionToTile(playerModel));
                    updatePathCounter = 0;
                }
                else {
                    updatePathCounter++;
                }

                alertNeighborEnemies();

                break;
        }

        checkCollision();
        setMoveDirection();

        model.setPosition(model.getBody().getPosition().scl(10));
        currentState = ((EnemyModel) model).getCurrentState();
        moveToNextTile();
    }

    private void alertNeighborEnemies() {
        for (int i = 0; i < GameMode.enemyControllers.size; i++) {
            EnemyController enemy = GameMode.enemyControllers.get(i);
            if (enemy == this) continue;

            Vector2 enemyPosition = enemy.model.getBody().getPosition().cpy();
//                    System.out.println(((EnemyModel) enemy.getModel()).getID() + "'s distance to current enemy: " + Vector2.dst(model.getBody().getPosition().x, model.getBody().getPosition().y, enemyPosition.x, enemyPosition.y));
            if (enemy.getCurrentState() != EnemyStates.CHASE &&
            Vector2.dst(
                    model.getBody().getPosition().x,
                    model.getBody().getPosition().y,
                    enemyPosition.x,
                    enemyPosition.y)
                < alertRadius) {

                enemy.changeState(EnemyStates.CHASE);
                System.out.println("Alerted!!!");
            }
        }
    }

    private boolean isPlayerWithinListenRadius() {
        return Vector2.dst(
                model.getBody().getPosition().x,
                model.getBody().getPosition().y,
                playerModel.getBody().getPosition().x,
                playerModel.getBody().getPosition().y)
                <= listenRadius

                && playerModel.getBody().getLinearVelocity().len() >= 1f;
    }

    private void lookAround() {
        // TODO: Look around at random directions
    }

    private void moveToNextTile() {
        vel = moveDirection.cpy();
        vel.scl(speedMultiplier);
        model.getBody().setLinearVelocity(vel);
        model.setVelocity(vel);
    }

    private TileModel modelPositionToTile(CharactersModel model) {
        return board.getTileState(model.getPosition().x, model.getPosition().y);
    }

    @Override
    public void changeState(EnemyStates newState) {
        ((EnemyModel) model).setCurrentState(newState);
        this.currentState = newState;
    }

    public boolean revertToPreviousState() {
        return false;
    }

    public void setInitialState(EnemyStates enemyState) {
        initState = enemyState;
        this.currentState = enemyState;
    }

    public void setGlobalState(EnemyStates enemyState) {
        return;
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
        return null;
    }

    @Override
    public boolean isInState(EnemyStates enemyState) {
        return getCurrentState() == enemyState;
    }

    public boolean handleMessage(Telegram telegram) {
        return false;
    }

}
