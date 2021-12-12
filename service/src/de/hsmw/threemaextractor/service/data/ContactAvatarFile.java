package de.hsmw.threemaextractor.service.data;

import de.hsmw.threemaextractor.service.lib.Base32;
import de.hsmw.threemaextractor.service.main.CryptUtils;
import de.hsmw.threemaextractor.service.main.MasterKey;

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
     * name format: ".c-" + [base32 encoded sha-256 hash of "c-" + identity]  + ".nomedia"
     */
    public static File getFileByIdentity(String mediaDir, String identity) throws FileNotFoundException {
        File file = new File(mediaDir, ".avatar/.c-" + Base32.encode(CryptUtils.sha256("c-" + identity)) + ".nomedia");

        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        return file;
    }
}
