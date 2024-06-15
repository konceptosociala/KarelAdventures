package org.konceptosociala.kareladventures.utils;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;

public class AudioManager {
    // Music
    public final AudioNode mainTheme;

    // SFX
    public final AudioNode button1;
    public final AudioNode button2;
    public final AudioNode uiError1;
    public final AudioNode ui1;
    public final AudioNode fillUp;
    public final AudioNode victory;

    public AudioManager(AssetManager assetManager) {
        mainTheme = new AudioNode(assetManager, "Music/Karel, my Karel (instrumental).ogg", DataType.Stream);
        mainTheme.setPositional(false);

        button1 = new AudioNode(assetManager, "Sounds/button1.ogg", DataType.Buffer);
        button1.setPositional(false);
        button2 = new AudioNode(assetManager, "Sounds/button2.ogg", DataType.Buffer);
        button2.setPositional(false);
        uiError1 = new AudioNode(assetManager, "Sounds/ui_close1.ogg", DataType.Buffer);
        uiError1.setPositional(false);
        ui1 = new AudioNode(assetManager, "Sounds/ui1.ogg", DataType.Buffer);
        ui1.setPositional(false);
        fillUp = new AudioNode(assetManager, "Sounds/fill_up.ogg", DataType.Buffer);
        fillUp.setPositional(false);
        victory = new AudioNode(assetManager, "Sounds/victory.ogg", DataType.Buffer);
        victory.setPositional(false);
    }
}
