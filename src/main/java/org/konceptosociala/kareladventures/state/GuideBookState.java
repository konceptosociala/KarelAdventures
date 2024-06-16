package org.konceptosociala.kareladventures.state;

import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.ui.GuideBookTip;
import org.konceptosociala.kareladventures.ui.InterfaceBlur;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;

public class GuideBookState extends BaseAppState {
    private KarelAdventures app;
    private InputManager inputManager;
    private Nifty nifty;
    private InterfaceBlur interfaceBlur;

    public GuideBookState(InterfaceBlur blur) {
        this.interfaceBlur = blur;
    }

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.inputManager = this.app.getInputManager();
        this.nifty = this.app.getNifty();
    }

    @Override
    protected void onEnable() {
        interfaceBlur.setEnabled(true);

        nifty.addScreen("guidebook_screen", new ScreenBuilder("Guidebook screen") {{
            layer(new LayerBuilder("guidebook_layer") {{
                childLayoutCenter();

                image(new ImageBuilder("guidebook_bg") {{
                    width("960px");
                    height("640px");
                    filename("Interface/UI/Transparent center/panel-transparent-center-013.png");
                    imageMode("resize:16,16,16,16,16,16,16,16,16,16,16,16");
                }});
                
                panel(new PanelBuilder("guidebook_panel") {{
                    childLayoutHorizontal();
                    width("960px");
                    height("640px");

                    panel(new PanelBuilder("guidebook_page_left") {{
                        childLayoutVertical();
                        width("480px");
                        height("640px");
                        padding("50px");

                        panel(new GuideBookTip("W", "йти вперед", "keyboard_w"));
                        panel(new GuideBookTip("S", "йти назад", "keyboard_s"));
                        panel(new GuideBookTip("A", "йти вліво", "keyboard_a"));
                        panel(new GuideBookTip("D", "йти вправо", "keyboard_d"));
                        panel(new GuideBookTip("G", "відкрити довідник", "keyboard_g"));
                        panel(new GuideBookTip("F6", "зберегтися", "keyboard_f6"));
                    }});

                    panel(new PanelBuilder("guidebook_page_right") {{
                        childLayoutVertical();
                        width("480px");
                        height("540px");
                        padding("50px");

                        panel(new GuideBookTip("ESC", "пауза/закрити інтерфейси", "keyboard_escape"));
                        panel(new GuideBookTip("E",   "взаємодія (НІПи, Karel Farm)", "keyboard_e"));
                        panel(new GuideBookTip("I",   "інвентар", "keyboard_i"));
                        panel(new GuideBookTip("LMB", "удар/предмет в інвентарі", "mouse_left"));
                        panel(new GuideBookTip("RMB", "видалити предмет з інвентаря", "mouse_right"));
                    }});
                }});
            }});
        }}.build(nifty));

        nifty.gotoScreen("guidebook_screen");
    }

    @Override
    protected void onDisable() {
        inputManager.setCursorVisible(false);
        interfaceBlur.setEnabled(false);
        nifty.gotoScreen("hud_screen");
    }

    @Override
    protected void cleanup(Application app) {
    }
}
