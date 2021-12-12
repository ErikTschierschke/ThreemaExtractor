package de.hsmw.threemaextractor.service.db;

import de.hsmw.threemaextractor.service.main.MasterKey;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlCipherHandler {

    private Connection connection;
    private final File mainDBFile;
    private final MasterKey masterKey;

    public SqlCipherHandler(File mainDBFile, MasterKey masterKey) {
        this.mainDBFile = mainDBFile;
        this.masterKey = masterKey;
    }

    /**
     * Return connection to the encrypted database file
     * <p>
     * If there is none, establish a new one
     */
    public Connection getConnection() throws SQLException {

        if (connection == null) {

            // connect SQLite driver to database file
            connection = DriverManager.getConnection("jdbc:sqlite:file:" + mainDBFile.getPath());

            // initialize encryption (SQLCipher v4 with KDF_ITER=1 and key)
            connection.createStatement().executeUpdate("SELECT sqlite3mc_config('default:cipher', 'sqlcipher');" +
                    "SELECT sqlite3mc_config('sqlcipher', 'legacy', 4);" +
                    "SELECT sqlite3mc_config('sqlcipher', 'default:kdf_iter', 1);" +
                    "PRAGMA key='x\"" + masterKey.getDatabaseKey() + "\"';" +
                    "");
        }
        return connection;
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }
}
