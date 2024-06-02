package org.konceptosociala.kareladventures.game;

import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import lombok.Getter;

@Getter
public class Sun {
    private DirectionalLight light1;
    private DirectionalLight light2;

    public Sun(Node rootNode) {
        super();
        light1 = new DirectionalLight(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal(), new ColorRGBA(1.0f, 0.376f, 0.09f, 1.0f));
        light2 = new DirectionalLight(new Vector3f(.5f, -.5f, .5f).normalizeLocal(), new ColorRGBA(1.0f, 0.769f, 0.09f, 1.0f));
        rootNode.addLight(light1);
        rootNode.addLight(light2);
    }
}
