/*
 * Copyright (c) 2009-2021 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.konceptosociala.kareladventures;

import com.jme3.app.SimpleApplication;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.environment.util.EnvMapUtils;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.*;
import com.jme3.ui.Picture;
import com.jme3.util.SkyFactory;

/**
 * test
 *
 * @author nehon
 */
public class RefEnv extends SimpleApplication {

    public static void main(String[] args) {
        RefEnv app = new RefEnv();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Spatial sc = assetManager.loadModel("Models/karel.glb");
        rootNode.attachChild(sc);
        Spatial sky = SkyFactory.createSky(assetManager, "Textures/sky.jpg", SkyFactory.EnvMapType.EquirectMap);
        rootNode.attachChild(sky);
        // rootNode.getChild(0).setCullHint(Spatial.CullHint.Always);
        stateManager.attach(new EnvironmentCamera(256, Vector3f.ZERO));
    }

    private int frame = 0;

    @Override
    public void simpleUpdate(float tpf) {
        frame++;

        EnvironmentCamera eCam = stateManager.getState(EnvironmentCamera.class);
        if (frame == 5) {
            final LightProbe probe = LightProbeFactory.makeProbe(eCam, rootNode, EnvMapUtils.GenerationType.Fast, new JobProgressAdapter<LightProbe>() {

                @Override
                public void done(LightProbe result) {
                    System.err.println("Done rendering env maps");
                    // rootNode.getChild(0).setCullHint(Spatial.CullHint.Dynamic);
                }
            });
            probe.getArea().setRadius(200);
            rootNode.addLight(probe);

        }

        if (eCam.isBusy()) {
            System.out.println("EnvironmentCamera busy as of frame " + frame);
        }
    }
}
