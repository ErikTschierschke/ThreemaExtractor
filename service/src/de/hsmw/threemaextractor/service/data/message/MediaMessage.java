package de.hsmw.threemaextractor.service.data.message;

import de.hsmw.threemaextractor.service.main.CryptUtils;
import de.hsmw.threemaextractor.service.main.MasterKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * media message data class
 */
public record MediaMessage(String uid, String identity, boolean isOutgoing,
                           State state, Date utcSent, Date utcReceived, Date utcRead,
                           String plainFileName, String mimeType,
                           String caption) implements IMessage {

    /**
     * encryptedFileName is retrieved from uid
     */
    public String getEncryptedFileName() {
        return "." + uid.replaceAll("-", "");
    }

    /**
     * files may have the same plain name - prepend 4 digits of uid
     */
    public String getUniquePlainName() {
        return uid.substring(0, 4) + "-" + plainFileName;
    }

    /**
     * decrypts the associated file and saves it to the specified output dir
     */
    public void saveFile(MasterKey masterKey, String mediaDir, String outDir) throws IOException {

        FileInputStream fis = new FileInputStream(new File(mediaDir, getEncryptedFileName()));
        byte[] data = CryptUtils.getCipherInputStream(fis, masterKey).readAllBytes();
        fis.close();

        File outFile = new File(outDir, getUniquePlainName());
        FileOutputStream fos = new FileOutputStream(outFile);
        fos.write(data);
        fos.close();
    }

    @Override
    public int compareTo(IMessage message) {
        return utcSent.compareTo(message.utcSent());
    }
}
