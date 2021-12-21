package de.hsmw.threemaextractor.test;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import de.hsmw.threemaextractor.service.main.ChatVisualizer;
import de.hsmw.threemaextractor.service.main.FileStore;
import de.hsmw.threemaextractor.service.data.distribution_list.DistributionList;
import de.hsmw.threemaextractor.service.data.group.Group;
import de.hsmw.threemaextractor.service.data.message.IMessage;
import de.hsmw.threemaextractor.service.file.MainDatabase;
import de.hsmw.threemaextractor.service.main.ThreemaExtractor;


public class ExtractorTest implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {
		

		FileStore fileStore = new FileStore(
				"res/self/files/key.dat", "res/self/databases/threema4.db", "res/self/shared_prefs/ch.threema.app_preferences.xml", "res/data/", "out/");
		
		ThreemaExtractor threemaExtractor = new ThreemaExtractor(fileStore);
		
		MainDatabase mainDatabase = threemaExtractor.getMainDatabase();
		
		ChatVisualizer chatVisualizer = new  ChatVisualizer(mainDatabase.getContacts(), true, true);
		
		System.out.println(chatVisualizer.visualizeDirectConversation(mainDatabase.getDirectMessages(), "XNYZKFYJ"));
		
		for (Group group : threemaExtractor.getMainDatabase().getGroups().getAll().values()) {
			System.out.println(group.members());
			
		}
		System.out.println(mainDatabase.getDistributionListStore().getAll().values());
		 for (DistributionList list : mainDatabase.getDistributionListStore().getAll().values()) {
			 System.out.println(list.name() + "test");
			 for (IMessage message : list.messages()) {
				 System.out.println(message);
			 }
		 }
		
		System.out.println(threemaExtractor.getMainDatabase().getGroups());
		
		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
		// nothing to do
	}
}
