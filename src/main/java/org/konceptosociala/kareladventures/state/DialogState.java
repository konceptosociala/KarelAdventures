package org.konceptosociala.kareladventures.state;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.game.npc.DialogMessage;
import org.konceptosociala.kareladventures.game.npc.NPC;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import lombok.Getter;
import lombok.Setter;   

@Getter
@Setter
public class DialogState extends BaseAppState implements ScreenController {
    private KarelAdventures app;
    private InputManager inputManager;
    private Nifty nifty;

    private Optional<NPC> npc = Optional.empty();
    private DialogMessage currentMessage;
    private int messageIndex = 0;

    private TextRenderer authorText;
    private TextRenderer messageText;

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.inputManager = this.app.getInputManager();
        this.nifty = this.app.getNifty();
    }

    @SuppressWarnings("null")
    @Override
    protected void onEnable() {
        if (npc.isEmpty() || npc.get().getDialog().getMessages().isEmpty()) {
            setEnabled(false);
            return;
        }

        var dialog = npc.get().getDialog();
        inputManager.setCursorVisible(true);
        currentMessage = dialog.getMessages().get(0);
        messageIndex = 0;

        nifty.addScreen("dialog_screen", new ScreenBuilder("Dialog screen") {{
            controller(DialogState.this);

            layer(new LayerBuilder("dialog_layer") {{
                childLayoutCenter();

                image(new ImageBuilder("dialog_image") {{
                    filename("Interface/UI/Transparent center/panel-transparent-center-003.png");
                    imageMode("resize:16,16,16,16,16,16,16,16,16,16,16,16");
                    width("960px");
                    height("360px");
                }});

                panel(new PanelBuilder("dialog_text") {{
                    childLayoutVertical();
                    width("960px");
                    height("360px");

                    text(new TextBuilder("dialog_author") {{
                        text(currentMessage.getAuthor());
                        textHAlign(Align.Left);
                        textVAlign(VAlign.Top);
                        width("100%");
                        height("25px");
                        color(Color.BLACK);
                        font("Interface/Fonts/Ubuntu-C.ttf");
                        marginTop("25px");
                        marginLeft("50px");
                    }});

                    text(new TextBuilder("dialog_message") {{
                        text(currentMessage.getMessage());
                        textHAlign(Align.Left);
                        textVAlign(VAlign.Top);
                        width("100%");
                        height("335px");
                        color(Color.BLACK);
                        font("Interface/Fonts/Ubuntu-C.ttf");
                        wrap(true);
                        margin("30px");
                    }});
                }});
            }});
        }}.build(nifty));

        nifty.gotoScreen("dialog_screen");

        authorText = nifty
            .getScreen("dialog_screen")
            .findElementById("dialog_author")
            .getRenderer(TextRenderer.class);

        messageText = nifty
            .getScreen("dialog_screen")
            .findElementById("dialog_message")
            .getRenderer(TextRenderer.class);
    }

    // UI callbacks

    public void nextMessage() {
        if (npc.isEmpty() || npc.get().getDialog().getMessages().isEmpty()) {
            setEnabled(false);
            return;
        }

        var dialog = npc.get().getDialog();
        var messages = dialog.getMessages();

        messageIndex++;
        if (messageIndex < messages.size()) {
            currentMessage = messages.get(messageIndex);
            authorText.setText(currentMessage.getAuthor());
            messageText.setText(currentMessage.getMessage());
        } else {
            var nextDialog = dialog.getNextDialog();
            if (nextDialog != null)
                npc.get().setDialog(nextDialog);

            setEnabled(false);
        }
    }

    // Other methods

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
