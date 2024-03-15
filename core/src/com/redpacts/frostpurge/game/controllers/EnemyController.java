package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.graphics.Color;

import com.redpacts.frostpurge.game.models.EnemyModel;
import com.redpacts.frostpurge.game.views.GameCanvas;

public class EnemyController extends CharactersController {
    EnemyController(EnemyModel enemy) {
        this.model = enemy;
    }

    public void draw(GameCanvas canvas){
        model.drawCharacter(canvas, (float) Math.toDegrees(model.getRotation()), Color.RED);
    }
}
