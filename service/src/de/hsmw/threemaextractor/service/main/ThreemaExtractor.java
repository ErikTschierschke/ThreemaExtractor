package de.hsmw.threemaextractor.service.main;

import de.hsmw.threemaextractor.service.data.ContactAvatarFile;
import de.hsmw.threemaextractor.service.file.UserProfile;
import de.hsmw.threemaextractor.service.file.MainDatabase;
import de.hsmw.threemaextractor.service.file.MasterKey;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * application entry point
 */
public class ThreemaExtractor {

    private final MainDatabase mainDatabase;
    private final UserProfile userProfile;
    private ContactAvatarFile userAvatar;

    public ThreemaExtractor(FileStore fileStore) {
        MasterKey masterKey = new MasterKey(fileStore.masterKeyFile());
        mainDatabase = new MainDatabase(fileStore.databaseFile(), masterKey, fileStore.mediaDir());
        userProfile = new UserProfile(fileStore.preferencesFile());

        try {
            File userAvatarFile = ContactAvatarFile.getFileByIdentity(fileStore.mediaDir(), userProfile.getIdentity(), true);
            userAvatar = new ContactAvatarFile(userAvatarFile, masterKey);
        } catch (FileNotFoundException e) {
            System.out.println("[WARNING] No user avatar found. The file was either deleted or the user didn't set an avatar.");
        } catch (IOException e) {
            System.err.println("ERROR READING USER AVATAR FILE");
            e.printStackTrace();
        }
    }

    public MainDatabase getMainDatabase() {
        return mainDatabase;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public ContactAvatarFile getUserAvatarFile() {
        return userAvatar;
    }
}
