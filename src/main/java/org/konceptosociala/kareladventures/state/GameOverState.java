package org.konceptosociala.kareladventures.state;

import java.io.File;
import javax.annotation.Nonnull;

import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.ui.ImageButton;
import org.konceptosociala.kareladventures.ui.InterfaceBlur;
import org.konceptosociala.kareladventures.utils.AudioManager;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class GameOverState extends BaseAppState implements ScreenController {
    private KarelAdventures app;
    private AppStateManager appStateManager;
    private InputManager inputManager;
    private BulletAppState bulletAppState;
    private Nifty nifty;
    private InterfaceBlur interfaceBlur;
    private GameState gameState;
    private KarelFarmState karelFarmState;
    private GuideBookState guideBookState;
    private DialogState dialogState;
    private InventoryState inventoryState;
    private ChaseCamera chaseCam;
    private AudioManager audio;

    public GameOverState(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.appStateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.nifty = this.app.getNifty();
        this.audio = this.app.getAudioManager();
        this.bulletAppState = gameState.getBulletAppState();
        this.interfaceBlur = gameState.getInterfaceBlur();
        this.karelFarmState = gameState.getKarelFarmState();
        this.guideBookState = gameState.getGuideBookState();
        this.dialogState = gameState.getDialogState();
        this.inventoryState = gameState.getInventoryState();
        this.chaseCam = gameState.getChaseCam();
    }

    @Override
    protected void onEnable() {
        inputManager.setCursorVisible(true);
        bulletAppState.setEnabled(false);
        karelFarmState.setEnabled(false);
        dialogState.setEnabled(false);
        inventoryState.setEnabled(false);
        chaseCam.setEnabled(false);
        guideBookState.setEnabled(false);
        interfaceBlur.setEnabled(true);

        nifty.addScreen("game_over_screen", new ScreenBuilder("Game over screen") {{
            controller(GameOverState.this);

            layer(new LayerBuilder("game_over_layer") {{
                childLayoutHorizontal();

                panel(new PanelBuilder("game_over_panel") {{
                    childLayoutCenter();
                    width("100%");
                    height("100%");

                    panel(new PanelBuilder("game_over_controls") {{
                        childLayoutCenter();

                        image(new ImageBuilder("main_menu_panel_bg") {{
                            filename("Interface/UI/Transparent center/panel-transparent-center-010.png");
                            imageMode("resize:16,16,16,16,16,16,16,16,16,16,16,16");
                            width("450px");
                            height("60%h");
                        }});

                        panel(new PanelBuilder("main_menu_panel_buttons"){{
                            childLayoutVertical();

                            if (new File("data/Saves/karel.sav").exists()) {
                                panel(new ImageButton("main_menu_resume_button", "Завантажити збереження", null, "loadSaving()", "hoverSound()"));
                            }
                            
                            panel(new ImageButton("main_menu_quit_button", "Вийти в меню", null, "exitToMenu()", "hoverSound()"));
                        }});
                    }});
                }});
                
            }});
            
        }}.build(nifty));

        nifty.gotoScreen("game_over_screen");
    }

    // UI callbacks

    public void hoverSound() {
        audio.button1.stop();
        audio.button1.play();
    }

    public void loadSaving() {
        audio.button2.stop();
        audio.button2.play();

        appStateManager.detach(gameState);
        app.getMainMenuState().loadGame();
        setEnabled(false);
    }

    public void exitToMenu() {
        audio.button2.stop();
        audio.button2.play();

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

