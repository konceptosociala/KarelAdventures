package org.konceptosociala.kareladventures.state;

import javax.annotation.Nonnull;

import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.ui.ImageButton;
import org.konceptosociala.kareladventures.ui.InterfaceBlur;

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
    private InterfaceBlur interfaceBlur;
    private GameState gameState;

    public PauseState(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.appStateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.bulletAppState = gameState.getBulletAppState();
        this.nifty = this.app.getNifty();
        this.interfaceBlur = gameState.getInterfaceBlur();
    }

    @Override
    protected void onEnable() {
        inputManager.setCursorVisible(true);
        bulletAppState.setEnabled(false);
        interfaceBlur.setEnabled(true);

        nifty.addScreen("pause_screen", new ScreenBuilder("Pause screen") {{
            controller(PauseState.this);

            layer(new LayerBuilder("pause_layer") {{
                childLayoutHorizontal();

                panel(new PanelBuilder("pause_panel") {{
                    childLayoutCenter();
                    width("100%");
                    height("100%");

                    panel(new PanelBuilder("pause_controls") {{
                        childLayoutCenter();

                        image(new ImageBuilder("main_menu_panel_bg") {{
                            filename("Interface/UI/Transparent center/panel-transparent-center-010.png");
                            imageMode("resize:16,16,16,16,16,16,16,16,16,16,16,16");
                            width("450px");
                            height("60%h");
                        }});

                        panel(new PanelBuilder("main_menu_panel_buttons"){{
                            childLayoutVertical();

                            panel(new ImageButton("main_menu_resume_button", "Відновити", null, "resume()"));
                            panel(new ImageButton("main_menu_quit_button", "Вийти в меню", null, "exitToMenu()"));
                        }});
                    }});
                }});
                
            }});
            
        }}.build(nifty));

        nifty.gotoScreen("pause_screen");
    }

    // UI callbacks

    public void resume() {
        gameState.setEnabled(true);
        setEnabled(false);
    }

    public void exitToMenu() {
        appStateManager.detach(gameState);
        app.getMainMenuState().setEnabled(true);
        setEnabled(false);
    }

    // Other methods

    public void gotoScreen(@Nonnull final String screenId) {
        nifty.gotoScreen(screenId);
    }

    @Override
    protected void onDisable() {
        interfaceBlur.setEnabled(false);
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
