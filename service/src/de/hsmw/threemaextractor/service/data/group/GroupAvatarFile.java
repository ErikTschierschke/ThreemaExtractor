package de.hsmw.threemaextractor.service.data.group;

import de.hsmw.threemaextractor.service.data.AbstractAvatarFile;
import de.hsmw.threemaextractor.service.file.MasterKey;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GroupAvatarFile extends AbstractAvatarFile {

    public GroupAvatarFile(File avatarFile, MasterKey masterKey) throws IOException {
        super(avatarFile, masterKey);
    }

    /**
     * get the avatar file associated with group id
     */
    public static File getFileNameByGroupId(File mediaDir, int groupId) throws FileNotFoundException {
        File file = new File(mediaDir, ".grp-avatar/.grp-avatar-" + groupId);

        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        return file;
    }
}
