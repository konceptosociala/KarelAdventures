package org.konceptosociala.kareladventures.ui;

import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.DepthOfFieldFilter;

public class InterfaceBlur {
    private DepthOfFieldFilter dof;

    public InterfaceBlur(FilterPostProcessor fpp) {
        dof = new DepthOfFieldFilter();
        dof.setFocusDistance(0);
        dof.setFocusRange(50);
        dof.setBlurScale(3);
        dof.setEnabled(false);
        fpp.addFilter(dof);
    }

    public void setEnabled(boolean enabled) {
        dof.setEnabled(enabled);
    }
}
