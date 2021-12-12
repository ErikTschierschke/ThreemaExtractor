package de.hsmw.threemaextractor.service.main;

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

public class UserProfile {

    private String phoneNumber;
    private String threemaId;
    private String nickname;
    private String eMail;

    public UserProfile(File appPreferencesFile) throws ParserConfigurationException, IOException, SAXException {

        // threema safe hash contains illegal XML chars, remove it
        String appPreferences = Files.readString(appPreferencesFile.toPath())
                .replaceAll("(.*(pref_threema_safe_hash_string).*)", "");

        // load XML file
        Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new InputSource(new StringReader(appPreferences)));
        document.normalize();
        NodeList items = document.getFirstChild().getChildNodes();

        // set user profile information
        for (int i = 0; i < items.getLength(); i++) {
            if (items.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element item = (Element) items.item(i);

                if (item.getAttribute("name").equals("linked_mobile"))
                    phoneNumber = item.getTextContent();
                if (item.getAttribute("name").equals("identity"))
                    threemaId = item.getTextContent();
                if (item.getAttribute("name").equals("nickname"))
                    nickname = item.getTextContent();
                if (item.getAttribute("name").equals("linked_email"))
                    eMail = item.getTextContent();
            }
        }
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getThreemaId() {
        return threemaId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEMail() {
        return eMail;
    }

}
