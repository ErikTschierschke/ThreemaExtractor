package de.hsmw.threemaextractor.service.lib;

/*
 * The code in this file is derived from the Threema-Android project. (https://github.com/threema-ch/threema-android)
 *
 * It is subject to the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation. A copy of the licence may be obtained at https://www.gnu.org/licenses/.
 * */


public class ThreemaLib {

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
