package de.hsmw.threemaextractor.test;

import de.hsmw.threemaextractor.service.data.ballot.Ballot;
import de.hsmw.threemaextractor.service.data.distribution_list.DistributionList;
import de.hsmw.threemaextractor.service.data.group.Group;
import de.hsmw.threemaextractor.service.data.message.IMessage;
import de.hsmw.threemaextractor.service.file.MainDatabase;
import de.hsmw.threemaextractor.service.file.MasterKey;
import de.hsmw.threemaextractor.service.main.ChatVisualizer;
import de.hsmw.threemaextractor.service.main.FileStore;
import de.hsmw.threemaextractor.service.main.ThreemaExtractor;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import java.io.File;


public class ExtractorTest implements IApplication {

    @Override
    public Object start(IApplicationContext context) throws Exception {

        FileStore fileStore = new FileStore(
                "res/crypt/key.dat", "res/crypt/threema4.db", "res/delete/ch.threema.app_preferences.xml", "res/delete/data/", "out/delete");

        fileStore.setPassphrase("test1234");

        ThreemaExtractor threemaExtractor = new ThreemaExtractor(fileStore);

        MainDatabase mainDatabase = threemaExtractor.getMainDatabase();

        for (IMessage message : mainDatabase.getDirectMessages().getAll()) {
            System.out.println(message);
        }

        ChatVisualizer chatVisualizer = new ChatVisualizer(mainDatabase.getContacts(), true, true); //"XNYZKFYJ"
        //System.out.println(chatVisualizer.visualizeDirectConversation(mainDatabase.getDirectMessages(), "ECHOECHO"));
        //System.out.println(chatVisualizer.visualizeConversation(mainDatabase.getGroups().getByName("A").messages()));

        for (Group group : threemaExtractor.getMainDatabase().getGroups().getAll().values()) {
            System.out.println(group.members());

        }
        System.out.println(mainDatabase.getDistributionLists().getAll().values());
        for (DistributionList list : mainDatabase.getDistributionLists().getAll().values()) {
            System.out.println(list.name() + "test");
            for (IMessage message : list.messages()) {
                System.out.println(message);
            }
        }

        for (Ballot ballot : mainDatabase.getBallots().getAll()) {
            System.out.println(ballot);
        }

        System.out.println(new MasterKey(new File("res/delete/key.dat")).getDatabaseKey());
        return IApplication.EXIT_OK;
    }

    @Override
    public void stop() {
        // nothing to do
    }
}
