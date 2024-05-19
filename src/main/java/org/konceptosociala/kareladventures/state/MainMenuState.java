package org.konceptosociala.kareladventures.state;

import org.konceptosociala.kareladventures.KarelAdventures;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;

public class MainMenuState extends BaseAppState {
    private KarelAdventures app;
    private AssetManager assetManager;
    private AudioRenderer audioRenderer;
    private InputManager inputManager;
    private ViewPort guiViewPort;

    @Override
    protected void initialize(Application app) {
        this.app = (KarelAdventures) app;
        this.assetManager = this.app.getAssetManager();
        this.audioRenderer = this.app.getAudioRenderer();
        this.inputManager = this.app.getInputManager();
        this.guiViewPort = this.app.getGuiViewPort();
    }

    @Override
    protected void onEnable() {
        inputManager.setCursorVisible(true);

        Nifty nifty = initNifty();

        nifty.addScreen("Screen_ID", new ScreenBuilder("Hello Nifty Screen") {{
            controller(new DefaultScreenController());

            layer(new LayerBuilder("Layer_ID") {{
                childLayoutVertical();

                panel(new PanelBuilder("Panel_ID") {{
                    childLayoutCenter();

                    control(new ButtonBuilder("Button_ID", "Hello Nifty"){{
                        alignCenter();
                        valignCenter();
                        width("5%");
                        height("15%");
                    }});
                    
                }});
                
            }});
            
        }}.build(nifty));
        
        nifty.gotoScreen("Screen_ID");
    }

    @Override
    protected void onDisable() {

    }
    
    @Override
    protected void cleanup(Application app) {

    }

    private Nifty initNifty() {
        NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
            assetManager, 
            inputManager,
            audioRenderer,
            guiViewPort
        );

        Nifty nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        ((SimpleApplication) getApplication()).getFlyByCamera().setDragToRotate(true);

        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");

        return nifty;
    }
    
}
