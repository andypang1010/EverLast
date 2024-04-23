package com.redpacts.frostpurge.game.util;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.redpacts.frostpurge.game.controllers.EnemyController;
import com.redpacts.frostpurge.game.models.EnemyModel;

public enum EnemyStates implements State<EnemyModel> {
    PATROL,
    QUESTION,
    SEARCH,
    CHASE;
    @Override
    public void enter(EnemyModel enemyModel) {

    }

    @Override
    public void update(EnemyModel enemyModel) {

    }

    @Override
    public void exit(EnemyModel enemyModel) {

    }

    @Override
    public boolean onMessage(EnemyModel enemyModel, Telegram telegram) {
        return false;
    }

}
