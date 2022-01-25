package de.hsmw.threemaextractor.service.main;

import de.hsmw.threemaextractor.service.file.MainDatabase;
import de.hsmw.threemaextractor.service.file.MasterKey;
import de.hsmw.threemaextractor.service.file.UserProfile;

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
        MasterKey masterKey = fileStore.getMasterKey();
        mainDatabase = new MainDatabase(fileStore.getDatabaseFile(), masterKey, fileStore.getMediaDir());
        userProfile = new UserProfile(fileStore.getPreferencesFile(), masterKey, fileStore.getMediaDir());

        new MediaHandler(fileStore.getMediaDir(), masterKey, mainDatabase, userProfile).saveAllMedia(fileStore.getOutputDir());

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
