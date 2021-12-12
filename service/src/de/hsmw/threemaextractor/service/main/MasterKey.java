package de.hsmw.threemaextractor.service.main;

import de.hsmw.threemaextractor.service.lib.ThreemaLib;

import java.io.File;
import java.io.IOException;

public class MasterKey {

    private final byte[] key;

    /**
     * Loads and deobfuscates master key from key.dat
     */
    public MasterKey(File masterKeyFile) throws IOException, UnsupportedOperationException {

        key = ThreemaLib.readKeyFromFile(masterKeyFile);
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
