package de.hsmw.threemaextractor.service.main;

import de.hsmw.threemaextractor.service.file.MasterKey;

import java.io.File;
import java.io.IOException;

/**
 * utility class for checking and storing file locations
 */
public final class FileStore {
    private final File databaseFile;
    private final File preferencesFile;
    private final File mediaDir;
    private final File outputDir;


    private final MasterKey masterKey;

    /**
     * initialize FileStore by given path strings
     * <p>
     *
     * @param masterKeyPath   path to master key file ({@code [ANDROID APP DIR]/ch.threema.app/files/key.dat})
     * @param databasePath    path to main database file ({@code [ANDROID APP DIR]/ch.threema.app/databases/threema4.db})
     * @param preferencesPath path to app preferences file ({@code [ANDROID APP DIR]/ch.threema.app/shared_prefs/ch.threema.app_preferences.xml})
     * @param mediaDir        media path ({@code [ANDROID MEDIA DIR]/ch.threema.app/files/data/})
     * @param outputDir       path where decrypted files should be saved
     * @throws IOException if a path does not exist or is not readable. - Paths can be checked with {@link #checkFilePresent(String)}
     */
    public FileStore(String masterKeyPath, String databasePath, String preferencesPath, String mediaDir, String outputDir) throws IOException {

        masterKey = new MasterKey(new File(masterKeyPath));


        // throw IOException if a path is incorrect
        if (!(checkFilePresent(masterKeyPath) &&
                checkFilePresent(databasePath) &&
                checkFilePresent(preferencesPath) &&
                checkFilePresent(mediaDir) &&
                checkFilePresent(outputDir))) {
            throw new IOException("[ERROR] One of the provided paths was incorrect!");
        }

        databaseFile = new File(databasePath);
        preferencesFile = new File(preferencesPath);
        this.mediaDir = new File(mediaDir);
        this.outputDir = new File(outputDir);

    }

    /**
     * @return <b>true</b> if a file is present and readable, <b>false</b> else
     */
    public static boolean checkFilePresent(String path) {
        File file = new File(path);
        return file.exists() && file.canRead();
    }

    public MasterKey getMasterKey() {
        return masterKey;
    }

    public File getDatabaseFile() {
        return databaseFile;
    }

    public File getPreferencesFile() {
        return preferencesFile;
    }

    public File getMediaDir() {
        return mediaDir;
    }

    public File getOutputDir() {
        return outputDir;
    }

    /**
     * @return <b>true</b> if master key is protected by passphrase, <b>false</b> else
     */
    public boolean masterKeyNeedsPassphrase() {
        return masterKey.needsPassphrase();
    }

    /**
     * try to set the passphrase to unlock the master key
     *
     * @return <b>true</b> if passphrase is correct, <b>else</b>
     */
    public boolean setPassphrase(String passphrase) {
        return masterKey.setPassphrase(passphrase);
    }
}
