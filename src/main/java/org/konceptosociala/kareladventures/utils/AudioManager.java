package org.konceptosociala.kareladventures.utils;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;

public class AudioManager {
    // Music
    public final AudioNode mainTheme;
    public final AudioNode village;
    public final AudioNode wasteland;
    public final AudioNode boss;

    // SFX
    public final AudioNode button1;
    public final AudioNode button2;
    public final AudioNode uiError1;
    public final AudioNode ui1;
    public final AudioNode fillUp;
    public final AudioNode victory;
    public final AudioNode save;
    public final AudioNode walk;
    public final AudioNode attack;
    public final AudioNode death;
    public final AudioNode insectDeath;

    public AudioManager(AssetManager assetManager) {
        mainTheme = new AudioNode(assetManager, "Music/Karel, my Karel (instrumental).ogg", DataType.Stream);
        mainTheme.setPositional(false);
        village = new AudioNode(assetManager, "Music/Dead Town.ogg", DataType.Stream);
        village.setPositional(false);
        wasteland = new AudioNode(assetManager, "Music/Wasted Land.ogg", DataType.Buffer);
        wasteland.setLooping(true);
        wasteland.setPositional(false);
        boss = new AudioNode(assetManager, "Music/Karel, my Karel.ogg", DataType.Stream);
        boss.setPositional(false);

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
        save = new AudioNode(assetManager, "Sounds/save.ogg", DataType.Buffer);
        save.setPositional(false);
        walk = new AudioNode(assetManager, "Sounds/walk.ogg", DataType.Buffer);
        walk.setPositional(false);
        attack = new AudioNode(assetManager, "Sounds/attack.ogg", DataType.Buffer);
        attack.setPositional(false);
        death = new AudioNode(assetManager, "Sounds/death.ogg", DataType.Buffer);
        death.setPositional(false);
        insectDeath = new AudioNode(assetManager, "Sounds/insect_death.ogg", DataType.Buffer);
        insectDeath.setPositional(false);
    }
}
