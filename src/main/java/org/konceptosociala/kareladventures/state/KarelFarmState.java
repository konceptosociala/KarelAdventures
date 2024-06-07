package org.konceptosociala.kareladventures.state;

import javax.annotation.Nonnull;

import org.konceptosociala.kareladventures.KarelAdventures;
import org.konceptosociala.kareladventures.ui.ImageButton;
import org.konceptosociala.kareladventures.ui.Logo;
import org.konceptosociala.kareladventures.ui.Margin;
import org.konceptosociala.kareladventures.ui.labyrinth.Labyrinth;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.Size;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.textfield.builder.TextFieldBuilder;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;

public class KarelFarmState extends BaseAppState implements ScreenController {
    private KarelAdventures app;
    private InputManager inputManager;
    private Nifty nifty;

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.inputManager = this.app.getInputManager();
        this.nifty = this.app.getNifty();
    }

    @Override
    protected void onEnable() {
        inputManager.setCursorVisible(true);

        nifty.addScreen("karel_farm_screen", new ScreenBuilder("Karel farm screen") {{
            controller(KarelFarmState.this);

            layer(new LayerBuilder("karel_farm_layer") {{
                childLayoutCenter();

                panel(new PanelBuilder("karel_farm_panel") {{
                    childLayoutVertical();

                    panel(Margin.vertical("10%"));
                    panel(new Logo("karel_farm_logo", "Interface/karel_farm_logo.png"));

                    panel(new PanelBuilder("karel_farm_controls_main_panel") {{
                        childLayoutCenter();     
                        align(Align.Center);
                        
                        image(new ImageBuilder("karel_farm_controls_bg") {{
                            filename("Interface/UI/Transparent center/panel-transparent-center-020.png");
                            imageMode("resize:22,4,22,22,22,4,22,4,22,4,22,22");
                            width("900px");
                            height("75%");
                            align(Align.Center);
                        }});

                        panel(new PanelBuilder("karel_farm_controls") {{
                            childLayoutHorizontal();
                            align(Align.Center);
                            width("900px");
                            height("75%");  

                            panel(new PanelBuilder("code_editor") {{
                                childLayoutCenter();
                                width("50%");
                                height("75%");  

                                panel(new PanelBuilder("code_editor_inner") {{
                                    childLayoutVertical();
                                    width("90%");
                                    height("90%");

                                    text(new TextBuilder("code_field_label") {{
                                        height("50px");
                                        width("100%");
                                        text("Code editor");
                                        color(Color.BLACK);
                                        font("Interface/Fonts/Ubuntu-C.ttf");
                                    }});
    
                                    text(new TextBuilder("code_field_history") {{
                                        height("100%");
                                        width("100%");
                                        color(Color.BLACK);
                                        backgroundColor(Color.WHITE);
                                        font("Interface/Fonts/FiraCode-Bold.ttf");
                                        textHAlign(Align.Left);
                                        textVAlign(VAlign.Top);
                                        align(Align.Left);
                                        valign(VAlign.Top);
                                    }});
    
                                    panel(Margin.vertical("10px"));
    
                                    control(new TextFieldBuilder("code_field") {{
                                        width("100%");
                                    }});
                                }});
                            }});

                            panel(new PanelBuilder("labyrinth") {{
                                childLayoutCenter();
                                width("50%");
                                height("75%");

                                panel(new PanelBuilder("labyrinth_inner") {{
                                    childLayoutVertical();
                                    width("90%");
                                    height("90%");  

                                    text(new TextBuilder("labyrinth_label") {{
                                        height("50px");
                                        width("100%");
                                        text("Karel Labyrinth");
                                        color(Color.BLACK);
                                        font("Interface/Fonts/Ubuntu-C.ttf");
                                    }});

                                    panel(new PanelBuilder("labyrinth_panel") {{
                                        childLayoutVertical();
                                        width("100%");
                                        height("100%");

                                        panel(new Labyrinth("labyrinth_game"));

                                        panel(new PanelBuilder("labyrinth_controls") {{
                                            childLayoutHorizontal();

                                            panel(new ImageButton("labyrinth_controls_run", "Run", new Size(100, 50), "runLabyrinth()"));
                                            panel(new ImageButton("labyrinth_controls_reset", "Reset", new Size(100, 50), "resetLabyrinth()"));
                                            panel(new ImageButton("labyrinth_controls_rebuild", "Rebuild", new Size(100, 50), "rebuildLabyrinth()"));
                                        }});
                                    }});
                                }});
                            }});
                        }}); 
                    }});
                }});
            }});
        }}.build(nifty));

        nifty.gotoScreen("karel_farm_screen");
    }

    // UI callbacks

    void runLabyrinth() {

    }

    void resetLabyrinth() {

    }

    void rebuildLabyrinth() {
        
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
