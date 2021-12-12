package de.hsmw.threemaextractor.service.main;

import de.hsmw.threemaextractor.service.data.ContactStore;
import de.hsmw.threemaextractor.service.data.group.GroupStore;
import de.hsmw.threemaextractor.service.data.message.DirectMessageStore;
import de.hsmw.threemaextractor.service.db.DataFetcher;
import de.hsmw.threemaextractor.service.db.SqlCipherHandler;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class MainDatabase {

    private final SqlCipherHandler sqlCipherHandler;
    private final Connection connection;
    private final DataFetcher dataFetcher;

    private final ContactStore contactStore = new ContactStore();
    private final DirectMessageStore messageStore = new DirectMessageStore();
    private final GroupStore groupStore = new GroupStore();

    public MainDatabase(File mainDBFile, MasterKey masterKey) throws SQLException {

        sqlCipherHandler = new SqlCipherHandler(mainDBFile, masterKey);
        connection = sqlCipherHandler.getConnection();
        dataFetcher = new DataFetcher(connection, contactStore, messageStore, groupStore);

        dataFetcher.fetchAll();
    }

    public ContactStore getContacts() {
        return contactStore;
    }

    public DirectMessageStore getDirectMessages() {
        return messageStore;
    }

    public GroupStore getGroups() {
        return groupStore;
    }
}
