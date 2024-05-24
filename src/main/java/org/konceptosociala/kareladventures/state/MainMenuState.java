package org.konceptosociala.kareladventures.state;

import javax.annotation.Nonnull;

import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.ui.ImageButton;
import org.konceptosociala.kareladventures.ui.Logo;
import org.konceptosociala.kareladventures.ui.Margin;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class MainMenuState extends BaseAppState implements ScreenController {
    private KarelAdventures app;
    private AppStateManager stateManager;
    private InputManager inputManager;
    private Nifty nifty;

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.nifty = this.app.getNifty();
    }

    @Override
    protected void onEnable() {
        inputManager.setCursorVisible(true);

        nifty.addScreen("main_menu_screen", new ScreenBuilder("Main menu screen") {{
            controller(app.getMainMenuState());

            layer(new LayerBuilder("main_menu_layer") {{
                childLayoutVertical();

                panel(Margin.vertical("10%"));
                panel(new Logo("main_logo", "Textures/logo.png", "80%"));
                panel(Margin.vertical("15%"));
                panel(new ImageButton("main_menu_play_button", "Play", "playGame()"));
                panel(new ImageButton("main_menu_play_button", "Settings", "openSettings()"));
                panel(new ImageButton("main_menu_quit_button", "Quit", "quitGame()"));
                
            }});
            
        }}.build(nifty));

        nifty.gotoScreen("main_menu_screen");
    }

    // UI callbacks

    public void playGame() {
        this.setEnabled(false);
        stateManager.attach(new GameState());
    }

    public void openSettings() {

    }

    public void quitGame() {
        app.stop();
    }

    // Other methods

    @Override
    protected void onDisable() {
    }
    
    @Override
    protected void cleanup(Application app) {
    }

    @Override
    public void bind(@Nonnull final Nifty nifty, @Nonnull final Screen screen) {
        this.nifty = nifty;
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }

    public void gotoScreen(@Nonnull final String screenId) {
        nifty.gotoScreen(screenId);
    }
}
