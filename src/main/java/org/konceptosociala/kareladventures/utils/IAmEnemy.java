package org.konceptosociala.kareladventures.utils;

import org.konceptosociala.kareladventures.state.GameState;

public interface IAmEnemy {
    public void receiveDamage(int dmg);
    public void pushback();
    public void setThisGameState(GameState gameState);
}
