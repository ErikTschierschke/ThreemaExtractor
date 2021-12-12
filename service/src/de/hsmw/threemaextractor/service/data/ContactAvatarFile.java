package de.hsmw.threemaextractor.service.data;

import de.hsmw.threemaextractor.service.lib.Base32;
import de.hsmw.threemaextractor.service.main.CryptUtils;
import de.hsmw.threemaextractor.service.file.MasterKey;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ContactAvatarFile extends AbstractAvatarFile {

    public ContactAvatarFile(File avatarFile, MasterKey masterKey) throws IOException {
        super(avatarFile, masterKey);
    }

    /**
     * get encrypted filename by user identity
     * <p>
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
