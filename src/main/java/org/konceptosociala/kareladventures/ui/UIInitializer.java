package org.konceptosociala.kareladventures.ui;

import org.konceptosociala.kareladventures.KarelAdventures;

import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;

public class UIInitializer {
    public static void initUI(KarelAdventures app) {
        initMainMenu(app);
    }

    private static void initMainMenu(KarelAdventures app) {
        var nifty = app.getNifty();

        nifty.addScreen("main_menu_screen", new ScreenBuilder("Hello Nifty Screen") {{
            controller(app.getMainMenuState());

            layer(new LayerBuilder("main_menu_layer") {{
                childLayoutVertical();

                panel(new ImageButton("main_menu_play_button", "Play", "playGame()"));
                panel(new ImageButton("main_menu_play_button", "Settings", "openSettings()"));
                panel(new ImageButton("main_menu_quit_button", "Quit", "quitGame()"));
                
            }});
            
        }}.build(nifty));
    }
}
