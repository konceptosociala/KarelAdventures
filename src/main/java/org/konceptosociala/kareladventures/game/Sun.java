package org.konceptosociala.kareladventures.game;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;

import lombok.Getter;

@Getter
public class Sun extends DirectionalLight {
    public static final int SHADOWMAP_SIZE = 2048;

    private FilterPostProcessor fpp;
    private DirectionalLightShadowFilter dlsf;
    private SSAOFilter ssao;
    private FXAAFilter fxaa;

    public Sun(AssetManager assetManager, FilterPostProcessor fpp) {
        super();
        setColor(ColorRGBA.White);
        setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());

        this.fpp = fpp;

        dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3);
        dlsf.setLight(this);
        dlsf.setEnabledStabilization(true);
        dlsf.setShadowIntensity(0.7f);
        dlsf.setEdgesThickness(10);
        dlsf.setShadowCompareMode(CompareMode.Hardware);
        dlsf.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
        dlsf.setEnabled(true);

        ssao = new SSAOFilter(0.2f, 1f, 1f, 0.1f);
        fxaa = new FXAAFilter();

        fpp.addFilter(dlsf);
        fpp.addFilter(ssao);
        fpp.addFilter(fxaa);
    }

    public void cleanup() {
        dlsf.setEnabled(false);
        ssao.setEnabled(false);
        fxaa.setEnabled(false);
        fpp.cleanup();
    }
}
