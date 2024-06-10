package org.konceptosociala.kareladventures.game.player;

import java.io.Serializable;

import lombok.Getter;
import lombok.ToString;

@ToString
public class Energy implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final int ENERGY_MIN = 0;
    private static final int ENERGY_MAX = 100;

    @Getter
    private int value;

    public Energy(int value) {
        this.value = Math.min(Math.max(value, ENERGY_MIN), ENERGY_MAX);
    }

    public void add(int ep) {
        this.value = Math.min(this.value + ep, ENERGY_MAX);
    }

    public void subtract(int ep) {
        this.value = Math.max(this.value - ep, ENERGY_MIN);
    }
}
