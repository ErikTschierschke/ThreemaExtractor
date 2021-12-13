package de.hsmw.threemaextractor.service.file;

import de.hsmw.threemaextractor.service.data.ContactStore;
import de.hsmw.threemaextractor.service.data.group.GroupStore;
import de.hsmw.threemaextractor.service.data.message.DirectMessageStore;
import de.hsmw.threemaextractor.service.db.DataFetcher;
import de.hsmw.threemaextractor.service.db.SqlCipherHandler;
import de.hsmw.threemaextractor.service.file.MasterKey;
import de.hsmw.threemaextractor.service.main.FileStore;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * representation main database (decrypted and parsed)
 */
public class MainDatabase {

    private final SqlCipherHandler sqlCipherHandler;
    private final DataFetcher dataFetcher;
    private final ContactStore contactStore = new ContactStore();
    private final DirectMessageStore messageStore = new DirectMessageStore();
    private final GroupStore groupStore = new GroupStore();
    private Connection connection;

    /**
     * initialize database connection, decrypt and parse
     * @param mainDBFile main database file (see {@link FileStore#databaseFile()})
     * @param masterKey master key
     * @param mediaDir media directory (see {@link FileStore#mediaDir()})
     */
    public MainDatabase(File mainDBFile, MasterKey masterKey, File mediaDir) {

        sqlCipherHandler = new SqlCipherHandler(mainDBFile, masterKey);

        try {
            connection = sqlCipherHandler.getConnection();
        } catch (SQLException e) {
            System.err.println("FAILED TO ESTABLISH DATABASE CONNECTION.");
            e.printStackTrace();
        }

        dataFetcher = new DataFetcher(connection, contactStore, messageStore, groupStore, masterKey, mediaDir);
        try {
            dataFetcher.fetchAll();
        } catch (SQLException e) {
            System.err.println("SQL error occured while parsing database. Read data may be incomplete or empty.");
            e.printStackTrace();
        }
    }

    /**
     * @return parsed Threema contacts
     * @see ContactStore
     */
    public ContactStore getContacts() {
        return contactStore;
    }

    /**
     * @return parsed direct messages
     * @see DirectMessageStore
     */
    public DirectMessageStore getDirectMessages() {
        return messageStore;
    }

    /**
     * @return parsed groups
     * @see GroupStore
     */
    public GroupStore getGroups() {
        return groupStore;
    }
}
