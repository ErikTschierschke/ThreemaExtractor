package de.hsmw.threemaextractor.service.main;

import de.hsmw.threemaextractor.service.file.MasterKey;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptUtils {

    // see https://github.com/threema-ch/threema-android/blob/81e456bc913cd8eaa5195219482f4445f409c344/app/src/main/java/ch/threema/localcrypto/MasterKey.java#L79
    private static final byte[] OBFUSCATION_KEY = new byte[]{(byte) 0x95, (byte) 0x0d, (byte) 0x26, (byte) 0x7a,
            (byte) 0x88, (byte) 0xea, (byte) 0x77, (byte) 0x10, (byte) 0x9c, (byte) 0x50, (byte) 0xe7, (byte) 0x3f,
            (byte) 0x47, (byte) 0xe0, (byte) 0x69, (byte) 0x72, (byte) 0xda, (byte) 0xc4, (byte) 0x39, (byte) 0x7c,
            (byte) 0x99, (byte) 0xea, (byte) 0x7e, (byte) 0x67, (byte) 0xaf, (byte) 0xfd, (byte) 0xdd, (byte) 0x32,
            (byte) 0xda, (byte) 0x35, (byte) 0xf7, (byte) 0x0c};


    /**
     * Load and deobfuscate key from key.dat,
     * see https://github.com/threema-ch/threema-android/blob/23f00dbb2ef2869a12f95f2cb285740f0aef0154/app/src/main/java/ch/threema/localcrypto/MasterKey.java#L410
     */
    public static byte[] readKeyFromFile(File masterKeyFile) throws IOException, UnsupportedOperationException {

        /*
         * KEY.DAT FILE STRUCTURE:
         * protected flag (1 byte, 1 = protected with passphrase, 0 = unprotected)
         * key (32 bytes)
         * salt (8 bytes)
         * verification (4 bytes = start of SHA1 hash of master key)
         *
         * (see https://github.com/threema-ch/threema-android/blob/23f00dbb2ef2869a12f95f2cb285740f0aef0154/app/src/main/java/ch/threema/localcrypto/MasterKey.java#L61)
         */

        try (DataInputStream dis = new DataInputStream(new FileInputStream(masterKeyFile))) {


            boolean protectedFlag = dis.readBoolean();
            if (protectedFlag) {
                throw new UnsupportedOperationException("Master key is encrypted.");
            }


            byte[] key = new byte[32];
            dis.readFully(key);

            // decode masterkey by XORing the obfuscation key
            for (int i = 0; i < 32; i++) {
                key[i] ^= OBFUSCATION_KEY[i];
            }
            return key;
        }
    }

    /**
     * get cipher input stream capable of reading encrypted threema files
     * see https://github.com/threema-ch/threema-android/blob/0b6543eafe325c37d25ae06e87802c5479bee099/app/src/main/java/ch/threema/localcrypto/MasterKey.java#L348
     */
    public static CipherInputStream getCipherInputStream(FileInputStream fileInputStream, MasterKey masterKey) throws IOException {

        //first 16 bytes are used as init vector
        byte[] initVector = new byte[16];
        fileInputStream.read(initVector);

        return new CipherInputStream(fileInputStream,
                getDecryptCipher(masterKey, initVector));

    }

    // see https://github.com/threema-ch/threema-android/blob/0b6543eafe325c37d25ae06e87802c5479bee099/app/src/main/java/ch/threema/localcrypto/MasterKey.java#L411
    private static Cipher getDecryptCipher(MasterKey masterKey, byte[] initVector) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE,
                    new SecretKeySpec(masterKey.getKey(), "AES"),
                    new IvParameterSpec(initVector));

            return cipher;

        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * apply SHA-256 hash to string
     */
    public static byte[] sha256(String input) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            //won't happen
        }
        if (messageDigest != null) {
            messageDigest.update(input.getBytes());
            return messageDigest.digest();
        }
        return null;
    }
}
