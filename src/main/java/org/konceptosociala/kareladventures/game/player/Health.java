package org.konceptosociala.kareladventures.game.player;

import lombok.Getter;
import lombok.ToString;

@ToString
public class Health {
    private static final int HP_MIN = 0;
    private static final int HP_MAX = 100;

    @Getter
    private int value;

    public Health(int value) {
        this.value = Math.min(Math.max(value, HP_MIN), HP_MAX);
    }

    public void add(int hp) {
        this.value = Math.min(this.value + hp, HP_MAX);
    }

    public void subtract(int hp) {
        this.value = Math.max(this.value - hp, HP_MIN);
    }
}
