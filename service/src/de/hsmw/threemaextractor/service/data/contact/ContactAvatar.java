package de.hsmw.threemaextractor.service.data.contact;

import de.hsmw.threemaextractor.service.data.AbstractAvatar;
import de.hsmw.threemaextractor.service.file.MasterKey;
import de.hsmw.threemaextractor.service.lib.Base32;
import de.hsmw.threemaextractor.service.main.CryptUtils;
import de.hsmw.threemaextractor.service.main.FileStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * represents avatar of contact
 *
 * @see AbstractAvatar
 */
public class ContactAvatar extends AbstractAvatar {


    /**
     * see {@link AbstractAvatar#AbstractAvatar(File, MasterKey)}
     */
    public ContactAvatar(File avatarFile, MasterKey masterKey) throws IOException {
        super(avatarFile, masterKey);
    }

    /**
     * returns the associated encrypted avatar file
     *
     * @param mediaDir media dir (see {@link FileStore#mediaDir()})
     * @param identity Threema ID of the desired contact
     * @param isUser   whether the desired contact is the user themself
     * @throws FileNotFoundException if the file is not present (contact has no avatar set or file was deleted)
     */
    /*
     * name format: ".avatar/.p-" + [base32 encoded sha-256 hash of "c-" + identity]  + ".nomedia"
     * (for user the prefix is .c)
     */
    public static File getFileByIdentity(File mediaDir, String identity, boolean isUser) throws FileNotFoundException {

        String prefix = isUser ? ".avatar/.c-" : ".avatar/.p-";

        File file = new File(mediaDir, prefix + Base32.encode(CryptUtils.sha256("c-" + identity)) + ".nomedia");

        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        return file;
    }
}
