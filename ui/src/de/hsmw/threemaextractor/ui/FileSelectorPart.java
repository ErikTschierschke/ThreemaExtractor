package de.hsmw.threemaextractor.ui;

import de.hsmw.threemaextractor.service.file.MainDatabase;
import de.hsmw.threemaextractor.service.main.ChatVisualizer;
import de.hsmw.threemaextractor.service.main.FileStore;
import de.hsmw.threemaextractor.service.main.FileStore.CheckResult;
import de.hsmw.threemaextractor.service.main.ThreemaExtractor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;

public class FileSelectorPart {
    private Text masterKeyPath;
    private Text mediaDirPath;
    private Text outDirPath;
    private Text databasePath;
    private Text preferencesPath;


    private boolean firstSelectionDone = false;

    @Inject
    public FileSelectorPart() {

    }

    @PostConstruct
    public void postConstruct(Composite parent) {

        // ---- automatically created layout by WindowBuilder----

        parent.setLayout(new FormLayout());

        Label masterKeyLabel = new Label(parent, SWT.NONE);
        FormData fd_masterKeyLabel = new FormData();
        masterKeyLabel.setLayoutData(fd_masterKeyLabel);
        masterKeyLabel.setText("key.dat");

        masterKeyPath = new Text(parent, SWT.BORDER);
        fd_masterKeyLabel.bottom = new FormAttachment(masterKeyPath, -6);
        fd_masterKeyLabel.left = new FormAttachment(masterKeyPath, 0, SWT.LEFT);
        FormData fd_masterKeyPath = new FormData();
        fd_masterKeyPath.top = new FormAttachment(0, 31);
        fd_masterKeyPath.right = new FormAttachment(0, 410);
        fd_masterKeyPath.left = new FormAttachment(0, 10);
        masterKeyPath.setLayoutData(fd_masterKeyPath);

        Button masterKeySelect = new Button(parent, SWT.NONE);
        FormData fd_masterKeySelect = new FormData();
        fd_masterKeySelect.top = new FormAttachment(masterKeyPath, 0, SWT.TOP);
        fd_masterKeySelect.right = new FormAttachment(100, -10);
        masterKeySelect.setLayoutData(fd_masterKeySelect);
        masterKeySelect.setText("Auswählen");

        Label databaseLabel = new Label(parent, SWT.NONE);
        FormData fd_databaseLabel = new FormData();
        fd_databaseLabel.left = new FormAttachment(0, 10);
        databaseLabel.setLayoutData(fd_databaseLabel);
        databaseLabel.setText("threema4.db");

        Button databaseSelect = new Button(parent, SWT.NONE);
        FormData fd_databaseSelect = new FormData();
        fd_databaseSelect.top = new FormAttachment(masterKeySelect, 48);
        fd_databaseSelect.right = new FormAttachment(masterKeySelect, 0, SWT.RIGHT);
        databaseSelect.setLayoutData(fd_databaseSelect);
        databaseSelect.setText("Auswählen");

        databasePath = new Text(parent, SWT.BORDER);
        fd_databaseLabel.bottom = new FormAttachment(databasePath, -6);
        FormData fd_databasePath = new FormData();
        fd_databasePath.bottom = new FormAttachment(databaseSelect, 0, SWT.BOTTOM);
        fd_databasePath.right = new FormAttachment(masterKeyPath, 0, SWT.RIGHT);
        fd_databasePath.left = new FormAttachment(0, 10);
        databasePath.setLayoutData(fd_databasePath);

        Label mediaDirLabel = new Label(parent, SWT.NONE);
        FormData fd_mediaDirLabel = new FormData();
        fd_mediaDirLabel.left = new FormAttachment(0, 10);
        mediaDirLabel.setLayoutData(fd_mediaDirLabel);
        mediaDirLabel.setText("Media-Verzeichnis");

        mediaDirPath = new Text(parent, SWT.BORDER);
        FormData fd_mediaDirPath = new FormData();
        fd_mediaDirPath.right = new FormAttachment(masterKeyPath, 0, SWT.RIGHT);
        fd_mediaDirPath.top = new FormAttachment(0, 250);
        fd_mediaDirPath.left = new FormAttachment(0, 10);
        mediaDirPath.setLayoutData(fd_mediaDirPath);

        Button mediaDirSelect = new Button(parent, SWT.NONE);
        mediaDirSelect.setToolTipText("");
        FormData fd_mediaDirSelect = new FormData();
        fd_mediaDirSelect.bottom = new FormAttachment(mediaDirPath, 0, SWT.BOTTOM);
        fd_mediaDirSelect.left = new FormAttachment(masterKeySelect, 0, SWT.LEFT);
        mediaDirSelect.setLayoutData(fd_mediaDirSelect);
        mediaDirSelect.setText("Auswählen");

        Label label_2 = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        FormData fd_label_2 = new FormData();
        fd_label_2.bottom = new FormAttachment(mediaDirPath, 46, SWT.BOTTOM);
        fd_label_2.top = new FormAttachment(mediaDirPath, 6);
        fd_label_2.right = new FormAttachment(masterKeySelect, 0, SWT.RIGHT);
        fd_label_2.left = new FormAttachment(masterKeyLabel, 0, SWT.LEFT);
        label_2.setLayoutData(fd_label_2);

        Label outDirLabel = new Label(parent, SWT.NONE);
        FormData fd_outDirLabel = new FormData();
        fd_outDirLabel.top = new FormAttachment(label_2, 6);
        fd_outDirLabel.left = new FormAttachment(masterKeyLabel, 0, SWT.LEFT);
        outDirLabel.setLayoutData(fd_outDirLabel);
        outDirLabel.setText("Ausgabe-Verzeichnis");

        outDirPath = new Text(parent, SWT.BORDER);
        FormData fd_outDirPath = new FormData();
        fd_outDirPath.top = new FormAttachment(outDirLabel, 6);
        fd_outDirPath.left = new FormAttachment(masterKeyLabel, 0, SWT.LEFT);
        fd_outDirPath.right = new FormAttachment(masterKeyPath, 0, SWT.RIGHT);
        outDirPath.setLayoutData(fd_outDirPath);

        Button outDirSelect = new Button(parent, SWT.NONE);
        FormData fd_outDirSelect = new FormData();
        fd_outDirSelect.top = new FormAttachment(outDirPath, 0, SWT.TOP);
        fd_outDirSelect.left = new FormAttachment(masterKeySelect, 0, SWT.LEFT);
        outDirSelect.setLayoutData(fd_outDirSelect);
        outDirSelect.setText("Auswählen");

        Button extractButton = new Button(parent, SWT.NONE);
        FormData fd_extractButton = new FormData();
        fd_extractButton.bottom = new FormAttachment(100, -31);
        fd_extractButton.right = new FormAttachment(100, -10);
        extractButton.setLayoutData(fd_extractButton);
        extractButton.setText("Extraktion starten");

        Label preferencesLabel = new Label(parent, SWT.NONE);
        FormData fd_preferencesLabel = new FormData();
        fd_preferencesLabel.top = new FormAttachment(databasePath, 27);
        fd_preferencesLabel.left = new FormAttachment(masterKeyLabel, 0, SWT.LEFT);
        preferencesLabel.setLayoutData(fd_preferencesLabel);
        preferencesLabel.setText("ch.threema.app_preferences.xml");

        preferencesPath = new Text(parent, SWT.BORDER);
        fd_mediaDirLabel.bottom = new FormAttachment(preferencesPath, 42, SWT.BOTTOM);
        fd_mediaDirLabel.top = new FormAttachment(preferencesPath, 27);
        FormData fd_preferencesPath = new FormData();
        fd_preferencesPath.right = new FormAttachment(masterKeyPath, 0, SWT.RIGHT);
        fd_preferencesPath.top = new FormAttachment(preferencesLabel, 6);
        fd_preferencesPath.left = new FormAttachment(masterKeyLabel, 0, SWT.LEFT);
        preferencesPath.setLayoutData(fd_preferencesPath);

        Button preferencesSelect = new Button(parent, SWT.NONE);
        FormData fd_preferencesSelect = new FormData();
        fd_preferencesSelect.top = new FormAttachment(preferencesPath, 0, SWT.TOP);
        fd_preferencesSelect.right = new FormAttachment(masterKeySelect, 0, SWT.RIGHT);
        preferencesSelect.setLayoutData(fd_preferencesSelect);
        preferencesSelect.setText("Auswählen");


        // ---- manually set event triggers ----

        //select master key file
        masterKeySelect.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {

                FileDialog fileDialog = new FileDialog(new Shell());
                String result = fileDialog.open();

                if (result != null) {
                    masterKeyPath.setText(result);
                }

                handleFirstSelection(result, masterKeyPath);
            }
        });

        //select database file
        databaseSelect.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {

                FileDialog fileDialog = new FileDialog(new Shell());
                String result = fileDialog.open();

                if (result != null) {
                    databasePath.setText(result);
                }

                handleFirstSelection(result, databasePath);
            }
        });

        //select preferences file
        preferencesSelect.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {

                FileDialog fileDialog = new FileDialog(new Shell());
                String result = fileDialog.open();

                if (result != null) {
                    preferencesPath.setText(result);
                }

                handleFirstSelection(result, preferencesPath);
            }
        });

        //select media dir
        mediaDirSelect.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {

                DirectoryDialog dirDialog = new DirectoryDialog(new Shell());
                String result = dirDialog.open();

                if (result != null) {
                    mediaDirPath.setText(result);
                }
            }
        });

        //select output dir
        outDirSelect.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {

                DirectoryDialog dirDialog = new DirectoryDialog(new Shell());
                String result = dirDialog.open();

                if (result != null) {
                    outDirPath.setText(result);
                }
            }
        });

        //extraction button click - check files and start extraction
        extractButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {

                // check all provided files and exit if a problem occurs
                if (checkFileMissing(masterKeyPath.getText(), false) ||
                        checkFileMissing(databasePath.getText(), false) ||
                        checkFileMissing(preferencesPath.getText(), false) ||
                        checkFileMissing(mediaDirPath.getText(), true) ||
                        checkFileMissing(outDirPath.getText(), true) ||
                        checkMasterKeyEncrypted(masterKeyPath.getText())) {
                    return;
                }

                FileStore fileStore = new FileStore(masterKeyPath.getText(), databasePath.getText(), preferencesPath.getText(),
                        mediaDirPath.getText(), outDirPath.getText());

                ThreemaExtractor extractor = new ThreemaExtractor(fileStore);
                //TODO somehow export this object

                // display a direct chat for testing
                MainDatabase database = extractor.getMainDatabase();
                ChatVisualizer chatVisualizer = new ChatVisualizer(database.getContacts(), false, false);

                System.out.println(chatVisualizer.visualizeDirectConversation(database.getDirectMessages(), "XNYZKFYJ"));

                parent.getShell().close();
            }
        });

    }

    private boolean checkFileMissing(String path, boolean isDir) {

        String object = isDir ? "Verzeichnis" : "Datei";

        if (FileStore.checkFile(path) == CheckResult.MISSING) {
            MessageDialog.openWarning(new Shell(),
                    "Fehler",
                    object + " " + path + " wurde nicht gefunden.");
            return true;
        }
        return false;
    }

    private boolean checkMasterKeyEncrypted(String path) {

        if (FileStore.checkMasterkeyFile(path) == CheckResult.MASTERKEY_ENCRYPTED) {
            MessageDialog.openWarning(new Shell(),
                    "Fehler",
                    "Masterkey ist mit einer Passphrase verschlüsselt.");
            return true;
        }
        return false;
    }

    // if the first file is selected, automatically set others
    private void handleFirstSelection(String path, Text setTextBox) {

        // only run on first file selected
        if (firstSelectionDone) {
            return;
        }
        firstSelectionDone = true;

        File appDataDir = new File(path).getParentFile().getParentFile();

        File defaultMasterKeyPath = new File(appDataDir + "/files/key.dat");
        File defaultDatabasePath = new File(appDataDir + "/databases/threema4.db");
        File defaultPreferencesPath = new File(appDataDir + "/shared_prefs/ch.threema.app_preferences.xml");

        if (masterKeyPath != setTextBox && FileStore.checkFile(defaultMasterKeyPath.getAbsolutePath()) == CheckResult.OK) {
            masterKeyPath.setText(defaultMasterKeyPath.getAbsolutePath());
        }
        if (databasePath != setTextBox && FileStore.checkFile(defaultDatabasePath.getAbsolutePath()) == CheckResult.OK) {
            databasePath.setText(defaultDatabasePath.getAbsolutePath());
        }
        if (preferencesPath != setTextBox && FileStore.checkFile(defaultPreferencesPath.getAbsolutePath()) == CheckResult.OK) {
            preferencesPath.setText(defaultPreferencesPath.getAbsolutePath());
        }
    }
}