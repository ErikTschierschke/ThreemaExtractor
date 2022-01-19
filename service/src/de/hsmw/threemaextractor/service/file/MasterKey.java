package de.hsmw.threemaextractor.service.file;

import de.hsmw.threemaextractor.service.lib.ThreemaLib;
import de.hsmw.threemaextractor.service.main.CryptUtils;

import java.io.File;
import java.io.IOException;

/**
 * represents master key from {@code key.dat}
 */
public class MasterKey {

    private byte[] key;

    /**
     * Loads and deobfuscates master key from {@code key.dat}
     */
    public MasterKey(File masterKeyFile) {

        try {
            key = CryptUtils.readKeyFromFile(masterKeyFile);
        } catch (IOException e) {
            System.err.println("ERROR READING MASTER KEY.");
            e.printStackTrace();
        }
    }


    /**
     * Derives the threema4.db encryption key from the master key<p>
     * see <a href=https://github.com/threema-ch/threema-android/blob/0b6543eafe325c37d25ae06e87802c5479bee099/app/src/main/java/ch/threema/app/ThreemaApplication.java#L802>ThreemaApplication.java</a>
     */
    public String getDatabaseKey() {
        return ThreemaLib.byteArrayToHexString(key);
    }

    public byte[] getKey() {
        return key;
    }
}
