package de.hsmw.threemaextractor.service.data;

import de.hsmw.threemaextractor.service.data.group.GroupAvatar;
import de.hsmw.threemaextractor.service.main.CryptUtils;
import de.hsmw.threemaextractor.service.file.MasterKey;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * represents contact and group avatars
 * @see ContactAvatar
 * @see GroupAvatar
 */
public abstract class AbstractAvatar {

    private final RenderedImage avatar;

    /**
     * initialize and decrypt the avatar
     *
     * @param avatarFile encrypted avatar file (see {@link ContactAvatar#getFileByIdentity(File, String, boolean)},
     *                   {@link GroupAvatar#getFileByGroupId(File, int)})
     * @param masterKey master key
     * @throws IOException if an invalid file is specified
     */
    public AbstractAvatar(File avatarFile, MasterKey masterKey) throws IOException {
        avatar = ImageIO.read(new ByteArrayInputStream(
                CryptUtils.getCipherInputStream(
                        new FileInputStream(avatarFile), masterKey).readAllBytes()
        ));
    }

    /**
     * @return the avatar as {@code RenderedImage}
     */
    public RenderedImage getAvatar() {
        return avatar;
    }

    /**
     * writes the avatar to a specific jpg file
     *
     * @param outputDir the output dir
     * @param fileName the file name (excluding {@code .jpg})
     * @throws IOException if the file could't be created
     */
    public void writeToFile(File outputDir, String fileName) throws IOException {
    	File outFile = new File(outputDir, fileName + ".jpg");
    	outFile.getParentFile().mkdirs();
        ImageIO.write(avatar, "jpg", outFile);
    }
}
