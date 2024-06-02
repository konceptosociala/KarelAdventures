package org.konceptosociala.kareladventures.game.npc;

import org.konceptosociala.kareladventures.utils.InteractableNode;

import com.jme3.scene.Spatial;

import lombok.Getter;
import lombok.Setter;

@Getter
public class NPC extends InteractableNode {
    private Spatial model; 
    private String name;
    @Setter
    private Dialog dialog;

    @Override
    public void interract() {
        
    }
}
