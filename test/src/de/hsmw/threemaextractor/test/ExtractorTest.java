package de.hsmw.threemaextractor.test;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import de.hsmw.threemaextractor.service.main.ChatVisualizer;
import de.hsmw.threemaextractor.service.main.FileStore;
import de.hsmw.threemaextractor.service.file.MainDatabase;
import de.hsmw.threemaextractor.service.main.ThreemaExtractor;


public class ExtractorTest implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {
		

		FileStore fileStore = new FileStore(
				"res/key.dat", "res/threema4.db", "res/ch.threema.app_preferences.xml", "res/data/", "out/");
		
		ThreemaExtractor threemaExtractor = new ThreemaExtractor(fileStore);
		
		MainDatabase mainDatabase = threemaExtractor.getMainDatabase();
		
		ChatVisualizer chatVisualizer = new  ChatVisualizer(mainDatabase.getContacts(), true, true);
		
		System.out.println(chatVisualizer.visualizeDirectConversation(mainDatabase.getDirectMessages(), "XNYZKFYJ"));
		
		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
		// nothing to do
	}
}
