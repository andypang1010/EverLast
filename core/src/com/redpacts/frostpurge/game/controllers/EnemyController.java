package com.redpacts.frostpurge.game.controllers;

import com.redpacts.frostpurge.game.models.EnemyModel;
import com.redpacts.frostpurge.game.views.GameCanvas;

public class EnemyController {
    private EnemyModel enemy;

    EnemyController(EnemyModel enemy) {
        this.enemy = enemy;
    }

    public void draw(GameCanvas canvas){
        enemy.drawEnemy(canvas, (float) Math.toDegrees(enemy.getRotation()));
    }
}
