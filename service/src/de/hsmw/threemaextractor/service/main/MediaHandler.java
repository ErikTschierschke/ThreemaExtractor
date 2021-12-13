package de.hsmw.threemaextractor.service.main;

import de.hsmw.threemaextractor.service.data.Contact;
import de.hsmw.threemaextractor.service.data.group.Group;
import de.hsmw.threemaextractor.service.data.message.IMessage;
import de.hsmw.threemaextractor.service.data.message.MediaMessage;
import de.hsmw.threemaextractor.service.file.MainDatabase;
import de.hsmw.threemaextractor.service.file.MasterKey;
import de.hsmw.threemaextractor.service.file.UserProfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * helper class to decrypt all available media
 */
public class MediaHandler {

    private static final String AVATAR_DIR = "avatars/";
    private static final String GROUP_AVATAR_DIR = "group_avatars/";
    private static final String MEDIA_DIR = "media/";
    private static final String GROUP_MEDIA_DIR = "group_media/";

    private final File mediaDir;
    private final MasterKey masterKey;
    private final MainDatabase mainDatabase;
    private final UserProfile userProfile;

    /**
     *
     * @param mediaDir directory where media data is stored ({@code [ANDROID MEDIA DIR]/ch.threema.app/files/data/})
     * @param masterKey the obtained {@link MasterKey}
     * @param mainDatabase the obtained {@link MainDatabase}
     * @param userProfile the obtained {@link UserProfile} information from the app preferences file
     */
    public MediaHandler(File mediaDir, MasterKey masterKey, MainDatabase mainDatabase, UserProfile userProfile) {
        this.mediaDir = mediaDir;
        this.masterKey = masterKey;
        this.mainDatabase = mainDatabase;
        this.userProfile = userProfile;
    }

    /**
     * saves avatar files and media to the output directory<p><p>
     *
     * creates the following structure:<p>
     * <ul>
     *     <li>{@code avatars/user.jpg} - user avatar
     *     <li>{@code avatars/[nickname].jpg} - contact avatars
     *     <li>{@code media/[nickname]/[unique file name].*} - direct chat media
     *     <li>{@code group_avatars/[group name].jpg} - group avatars
     *     <li>{@code group_media/[group name]/[unique file name].*} - group chat media
     * </ul><p>
     * <b>unique file name</b>: {@code [4 chars of message uid] - [original file name]}<p>
     * (since file names may occur multiple times)
     *
     */
    public void saveAllMedia(File outputDir) {

        try {

            // save user avatar if available
        	if (userProfile.getUserAvatar() != null) {
				userProfile.getUserAvatar().writeToFile(outputDir, AVATAR_DIR + "user");
			}
            

            // save contact avatars
            for (Contact contact : mainDatabase.getContacts().getAll()) {
            	if (contact.contactAvatar() != null) {
					contact.contactAvatar().writeToFile(outputDir, AVATAR_DIR + contact.nickname());
				}
            }

            // save media from direct messages
            for (IMessage message : mainDatabase.getDirectMessages().getAll()) {
                if (message instanceof MediaMessage mediaMessage) {
                    String contactName = mainDatabase.getContacts().getById(mediaMessage.identity()).nickname();

                    try {
                        mediaMessage.saveFile(masterKey, mediaDir, new File(outputDir, MEDIA_DIR + contactName));
                    } catch (FileNotFoundException e) {
                        System.out.println("[WARNING] Media file \"" + mediaMessage.plainFileName() +
                                "\" is referenced in chat with \"" + contactName + "\" but wasn't found in the media directory." +
                                " It might have been deleted.");
                    }

                }
            }

            for (Group group : mainDatabase.getGroups().getAll().values()) {
                // save group avatars
                group.groupAvatarFile().writeToFile(outputDir, GROUP_AVATAR_DIR + group.name());

                // save group media
                for (IMessage message : group.messages().getMessages()) {
                    if (message instanceof MediaMessage mediaMessage) {
                        try {
                            mediaMessage.saveFile(masterKey, mediaDir, new File(outputDir, GROUP_MEDIA_DIR + group.name()));
                        } catch (FileNotFoundException e) {
                            System.out.println("[WARNING] Media file \"" + mediaMessage.plainFileName() +
                                    "\" is referenced in group chat \"" + group.name() + "\" but wasn't found in the media directory." +
                                    " It might have been deleted.");
                        }

                    }
                }
            }

        } catch (IOException e) {
            System.err.println("ERROR WRITING MEDIA FILES.");
            e.printStackTrace();
        }

    }
}
