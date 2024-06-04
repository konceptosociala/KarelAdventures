package org.konceptosociala.kareladventures.state;

import javax.annotation.Nonnull;

import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.game.npc.Dialog;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class DialogState extends BaseAppState implements ScreenController {
    private KarelAdventures app;
    private InputManager inputManager;
    private Nifty nifty;
    private Dialog dialog;

    public DialogState(Dialog dialog) {
       this.dialog = dialog;
    }

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.inputManager = this.app.getInputManager();
        this.nifty = this.app.getNifty();
    }

    @Override
    protected void onEnable() {
        inputManager.setCursorVisible(true);

        nifty.addScreen("dialog_screen", new ScreenBuilder("Dialog screen") {{
            controller(DialogState.this);

            layer(new LayerBuilder("dialog_layer") {{
                childLayoutCenter();

                image(new ImageBuilder("dialog_image") {{
                    filename("Interface/dialog.png");
                    width("90%");
                }});
            }});
        }}.build(nifty));
    }

    @Override
    protected void onDisable() {
        inputManager.setCursorVisible(false);
        nifty.gotoScreen("hud_screen");
    }

    public void gotoScreen(@Nonnull final String screenId) {
        nifty.gotoScreen(screenId);
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
