package org.konceptosociala.kareladventures.game.player;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;

@ToString
public class Health implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public int HP_MIN = 0;
    public int HP_MAX = 100;

    @Getter
    @Setter
    private int value;

    public Health(int value) {
        HP_MIN = 0;
        HP_MAX = value;
        this.value = Math.min(Math.max(value, HP_MIN), HP_MAX);
    }

    public void add(int hp) {
        this.value = Math.min(this.value + hp, HP_MAX);
    }

    public void subtract(int hp) {
        this.value = Math.max(this.value - hp, HP_MIN);
    }
}
