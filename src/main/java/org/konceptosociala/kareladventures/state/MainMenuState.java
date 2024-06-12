package org.konceptosociala.kareladventures.state;

import java.io.File;
import java.util.Optional;

import javax.annotation.Nonnull;
import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.state.LoadGameState.LoadType;
import org.konceptosociala.kareladventures.ui.FadePanel;
import org.konceptosociala.kareladventures.ui.ImageButton;
import org.konceptosociala.kareladventures.ui.Logo;
import org.konceptosociala.kareladventures.ui.Margin;
import org.konceptosociala.kareladventures.ui.MsgBox;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData.DataType;
import com.jme3.input.InputManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;

public class MainMenuState extends BaseAppState implements ScreenController {
    private KarelAdventures app;
    private AppStateManager stateManager;
    private InputManager inputManager;
    private AssetManager assetManager;
    private AudioNode mainTheme;
    private Nifty nifty;

    private Optional<PanelRenderer> fadePanel;
    private float fadeOpacity;
    private boolean msgBoxVisible;

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.assetManager = this.app.getAssetManager();
        this.nifty = this.app.getNifty();

        mainTheme = new AudioNode(assetManager, "Music/Karel, my Karel (instrumental).ogg", DataType.Stream);
        mainTheme.setPositional(false);
    }

    @Override
    protected void onEnable() {
        inputManager.setCursorVisible(true);
        fadePanel = Optional.empty();
        fadeOpacity = 1.0f;

        nifty.addScreen("main_menu_screen", new ScreenBuilder("Main menu screen") {{
            controller(MainMenuState.this);

            layer(new LayerBuilder("main_menu_layer") {{
                childLayoutCenter();
                backgroundImage("Interface/UI/menu_bg.png");

                panel(new PanelBuilder("main_menu_panel") {{
                    childLayoutVertical();

                    panel(Margin.vertical("10%"));
                    panel(new Logo("main_logo", "Interface/UI/logo2.png"));

                    panel(new PanelBuilder("main_menu_controls_panel"){{
                        childLayoutCenter();

                        image(new ImageBuilder("main_menu_panel_bg") {{
                            filename("Interface/UI/Transparent center/panel-transparent-center-010.png");
                            imageMode("resize:16,16,16,16,16,16,16,16,16,16,16,16");
                            width("450px");
                            height("80%h");
                        }});

                        panel(new PanelBuilder("main_menu_panel_buttons"){{
                            childLayoutVertical();

                            panel(new ImageButton("main_menu_new_button", "Нова гра", null, "newGame()"));
                            panel(new ImageButton("main_menu_load_button", "Завантажити гру", null, "loadGame()"));
                            panel(new ImageButton("main_menu_settings_button", "Налаштування", null, "openSettings()"));
                            panel(new ImageButton("main_menu_quit_button", "Вийти", null, "quitGame()"));
                        }});
                    }});
                }});

                panel(new FadePanel("main_menu_fade"));
                
            }});

            layer(new MsgBox("msgbox", "hideMsgBox()"));
            
        }}.build(nifty));

        nifty.gotoScreen("main_menu_screen");

        mainTheme.play();
    }

    @SuppressWarnings("null")
    @Override
    public void update(float tpf) {
        if (fadeOpacity <= 0) return;

        if (fadePanel.isEmpty()) {
            fadePanel = Optional.of(
                nifty
                    .getScreen("main_menu_screen")
                    .findElementById("main_menu_fade")
                    .getRenderer(PanelRenderer.class)
            );
        }

        fadeOpacity -= 0.01f;
        fadePanel.get().setBackgroundColor(new Color(0, 0, 0, fadeOpacity));
    }

    // UI callbacks

    public void newGame() {
        if (msgBoxVisible) return;

        setEnabled(false);
        stateManager.attach(new LoadGameState(LoadType.NewGame));
    }

    public void loadGame() {
        if (msgBoxVisible) return;

        if (new File("data/Saves/karel.sav").exists()) {
            setEnabled(false);
            stateManager.attach(new LoadGameState(LoadType.Saving));
        } else {
            showMsgBox("Помилка", "Файл 'data/Saves/karel.sav' не знайдено.");
        }
    }

    public void openSettings() {
        if (msgBoxVisible) return;


    }

    public void quitGame() {
        if (msgBoxVisible) return;

        app.stop();
    }

    @SuppressWarnings("null")
    public void showMsgBox(String title, String message) {
        nifty
            .getCurrentScreen()
            .findElementById("msgbox")
            .setVisible(true);

        msgBoxVisible = true;

        MsgBox.setData(nifty, "msgbox", title, message);
    }

    @SuppressWarnings("null")
    public void hideMsgBox() {
        nifty
            .getCurrentScreen()
            .findElementById("msgbox")
            .setVisible(false);

        msgBoxVisible = false;
    }

    // Other methods

    @Override
    protected void onDisable() {
        mainTheme.stop();
    }
    
    @Override
    protected void cleanup(Application app) {
    }

    @Override
    public void bind(@Nonnull final Nifty nifty, @Nonnull final Screen screen) {
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
