package de.hsmw.threemaextractor.service.file;

import de.hsmw.threemaextractor.service.lib.ThreemaLib;

import java.io.File;
import java.io.IOException;

public class MasterKey {

    private byte[] key;

    /**
     * Loads and deobfuscates master key from key.dat
     */
    public MasterKey(File masterKeyFile) {

        try {
            key = ThreemaLib.readKeyFromFile(masterKeyFile);
        } catch (IOException e) {
            System.err.println("ERROR READING MASTER KEY.");
            e.printStackTrace();
        }
    }


    /**
     * Derives the threema4.db encryption key from the master key
     * see https://github.com/threema-ch/threema-android/blob/0b6543eafe325c37d25ae06e87802c5479bee099/app/src/main/java/ch/threema/app/ThreemaApplication.java#L802
     */
    public String getDatabaseKey() {
        return ThreemaLib.byteArrayToHexString(key);
    }

    public byte[] getKey() {
        return key;
    }
}
