package org.konceptosociala.kareladventures.utils;

import java.io.*;
import java.util.*;

import org.konceptosociala.kareladventures.game.inventory.Inventory;
import org.konceptosociala.kareladventures.game.npc.Dialog;
import org.konceptosociala.kareladventures.game.player.Energy;
import org.konceptosociala.kareladventures.game.player.Health;

import com.jme3.math.Vector3f;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class SaveLoader implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final Health playerHealth;
    private final Energy playerEnergy;
    private final int balance;
    private final Inventory playerInventory;  
    private final HashMap<String, Dialog> dialogs;
    private final Vector3f playerPosition;
    private final Level currentLevel;

    public static SaveLoader load(String fileName) throws SaveLoadException {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            SaveLoader sl = (SaveLoader) objectInputStream.readObject();
            objectInputStream.close(); 

            return sl;
        } catch (Exception e) {
            throw new SaveLoadException(e.getMessage());
        }
    }

    public void save(String fileName) throws SaveLoadException {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (Exception e) {
            throw new SaveLoadException(e.getMessage());
        }
    }
}
