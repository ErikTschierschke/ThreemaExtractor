package de.hsmw.threemaextractor.service.data;

import de.hsmw.threemaextractor.service.main.CryptUtils;
import de.hsmw.threemaextractor.service.main.MasterKey;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class AbstractAvatarFile {

    private final byte[] avatarData;

    public AbstractAvatarFile(File avatarFile, MasterKey masterKey) throws IOException {
        avatarData = CryptUtils.getCipherInputStream(
                new FileInputStream(avatarFile), masterKey).readAllBytes();
    }

    public BufferedImage getAvatarImage() {
        try {
            return ImageIO.read(new ByteArrayInputStream(avatarData));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
