package de.hsmw.threemaextractor.service.file;

import de.hsmw.threemaextractor.service.data.Avatar;
import de.hsmw.threemaextractor.service.main.FileStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;

/**
 * represents information parsed from app preferences file
 */
public class UserProfile {

    private String phoneNumber;
    private String identity;
    private String nickname;
    private String eMail;
    private Avatar userAvatar;
    private String[] blockedList;

    /**
     * parse information from app preferences file
     *
     * @param appPreferencesFile app preferences file (see {@link FileStore#getPreferencesFile()})
     * @param masterKey          master key
     * @param mediaDir           media directory (see {@link FileStore#getMediaDir()})
     */
    public UserProfile(File appPreferencesFile, MasterKey masterKey, File mediaDir) {


        String appPreferences = null;
        Document document = null;
        try {
            // threema safe hash contains illegal XML chars, remove it
            appPreferences = Files.readString(appPreferencesFile.toPath())
                    .replaceAll("(.*(pref_threema_safe_hash_string).*)", "");

            // load XML file
            document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new StringReader(appPreferences)));
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        document.normalize();
        NodeList items = document.getFirstChild().getChildNodes();

        // set user profile information
        for (int i = 0; i < items.getLength(); i++) {
            if (items.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element item = (Element) items.item(i);

                if (item.getAttribute("name").equals("linked_mobile"))
                    phoneNumber = item.getTextContent();
                if (item.getAttribute("name").equals("identity"))
                    identity = item.getTextContent();
                if (item.getAttribute("name").equals("nickname"))
                    nickname = item.getTextContent();
                if (item.getAttribute("name").equals("linked_email"))
                    eMail = item.getTextContent();
                if (item.getAttribute("name").equals("identity_list_blacklist")) {
                    blockedList = item.getTextContent().split(";");
                }
            }
        }

        // try to get user avatar
        try {
            userAvatar = new Avatar(Avatar.getContactAvatarFile(mediaDir, identity, true), masterKey);
        } catch (IOException e) {
            System.out.println("[WARNING] User avatar not found. It was either deleted or the user has no avatar set.");
            userAvatar = null;
        }
    }


    /**
     * @return the users phone number if he has set one, <b>null</b> if not
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @return the users threema id
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * @return the nickname set by the user
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return the users e-mail if he has set one, <b>null</b> if not
     */
    public String getEMail() {
        return eMail;
    }

    /**
     * @return the users avatar, <b>null</b> if user has no avatar set
     */
    public Avatar getUserAvatar() {
        return userAvatar;
    }

    /**
     * @return list of Threema IDs blocked by the user
     */
    public String[] getBlockedList() {
        return blockedList;
    }
}
