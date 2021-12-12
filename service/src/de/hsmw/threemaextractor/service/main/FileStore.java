package de.hsmw.threemaextractor.service.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * utility class for checking and storing file locations
 */
public record FileStore(File masterKeyFile, File databaseFile, File preferencesFile, File mediaDir, File outputDir) {

    /**
     * initialize FileStore by given path strings
     */
    public FileStore(String masterKeyPath, String databasePath, String preferencesPath, String mediaDir, String outputDir) {
        this(new File(masterKeyPath), new File(databasePath), new File(preferencesPath), new File(mediaDir), new File(outputDir));
    }

    /**
     * check if a file is present
     */
    public static CheckResult checkFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return CheckResult.OK;
        }
        return CheckResult.MISSING;
    }

    /**
     * check if master key is present and not encrypted by passphrase
     */
    public static CheckResult checkMasterkeyFile(String path) {

        File file = new File(path);
        try {

            // if first byte of master key != 0, it's encrypted with passphrase
            FileInputStream fis = new FileInputStream(file);
            int encryptionFlag = fis.read();
            if (encryptionFlag == 0) {
                return CheckResult.OK;
            }
            return CheckResult.MASTERKEY_ENCRYPTED;
        } catch (IOException e) {
            return CheckResult.MISSING;
        }
    }

    public enum CheckResult {
        OK, MISSING, MASTERKEY_ENCRYPTED
    }
}
