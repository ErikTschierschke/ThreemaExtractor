package de.hsmw.threemaextractor.service.main;

import de.hsmw.threemaextractor.service.data.Contact;
import de.hsmw.threemaextractor.service.data.ContactStore;
import de.hsmw.threemaextractor.service.file.UserProfile;
import de.hsmw.threemaextractor.service.data.group.Group;
import de.hsmw.threemaextractor.service.data.message.*;
import de.hsmw.threemaextractor.service.file.MainDatabase;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * turns {@link IMessageStore} into readable chat representation
 */
public class ChatVisualizer {

    private final String userName;
    private final boolean useCustomNames;
    private final ContactStore contactStore;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM. HH:mm");

    /**
     * @param userName       user name used for outgoing messages (e.g. {@link UserProfile#getNickname()})
     * @param contactStore   the {@link ContactStore} retrieved from the database ({@link MainDatabase#getContacts()})
     * @param useCustomNames <b>true:</b> use user specified first + last names
     *                       <b>false:</b> use nicknames (specified by contacts)
     * @param useLocalTime   <b>true:</b> timestamps in "Europe/Berlin" time -
     *                       <b>false:</b> timestamps in UTC
     */
    public ChatVisualizer(String userName, ContactStore contactStore, boolean useCustomNames, boolean useLocalTime) {
        this.userName = userName;
        this.useCustomNames = useCustomNames;
        this.contactStore = contactStore;

        if (useLocalTime) {
            TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));
        }

    }

    /**
     * if no userName is provided, "Suspect" is used
     */
    public ChatVisualizer(ContactStore contactStore, boolean useCustomNames, boolean useLocalTime) {
        this("Suspect", contactStore, useCustomNames, useLocalTime);
    }

    /**
     * visualizes a direct chat
     *
     * @param directMessageStore the {@link DirectMessageStore} retireved from database ({@link MainDatabase#getDirectMessages()})
     * @param partnerId          Threema ID of the chat partner
     */
    public String visualizeDirectConversation(DirectMessageStore directMessageStore, String partnerId) {
        StringBuilder conversation = new StringBuilder();
        TreeSet<IMessage> messages = directMessageStore.getByIdentity(partnerId);

        for (IMessage message : messages) {
            conversation.append(messageToString(message)).append("\n\n");
        }

        return conversation.toString();
    }

    /**
     * visualizes a group chat
     *
     * @param groupMessageStore a {@link GroupMessageStore} ({@link Group#messages()})
     */
    public String visualizeGroupConversation(GroupMessageStore groupMessageStore) {
        StringBuilder conversation = new StringBuilder();
        TreeSet<IMessage> messages = groupMessageStore.getMessages();

        for (IMessage message : messages) {
            conversation.append(messageToString(message)).append("\n\n");
        }

        return conversation.toString();
    }

    /**
     * turns a single {@link IMessage} into a readable String
     */
    public String messageToString(IMessage message) {
        String msg;
        if (message.isOutgoing()) {
            msg = "[" + userName + " - " + dateFormat.format(message.utcSent()) + "]\n";
        } else {
            // group status messages don't have an identity
            if (message.identity() == null) {
                msg = "[" + dateFormat.format(message.utcReceived()) + "]\n";
            } else {
                msg = "[" + getContactName(message.identity()) + " - " + dateFormat.format(message.utcReceived()) + "]\n";
            }

        }

        if (message instanceof TextMessage textMessage) {
            msg += textMessage.text();
        }
        if (message instanceof MediaMessage mediaMessage) {
            msg += "<File - " + mediaMessage.getUniquePlainName() + ">";
        }
        if (message instanceof LocationMessage locationMessage) {
            msg += "<Location - " + locationMessage.address() + " Lat: " + locationMessage.lat() + " Lon: " + locationMessage.lon() + ">";
        }
        if (message instanceof VoipMessage voipMessage) {
            msg += "<Voip Call - " + voipMessage.status().toString();
            if (voipMessage.rejectReason() != null) {
                msg += " Reason: " + voipMessage.rejectReason().name();
            }
            if (voipMessage.duration() != -1) {
                msg += " Duration: " + voipMessage.duration() + "s";
            }
            msg += ">";
        }

        return msg;
    }

    private String getContactName(String threemaId) {
        Contact contact = contactStore.getById(threemaId);
        if (useCustomNames) {
            return contact.firstName() + " " + contact.lastName();
        }
        return contact.nickname();
    }
}
