package de.hsmw.threemaextractor.test;

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
                "res/key.dat", "res/threema4.db", "res/ch.threema.app_preferences.xml", "res/data/", "out/");

        ThreemaExtractor threemaExtractor = new ThreemaExtractor(fileStore);

        MainDatabase mainDatabase = threemaExtractor.getMainDatabase();

        ChatVisualizer chatVisualizer = new ChatVisualizer(mainDatabase.getContacts(), true, true);
//"XNYZKFYJ"
        //System.out.println(chatVisualizer.visualizeDirectConversation(mainDatabase.getDirectMessages(), "ECHOECHO"));
        System.out.println(chatVisualizer.visualizeGroupConversation(mainDatabase.getGroups().getByName("A").messages()));

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

        System.out.println(new MasterKey(new File("res/key.dat")).getDatabaseKey());
        return IApplication.EXIT_OK;
    }

    @Override
    public void stop() {
        // nothing to do
    }
}
