package de.hsmw.threemaextractor.service.data.group;

import de.hsmw.threemaextractor.service.data.AbstractAvatar;
import de.hsmw.threemaextractor.service.file.MasterKey;
import de.hsmw.threemaextractor.service.main.FileStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * represents group avatar
 *
 * @see AbstractAvatar
 */
public class GroupAvatar extends AbstractAvatar {

    /**
     * see {@link AbstractAvatar#AbstractAvatar(File, MasterKey)}
     */
    public GroupAvatar(File avatarFile, MasterKey masterKey) throws IOException {
        super(avatarFile, masterKey);
    }

    /**
     * returns the associated encrypted avatar file
     *
     * @param mediaDir media dir (see {@link FileStore#mediaDir()})
     * @param groupId  the ID of the desired group
     * @throws FileNotFoundException if the file is not present (group has no avatar set or file was deleted)
     */
    public static File getFileByGroupId(File mediaDir, int groupId) throws FileNotFoundException {
        File file = new File(mediaDir, ".grp-avatar/.grp-avatar-" + groupId);

        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        return file;
    }
}
