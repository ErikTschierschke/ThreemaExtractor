package de.hsmw.threemaextractor.service.main;

import de.hsmw.threemaextractor.service.file.UserProfile;
import de.hsmw.threemaextractor.service.file.MainDatabase;
import de.hsmw.threemaextractor.service.file.MasterKey;

/**
 * service entry point
 */
public class ThreemaExtractor {

    private final MainDatabase mainDatabase;
    private final UserProfile userProfile;

    /**
     * takes a {@link FileStore} and extracts all available data
     */
    public ThreemaExtractor(FileStore fileStore) {
        MasterKey masterKey = new MasterKey(fileStore.masterKeyFile());
        mainDatabase = new MainDatabase(fileStore.databaseFile(), masterKey, fileStore.mediaDir());
        userProfile = new UserProfile(fileStore.preferencesFile(), masterKey, fileStore.mediaDir());

        new MediaHandler(fileStore.mediaDir(), masterKey, mainDatabase, userProfile).saveAllMedia(fileStore.outputDir());

    }

    /**
     * @return the decrypted and parsed main database
     * @see MainDatabase
     */
    public MainDatabase getMainDatabase() {
        return mainDatabase;
    }

    /**
     * @return the user profile information parsed from the app preference file
     * @see UserProfile
     */
    public UserProfile getUserProfile() {
        return userProfile;
    }
}
