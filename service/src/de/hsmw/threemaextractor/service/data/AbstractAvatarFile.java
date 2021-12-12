package de.hsmw.threemaextractor.service.data;

import de.hsmw.threemaextractor.service.main.CryptUtils;
import de.hsmw.threemaextractor.service.file.MasterKey;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class AbstractAvatarFile {

    private final RenderedImage avatar;

    public AbstractAvatarFile(File avatarFile, MasterKey masterKey) throws IOException {
        avatar = ImageIO.read(new ByteArrayInputStream(
                CryptUtils.getCipherInputStream(
                        new FileInputStream(avatarFile), masterKey).readAllBytes()
        ));
    }

    public RenderedImage getAvatar() {
        return avatar;
    }

    public void writeToFile(File outputDir, String filePath) throws IOException {
        ImageIO.write(avatar, "jpg", new File(outputDir, filePath));
    }
}
