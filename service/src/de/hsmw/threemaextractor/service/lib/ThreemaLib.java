package de.hsmw.threemaextractor.service.lib;

/*
 * The code in this file is derived from the Threema-Android project. (https://github.com/threema-ch/threema-android)
 *
 * It is subject to the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation. You may obtain a copy of the license at https://www.gnu.org/licenses/.
 * */


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ThreemaLib {

    // see https://github.com/threema-ch/threema-android/blob/81e456bc913cd8eaa5195219482f4445f409c344/app/src/main/java/ch/threema/localcrypto/MasterKey.java#L79
    private static final byte[] OBFUSCATION_KEY = new byte[]{(byte) 0x95, (byte) 0x0d, (byte) 0x26, (byte) 0x7a,
            (byte) 0x88, (byte) 0xea, (byte) 0x77, (byte) 0x10, (byte) 0x9c, (byte) 0x50, (byte) 0xe7, (byte) 0x3f,
            (byte) 0x47, (byte) 0xe0, (byte) 0x69, (byte) 0x72, (byte) 0xda, (byte) 0xc4, (byte) 0x39, (byte) 0x7c,
            (byte) 0x99, (byte) 0xea, (byte) 0x7e, (byte) 0x67, (byte) 0xaf, (byte) 0xfd, (byte) 0xdd, (byte) 0x32,
            (byte) 0xda, (byte) 0x35, (byte) 0xf7, (byte) 0x0c};


    /**
     * Load and deobfuscate key from key.dat,
     * partially adapted from https://github.com/threema-ch/threema-android/blob/23f00dbb2ef2869a12f95f2cb285740f0aef0154/app/src/main/java/ch/threema/localcrypto/MasterKey.java#L410
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
                // master key encrypted with passphrase - not supported
                throw new UnsupportedOperationException("Master key is encrypted.");
            }


            byte[] key = new byte[32];
            dis.readFully(key);

            // deobfuscation
            for (int i = 0; i < 32; i++)
                key[i] ^= OBFUSCATION_KEY[i];

            return key;
        }
    }

    /**
     * adapted from https://github.com/threema-ch/threema-android/blob/81e456bc913cd8eaa5195219482f4445f409c344/app/src/main/java/ch/threema/client/Utils.java#L52
     */
    public static String byteArrayToHexString(byte[] bytes) {

        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
