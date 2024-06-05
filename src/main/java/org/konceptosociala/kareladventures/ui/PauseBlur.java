package org.konceptosociala.kareladventures.ui;

import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.DepthOfFieldFilter;

public class PauseBlur {
    private DepthOfFieldFilter dof;

    public PauseBlur(FilterPostProcessor fpp) {
        dof = new DepthOfFieldFilter();
        dof.setFocusDistance(0);
        dof.setFocusRange(50);
        dof.setBlurScale(3);
        fpp.addFilter(dof);

        setEnabled(false);
    }

    public void setEnabled(boolean enabled) {
        dof.setEnabled(enabled);
    }
}
