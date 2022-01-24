package de.hsmw.threemaextractor.service.main;

import de.hsmw.threemaextractor.service.data.contact.Contact;
import de.hsmw.threemaextractor.service.data.contact.ContactStore;
import de.hsmw.threemaextractor.service.data.group.Group;
import de.hsmw.threemaextractor.service.data.message.*;
import de.hsmw.threemaextractor.service.file.MainDatabase;
import de.hsmw.threemaextractor.service.file.UserProfile;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * turns {@link IMessageStore} into readable chat representation
 * intentionally built for internal testing, but could be useful in production as well
 */
public class ChatVisualizer {

    private final String userName;
    private final boolean useFirstLastNames;
    private final ContactStore contactStore;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM. HH:mm");

    /**
     * @param userName          user name used for outgoing messages (e.g. {@link UserProfile#getNickname()})
     * @param contactStore      the {@link ContactStore} retrieved from the database ({@link MainDatabase#getContacts()})
     * @param useFirstLastNames <b>true:</b> use user specified first + last names
     *                          <b>false:</b> use nicknames (specified by contacts)
     * @param useLocalTime      <b>true:</b> timestamps in "Europe/Berlin" time -
     *                          <b>false:</b> timestamps in UTC
     */
    public ChatVisualizer(String userName, ContactStore contactStore, boolean useFirstLastNames, boolean useLocalTime) {
        this.userName = userName;
        this.useFirstLastNames = useFirstLastNames;
        this.contactStore = contactStore;

        if (useLocalTime) {
            TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));
        }

    }

    /**
     * if no userName is provided, "Suspect" is used
     */
    public ChatVisualizer(ContactStore contactStore, boolean useFirstLastNames, boolean useLocalTime) {
        this("Suspect", contactStore, useFirstLastNames, useLocalTime);
    }

    /**
     * turns a conversation into a human-readable String
     *
     * @param messageSet a conversation as {@code TreeSet<IMessage>} (see {@link DirectMessageStore#getByIdentity(String)}, {@link Group#messages()})
     */
    public String visualizeConversation(TreeSet<IMessage> messageSet) {
        StringBuilder conversation = new StringBuilder();

        for (IMessage message : messageSet) {
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
                if (message instanceof TextMessage t) {
                    System.out.println(t.text());
                }
                msg = "[" + getContactName(message.identity()) + " - " + dateFormat.format(message.utcReceived()) + "]\n";
                System.out.println("SUCCESS");
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
        if (message instanceof BallotStatusMessage ballotStatusMessage) {
            msg += "<Ballot Status Message - BallotID: " + ballotStatusMessage.ballotId() +
                    " Action: " + ballotStatusMessage.action().toString() + ">";
        }

        return msg;
    }

    private String getContactName(String threemaId) {
        Contact contact = contactStore.getById(threemaId);
        if (useFirstLastNames) {
            return contact.firstName() + " " + contact.lastName();
        }
        return contact.nickname();
    }
}
