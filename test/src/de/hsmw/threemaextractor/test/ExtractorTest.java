package de.hsmw.threemaextractor.test;

import de.hsmw.threemaextractor.service.data.Avatar;
import de.hsmw.threemaextractor.service.data.ballot.Ballot;
import de.hsmw.threemaextractor.service.data.contact.Contact;
import de.hsmw.threemaextractor.service.data.distribution_list.DistributionList;
import de.hsmw.threemaextractor.service.data.group.Group;
import de.hsmw.threemaextractor.service.data.message.IMessage;
import de.hsmw.threemaextractor.service.data.message.MediaMessage;
import de.hsmw.threemaextractor.service.main.ChatVisualizer;
import de.hsmw.threemaextractor.service.main.FileStore;
import de.hsmw.threemaextractor.service.main.MediaHandler;
import de.hsmw.threemaextractor.service.main.ThreemaExtractor;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import java.io.FileNotFoundException;
import java.io.IOException;


public class ExtractorTest implements IApplication {

    @Override
    public Object start(IApplicationContext context) throws Exception {


        // load 4.5 data set
        FileStore fileStore1 = new FileStore("res/4.5/key.dat", "res/4.5/threema4.db", "res/4.5/ch.threema.app_preferences.xml", "res/4.5/data/", "out/4.5/");
        System.out.println(fileStore1.masterKeyNeedsPassphrase());
        extractAll(fileStore1);

        // load 4.6 data set
        FileStore fileStore2 = new FileStore("res/4.6/ch.threema.app/files/key.dat", "res/4.6/ch.threema.app/databases/threema4.db",
                "res/4.6/ch.threema.app/shared_prefs/ch.threema.app_preferences.xml", "res/4.6/data/", "out/4.6/");
        System.out.println(fileStore2.masterKeyNeedsPassphrase());
        extractAll(fileStore2);

        // load passphrase encrypted data set
        FileStore fileStore3 = new FileStore("res/crypt/key.dat", "res/crypt/threema4.db", "res/crypt/ch.threema.app_preferences.xml", "res/crypt/data/", "out/crypt/");
        System.out.println(fileStore3.masterKeyNeedsPassphrase());
        fileStore3.setPassphrase("test1234");
        extractAll(fileStore3);

        return IApplication.EXIT_OK;
    }

    private void extractAll(FileStore fileStore) {
        ThreemaExtractor threemaExtractor = new ThreemaExtractor(fileStore);
        try {

            // initialize ChatVisualizer
            ChatVisualizer chatVisualizer = new ChatVisualizer(threemaExtractor.getMainDatabase().getContacts(), true, true);


            /*
             * ==== USER PROFILE ====
             */

            // print data
            System.out.println(threemaExtractor.getUserProfile());
            // write user avatar
            Avatar userAvatar = threemaExtractor.getUserProfile().getUserAvatar();
            if (userAvatar != null) {
                userAvatar.writeToFile(fileStore.getOutputDir(), "user");
            }


            /*
             * ==== CONTACTS ====
             */
            for (Contact contact : threemaExtractor.getMainDatabase().getContacts().getAll()) {

                // print all contacts
                System.out.println(contact);
                // write contact avatar
                if (contact.contactAvatar() != null) {
                    contact.contactAvatar().writeToFile(fileStore.getOutputDir(), contact.nickname());
                }
            }

            /*
             * ==== DIRECT MESSAGES ====
             */
            for (IMessage message : threemaExtractor.getMainDatabase().getDirectMessages().getAll()) {

                // print all direct messages
                System.out.println(message);

                // write media from media messages
                if (message instanceof MediaMessage mediaMessage) {
                    try {
                        mediaMessage.saveFile(fileStore.getMasterKey(), fileStore.getMediaDir(), fileStore.getOutputDir());
                    } catch (FileNotFoundException e) {
                        System.out.println(mediaMessage.getEncryptedFileName() + " was not found!");
                    }

                }
            }
            // visualize all direct conversations
            for (Contact contact : threemaExtractor.getMainDatabase().getContacts().getAll()) {
                System.out.println(chatVisualizer.visualizeConversation(threemaExtractor.getMainDatabase().getDirectMessages().getByIdentity(contact.identity())));
            }

            /*
             * ==== GROUPS ====
             */
            for (Group group : threemaExtractor.getMainDatabase().getGroups().getAll().values()) {

                // print all groups
                System.out.println(group);
                // save all group avatars
                if (group.groupAvatar() != null) {
                    group.groupAvatar().writeToFile(fileStore.getOutputDir(), group.name());
                }

                for (IMessage message : group.messages()) {
                    // print all group messages
                    System.out.println(message);
                    // write media from media messages
                    if (message instanceof MediaMessage mediaMessage) {
                        try {
                            mediaMessage.saveFile(fileStore.getMasterKey(), fileStore.getMediaDir(), fileStore.getOutputDir());
                        } catch (FileNotFoundException e) {
                            System.out.println(mediaMessage.getEncryptedFileName() + " was not found!");
                        }

                    }
                }
                System.out.println(chatVisualizer.visualizeConversation(group.messages()));
            }

            /*
             * ==== DISTRIBUTION LISTS ====
             */
            for (DistributionList distributionList : threemaExtractor.getMainDatabase().getDistributionLists().getAll().values()) {

                // print all lists
                System.out.println(distributionList);
                // print all list messages
                System.out.println(distributionList.messages());
                System.out.println(chatVisualizer.visualizeConversation(distributionList.messages()));
            }

            /*
             *	==== Ballots ====
             */
            for (Ballot ballot : threemaExtractor.getMainDatabase().getBallots().getAll()) {
                // print all ballots
                System.out.println(ballot);
            }

            /*
             * ==== MEDIA ====
             */

            // extract all media
            new MediaHandler(fileStore.getMediaDir(), fileStore.getMasterKey(), threemaExtractor.getMainDatabase(), threemaExtractor.getUserProfile())
                    .saveAllMedia(fileStore.getOutputDir());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        // nothing to do
    }
}
