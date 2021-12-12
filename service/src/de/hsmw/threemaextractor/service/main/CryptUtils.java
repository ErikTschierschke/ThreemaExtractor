package de.hsmw.threemaextractor.service.main;

import de.hsmw.threemaextractor.service.file.MasterKey;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptUtils {

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
