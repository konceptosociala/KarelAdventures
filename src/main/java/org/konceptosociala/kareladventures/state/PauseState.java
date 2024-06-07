package org.konceptosociala.kareladventures.state;

import javax.annotation.Nonnull;

import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.ui.PauseBlur;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import lombok.Getter;

@Getter
public class PauseState extends BaseAppState implements ScreenController {
    private KarelAdventures app;
    private AppStateManager appStateManager;
    private InputManager inputManager;
    private BulletAppState bulletAppState;
    private Nifty nifty;
    private PauseBlur pauseBlur;

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.appStateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.bulletAppState = this.app.getBulletAppState();
        this.nifty = this.app.getNifty();
        this.pauseBlur = new PauseBlur(this.app.getFpp());
    }

    @Override
    protected void onEnable() {
        inputManager.setCursorVisible(true);
        bulletAppState.setEnabled(false);
        pauseBlur.setEnabled(true);

        nifty.addScreen("pause_screen", new ScreenBuilder("Pause screen") {{
            controller(new DefaultScreenController());

            layer(new LayerBuilder("pause_layer") {{
                childLayoutHorizontal();

                panel(new PanelBuilder("pause_panel") {{
                    childLayoutCenter();

                    width("100%");
                    height("100%");

                    image(new ImageBuilder("pause_bg") {{
                        filename("Interface/plane1.png");
                        height("50%");
                    }});

                    panel(new PanelBuilder("pause_controls") {{

                    }});
                }});
                
            }});
            
        }}.build(nifty));

        nifty.gotoScreen("pause_screen");
    }

    public void gotoScreen(@Nonnull final String screenId) {
        nifty.gotoScreen(screenId);
    }

    @Override
    protected void onDisable() {
        pauseBlur.setEnabled(false);
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }

    @Override
    protected void cleanup(Application app) {
    }
    
}
