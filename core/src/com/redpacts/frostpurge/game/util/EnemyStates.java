package com.redpacts.frostpurge.game.util;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.redpacts.frostpurge.game.controllers.EnemyController;

public enum EnemyStates implements State<EnemyController> {
    PATROL,
    CHASE;
    @Override
    public void enter(EnemyController enemyController) {

    }

    @Override
    public void update(EnemyController enemyController) {

    }

    @Override
    public void exit(EnemyController enemyController) {

    }

    @Override
    public boolean onMessage(EnemyController enemyController, Telegram telegram) {
        return false;
    }

}
