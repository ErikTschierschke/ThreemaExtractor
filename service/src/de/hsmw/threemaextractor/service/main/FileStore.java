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
     * <p>
     * to prevent errors, check files first with {@link FileStore#checkFile(String)} and {@link FileStore#checkMasterkeyFile(String)}
     *
     * @param masterKeyPath path to master key file ({@code [ANDROID APP DIR]/ch.threema.app/files/key.dat})
     * @param databasePath path to main database file ({@code [ANDROID APP DIR]/ch.threema.app/databases/threema4.db})
     * @param preferencesPath path to app preferences file ({@code [ANDROID APP DIR]/ch.threema.app/shared_prefs/ch.threema.app_preferences.xml})
     * @param mediaDir media path ({@code [ANDROID MEDIA DIR]/ch.threema.app/files/data/})
     * @param outputDir path where decrypted files should be saved
     */
    public FileStore(String masterKeyPath, String databasePath, String preferencesPath, String mediaDir, String outputDir) {
        this(new File(masterKeyPath), new File(databasePath), new File(preferencesPath), new File(mediaDir), new File(outputDir));
    }

    /**
     * check if a file is present
     *
     * @return {@link CheckResult#OK} or {@link CheckResult#MISSING}
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
     *
     * @param path path to master key
     *
     * @return {@link CheckResult#OK} if master key is available and not protected,<p>
     *         {@link CheckResult#MISSING} if master key file is not found,<p>
     *         {@link CheckResult#MASTERKEY_ENCRYPTED} if master key is encrypted by passphrase
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
