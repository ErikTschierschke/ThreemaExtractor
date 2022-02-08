package de.hsmw.threemaextractor.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.hsmw.threemaextractor.service.main.FileStore;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;

public class PassphraseDialog extends TitleAreaDialog{
	
	private FileStore fileStore;
	private Text passphraseText;
	private boolean passphraseIncorrect;
	
public PassphraseDialog(Shell parentShell, FileStore fileStore, boolean passphraseIncorrect) {
		
		super(parentShell);
		this.fileStore = fileStore;
		this.passphraseIncorrect = passphraseIncorrect;
	}
	
	@Override
	public void create() {
		super.create();
		
		setTitle("Passphrase benötigt!");
		setMessage("Die eingegebenen Daten wurden mit einer Passphrase verschlüsselt. Ohne Kenntnis der Passphrase ist eine weitere Auswertung nicht möglich.");
	}
	
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(1, false);
        container.setLayout(layout);
        
        Label passphraseLabel = new Label(container, SWT.NONE);
        passphraseLabel.setText("Passphrase");
        
        passphraseText = new Text(container, SWT.BORDER | SWT.PASSWORD);
        passphraseText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
       
        

        return area;
    }
    
    @Override
    public void okPressed() {
    	
    	if (fileStore.setPassphrase(passphraseText.getText())) {
			super.okPressed();
			return;
		} else {
            MessageDialog.openWarning(getParentShell().getShell(),
                    "Fehler",
                    "Die eingegebene Passphrase ist nicht korrekt.");
		}
    }
    
    
	
	

	
}
