package de.hsmw.threemaextractor.ui;

import de.hsmw.threemaextractor.service.file.MainDatabase;
import de.hsmw.threemaextractor.service.main.ChatVisualizer;
import de.hsmw.threemaextractor.service.main.FileStore;
import de.hsmw.threemaextractor.service.main.ThreemaExtractor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class FileSelectorPart {
    private Text masterKeyPath;
    private Text mediaDirPath;
    private Text outDirPath;
    private Text databasePath;
    private Text preferencesPath;


    private boolean firstSelectionDone = false;

    private Button extractButton;

    @Inject
    public FileSelectorPart() {

    }

    @PostConstruct
    public void postConstruct(Composite parent) {

        // ---- automatically created layout by WindowBuilder----

        parent.setLayout(new FormLayout());

        Label masterKeyLabel = new Label(parent, SWT.NONE);
        FormData fd_masterKeyLabel = new FormData();
        fd_masterKeyLabel.left = new FormAttachment(0, 10);
        masterKeyLabel.setLayoutData(fd_masterKeyLabel);
        masterKeyLabel.setText("key.dat");

        masterKeyPath = new Text(parent, SWT.BORDER);
        fd_masterKeyLabel.bottom = new FormAttachment(masterKeyPath, -6);
        FormData fd_masterKeyPath = new FormData();
        fd_masterKeyPath.top = new FormAttachment(0, 31);
        fd_masterKeyPath.left = new FormAttachment(0, 10);
        masterKeyPath.setLayoutData(fd_masterKeyPath);

        Button masterKeySelect = new Button(parent, SWT.NONE);
        fd_masterKeyPath.right = new FormAttachment(masterKeySelect, -18);
        FormData fd_masterKeySelect = new FormData();
        fd_masterKeySelect.top = new FormAttachment(0, 31);
        fd_masterKeySelect.right = new FormAttachment(100, -10);
        masterKeySelect.setLayoutData(fd_masterKeySelect);
        masterKeySelect.setText("Ausw??hlen");

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
        databaseSelect.setText("Ausw??hlen");

        databasePath = new Text(parent, SWT.BORDER);
        fd_databaseLabel.bottom = new FormAttachment(databasePath, -6);
        FormData fd_databasePath = new FormData();
        fd_databasePath.right = new FormAttachment(databaseSelect, -18);
        fd_databasePath.left = new FormAttachment(0, 10);
        fd_databasePath.bottom = new FormAttachment(databaseSelect, 0, SWT.BOTTOM);
        databasePath.setLayoutData(fd_databasePath);

        Label mediaDirLabel = new Label(parent, SWT.NONE);
        FormData fd_mediaDirLabel = new FormData();
        fd_mediaDirLabel.left = new FormAttachment(0, 10);
        mediaDirLabel.setLayoutData(fd_mediaDirLabel);
        mediaDirLabel.setText("Media-Verzeichnis");

        mediaDirPath = new Text(parent, SWT.BORDER);
        FormData fd_mediaDirPath = new FormData();
        fd_mediaDirPath.left = new FormAttachment(0, 10);
        fd_mediaDirPath.top = new FormAttachment(0, 250);
        mediaDirPath.setLayoutData(fd_mediaDirPath);

        Button mediaDirSelect = new Button(parent, SWT.NONE);
        fd_mediaDirPath.right = new FormAttachment(mediaDirSelect, -18);
        mediaDirSelect.setToolTipText("");
        FormData fd_mediaDirSelect = new FormData();
        fd_mediaDirSelect.bottom = new FormAttachment(mediaDirPath, 0, SWT.BOTTOM);
        fd_mediaDirSelect.left = new FormAttachment(masterKeySelect, 0, SWT.LEFT);
        mediaDirSelect.setLayoutData(fd_mediaDirSelect);
        mediaDirSelect.setText("Ausw??hlen");

        Label label_2 = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        FormData fd_label_2 = new FormData();
        fd_label_2.left = new FormAttachment(0, 10);
        fd_label_2.right = new FormAttachment(100, -10);
        fd_label_2.bottom = new FormAttachment(mediaDirPath, 46, SWT.BOTTOM);
        fd_label_2.top = new FormAttachment(mediaDirPath, 6);
        label_2.setLayoutData(fd_label_2);

        Label outDirLabel = new Label(parent, SWT.NONE);
        FormData fd_outDirLabel = new FormData();
        fd_outDirLabel.left = new FormAttachment(0, 10);
        fd_outDirLabel.top = new FormAttachment(label_2, 6);
        outDirLabel.setLayoutData(fd_outDirLabel);
        outDirLabel.setText("Ausgabe-Verzeichnis");

        outDirPath = new Text(parent, SWT.BORDER);
        FormData fd_outDirPath = new FormData();
        fd_outDirPath.left = new FormAttachment(0, 10);
        fd_outDirPath.top = new FormAttachment(outDirLabel, 6);
        outDirPath.setLayoutData(fd_outDirPath);

        Button outDirSelect = new Button(parent, SWT.NONE);
        fd_outDirPath.right = new FormAttachment(outDirSelect, -18);
        FormData fd_outDirSelect = new FormData();
        fd_outDirSelect.top = new FormAttachment(outDirPath, 0, SWT.TOP);
        fd_outDirSelect.left = new FormAttachment(masterKeySelect, 0, SWT.LEFT);
        outDirSelect.setLayoutData(fd_outDirSelect);
        outDirSelect.setText("Ausw??hlen");

        extractButton = new Button(parent, SWT.NONE);
        extractButton.setEnabled(false);
        FormData fd_extractButton = new FormData();
        fd_extractButton.bottom = new FormAttachment(100, -31);
        fd_extractButton.right = new FormAttachment(100, -10);
        extractButton.setLayoutData(fd_extractButton);
        extractButton.setText("Extraktion starten");

        Label preferencesLabel = new Label(parent, SWT.NONE);
        FormData fd_preferencesLabel = new FormData();
        fd_preferencesLabel.left = new FormAttachment(0, 10);
        fd_preferencesLabel.top = new FormAttachment(databasePath, 27);
        preferencesLabel.setLayoutData(fd_preferencesLabel);
        preferencesLabel.setText("ch.threema.app_preferences.xml");

        preferencesPath = new Text(parent, SWT.BORDER);
        fd_mediaDirLabel.bottom = new FormAttachment(preferencesPath, 42, SWT.BOTTOM);
        fd_mediaDirLabel.top = new FormAttachment(preferencesPath, 27);
        FormData fd_preferencesPath = new FormData();
        fd_preferencesPath.left = new FormAttachment(0, 10);
        fd_preferencesPath.top = new FormAttachment(preferencesLabel, 6);
        preferencesPath.setLayoutData(fd_preferencesPath);

        Button preferencesSelect = new Button(parent, SWT.NONE);
        fd_preferencesPath.right = new FormAttachment(preferencesSelect, -18);
        FormData fd_preferencesSelect = new FormData();
        fd_preferencesSelect.top = new FormAttachment(preferencesPath, 0, SWT.TOP);
        fd_preferencesSelect.right = new FormAttachment(masterKeySelect, 0, SWT.RIGHT);
        preferencesSelect.setLayoutData(fd_preferencesSelect);
        preferencesSelect.setText("Ausw??hlen");


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
                        checkFileMissing(outDirPath.getText(), true)) {
                    return;
                }

                FileStore fileStore;
                try {
                    fileStore = new FileStore(masterKeyPath.getText(), databasePath.getText(), preferencesPath.getText(),
                            mediaDirPath.getText(), outDirPath.getText());

                    // check if passphrase is required
                    if (fileStore.masterKeyNeedsPassphrase()) {
                        PassphraseDialog passphraseDialog = new PassphraseDialog(new Shell(), fileStore, false);
                        int result = passphraseDialog.open();

                        if (result == Window.OK) {
                            startExtraction(fileStore);
                        }
                    } else {
                        startExtraction(fileStore);
                    }
                } catch (IOException exception) {
                    // won't happen, files are already checked
                    exception.printStackTrace();
                }


                parent.getShell().close();
            }
        });


        // set text modify listeners to toggle extract button activation
        ModifyListener changeListener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent arg0) {
                checkAllSelected();
            }
        };

        masterKeyPath.addModifyListener(changeListener);
        databasePath.addModifyListener(changeListener);
        preferencesPath.addModifyListener(changeListener);
        mediaDirPath.addModifyListener(changeListener);
        outDirPath.addModifyListener(changeListener);

    }

    private void startExtraction(FileStore fileStore) {
        ThreemaExtractor extractor = new ThreemaExtractor(fileStore);
        MainDatabase database = extractor.getMainDatabase();
        ChatVisualizer chatVisualizer = new ChatVisualizer(extractor.getUserProfile().getNickname(), database.getContacts(), false, false);

        System.out.println(chatVisualizer.visualizeConversation(database.getDirectMessages().getByIdentity("XNYZKFYJ")));
    }

    private boolean checkFileMissing(String path, boolean isDir) {

        String object = isDir ? "Verzeichnis" : "Datei";

        if (!FileStore.checkFilePresent(path)) {
            MessageDialog.openWarning(new Shell(),
                    "Fehler",
                    object + " " + path + " wurde nicht gefunden.");
            return true;
        }
        return false;
    }


    /**
     * checks if all paths are selected - if so activates the start button
     */
    private void checkAllSelected() {
        if (!masterKeyPath.getText().isEmpty() &&
                !databasePath.getText().isEmpty() &&
                !preferencesPath.getText().isEmpty() &&
                !mediaDirPath.getText().isEmpty() &&
                !outDirPath.getText().isEmpty()) {
            extractButton.setEnabled(true);
        } else {
            extractButton.setEnabled(false);
        }
    }

    /**
     * if the first file from the app directory is selected, try to find the others automatically
     */
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

        if (masterKeyPath != setTextBox && FileStore.checkFilePresent(defaultMasterKeyPath.getAbsolutePath())) {
            masterKeyPath.setText(defaultMasterKeyPath.getAbsolutePath());
        }
        if (databasePath != setTextBox && FileStore.checkFilePresent(defaultDatabasePath.getAbsolutePath())) {
            databasePath.setText(defaultDatabasePath.getAbsolutePath());
        }
        if (preferencesPath != setTextBox && FileStore.checkFilePresent(defaultPreferencesPath.getAbsolutePath())) {
            preferencesPath.setText(defaultPreferencesPath.getAbsolutePath());
        }
    }
}