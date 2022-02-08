package de.hsmw.threemaextractor.service.file;

import de.hsmw.threemaextractor.service.lib.ThreemaLib;
import de.hsmw.threemaextractor.service.main.CryptUtils;
import de.hsmw.threemaextractor.service.main.FileStore;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * represents master key from {@code key.dat}
 */
public class MasterKey {

    // see https://github.com/threema-ch/threema-android/blob/81e456bc913cd8eaa5195219482f4445f409c344/app/src/main/java/ch/threema/localcrypto/MasterKey.java#L79
    private static final byte[] OBFUSCATION_KEY = new byte[]{(byte) 0x95, (byte) 0x0d, (byte) 0x26, (byte) 0x7a,
            (byte) 0x88, (byte) 0xea, (byte) 0x77, (byte) 0x10, (byte) 0x9c, (byte) 0x50, (byte) 0xe7, (byte) 0x3f,
            (byte) 0x47, (byte) 0xe0, (byte) 0x69, (byte) 0x72, (byte) 0xda, (byte) 0xc4, (byte) 0x39, (byte) 0x7c,
            (byte) 0x99, (byte) 0xea, (byte) 0x7e, (byte) 0x67, (byte) 0xaf, (byte) 0xfd, (byte) 0xdd, (byte) 0x32,
            (byte) 0xda, (byte) 0x35, (byte) 0xf7, (byte) 0x0c};

    private byte protectionFlag;
    private byte[] key;
    private byte[] salt;
    private byte[] checksum;

    /**
     * Loads and deobfuscates master key from {@code key.dat}<p>
     *
     * @param masterKeyFile the key.dat file
     */
    public MasterKey(File masterKeyFile) {

        /*
         * KEY.DAT FILE STRUCTURE:
         * protection flag (1 byte, 0x02 = protected with passphrase (Scrypt), 0x01 = protected with passphrase (PBKDF2), 0x00 = unprotected)
         * key (32 bytes)
         * salt (8 bytes)
         * verification (4 bytes = start of SHA1 hash of master key)
         *
         * (see https://github.com/threema-ch/threema-android/blob/557b69f33dd1db96f58e41f6602e32522470f53e/app/src/main/java/ch/threema/localcrypto/MasterKey.java#L66)
         */

        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new FileInputStream(masterKeyFile));


            byte[] masterKey = new byte[32];

            protectionFlag = dis.readByte();
            key = dis.readNBytes(32);
            salt = dis.readNBytes(8);
            checksum = dis.readNBytes(4);

            // decode masterkey by XORing the obfuscation key
            for (int i = 0; i < 32; i++) {
                key[i] ^= OBFUSCATION_KEY[i];
            }
        } catch (IOException e) {
            // won't happen, if correctly checked with FileStore.checkFilePresent()
            e.printStackTrace();
        }
    }


    /**
     * Returns a hex string representation of the master key (usable in SQLCipher CLI)
     * see <a href=https://github.com/threema-ch/threema-android/blob/0b6543eafe325c37d25ae06e87802c5479bee099/app/src/main/java/ch/threema/app/ThreemaApplication.java#L802>ThreemaApplication.java</a>
     */
    public String getDatabaseKey() {
        return ThreemaLib.byteArrayToHexString(key);
    }

    /**
     * @return the master key (32 byte)
     */
    public byte[] getKey() {
        return key;
    }

    /**
     * @return <b>true</b> if the master key is protected with a passphrase, <b>false</b> else
     */
    public boolean needsPassphrase() {
        return protectionFlag != 0;
    }

    /**
     * sets a passphrase to decrypt the master key, verifies if passphrase is correct
     * @return <b>true</b> if passphrase is correct and decryption was successful, <b>false</b> else
     */
    public boolean setPassphrase(String passphrase) {

        if (protectionFlag == 0) {
            // no passphrase needed, do nothing
            return true;
        }

        // decrypt key with passphrase
        byte[] decryptionKey = new byte[32];
        try {

            decryptionKey = CryptUtils.getPassphraseDecryptionKey(protectionFlag, salt, passphrase);

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 32; i++) {
            key[i] ^= decryptionKey[i];
        }

        // compare with checksum to check if passphrase is correct
        String checksumString = new String(checksum);
        return checksumString.equals(CryptUtils.calcPassphraseChecksum(key));
    }


}
