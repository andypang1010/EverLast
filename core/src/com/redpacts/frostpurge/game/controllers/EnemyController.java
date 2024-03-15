package com.redpacts.frostpurge.game.controllers;

import com.badlogic.gdx.graphics.Color;

import com.redpacts.frostpurge.game.models.EnemyModel;
import com.redpacts.frostpurge.game.views.GameCanvas;

import java.awt.*;

public class EnemyController extends CharactersController {
    EnemyController(EnemyModel enemy) {
        this.owner = enemy;
    }

    public void draw(GameCanvas canvas){
        owner.drawCharacter(canvas, (float) Math.toDegrees(owner.getRotation()), Color.RED);
    }
}
