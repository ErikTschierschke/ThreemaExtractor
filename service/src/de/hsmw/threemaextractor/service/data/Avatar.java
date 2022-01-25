package de.hsmw.threemaextractor.service.data;

import de.hsmw.threemaextractor.service.file.MasterKey;
import de.hsmw.threemaextractor.service.lib.Base32;
import de.hsmw.threemaextractor.service.main.CryptUtils;
import de.hsmw.threemaextractor.service.main.FileStore;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;

/**
 * represents contact and group avatars
 */
public class Avatar {

    private final RenderedImage avatar;

    /**
     * initialize and decrypt the avatar
     *
     * @param avatarFile encrypted avatar file (see {@link #getContactAvatarFile(File, String, boolean)},
     *                   {@link #getGroupAvatarFile(File, int)} )
     * @param masterKey  master key
     * @throws IOException if an invalid file is specified
     */
    public Avatar(File avatarFile, MasterKey masterKey) throws IOException {
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
     * @param fileName  the file name (excluding {@code .jpg})
     * @throws IOException if the file couldn't be created
     */
    public void writeToFile(File outputDir, String fileName) throws IOException {
        File outFile = new File(outputDir, fileName + ".jpg");
        outFile.getParentFile().mkdirs();
        ImageIO.write(avatar, "jpg", outFile);
    }

    /**
     * returns the associated encrypted file of a contact avatar
     *
     * @param mediaDir media dir (see {@link FileStore#getMediaDir()})
     * @param identity Threema ID of the desired contact
     * @param isUser   whether the desired contact is the user themself
     * @throws FileNotFoundException if the file is not present (contact has no avatar set or file was deleted)
     */
    public static File getContactAvatarFile(File mediaDir, String identity, boolean isUser) throws FileNotFoundException {

        // name format: ".avatar/.p-" + [base32 encoded sha-256 hash of "c-" + identity]  + ".nomedia"
        // (for user the prefix is .c)
        String prefix = isUser ? ".avatar/.c-" : ".avatar/.p-";

        File file = new File(mediaDir, prefix + Base32.encode(CryptUtils.sha256("c-" + identity)) + ".nomedia");

        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        return file;
    }

    /**
     * returns the associated encrypted file of a group avatar
     *
     * @param mediaDir media dir (see {@link FileStore#getMediaDir()})
     * @param groupId  the ID of the desired group
     * @throws FileNotFoundException if the file is not present (group has no avatar set or file was deleted)
     */
    public static File getGroupAvatarFile(File mediaDir, int groupId) throws FileNotFoundException {
        File file = new File(mediaDir, ".grp-avatar/.grp-avatar-" + groupId);

        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        return file;
    }
}
