package de.hsmw.threemaextractor.service.data.message;

import java.util.HashMap;
import java.util.TreeSet;

/**
 * stores messages from all direct conversations
 */
public class DirectMessageStore {

    private final HashMap<String, TreeSet<IMessage>> messages = new HashMap<>();


    /**
     * set the stored messages
     *
     * @param messageSet all direct messages as {@code TreeSet<IMessage>}
     * @hidden
     */
    public void setMessages(TreeSet<IMessage> messageSet) {

        // split messages by Threema ID of chat partner to get conversations
        for (IMessage message : messageSet) {
            TreeSet<IMessage> conversation = messages.getOrDefault(message.identity(), new TreeSet<>());
            conversation.add(message);
            messages.put(message.identity(), conversation);
        }
    }

    /**
     * gets a direct conversation by the partners Threema ID
     *
     * @return the direct messages with the partner as {@code TreeSet}
     */
    public TreeSet<IMessage> getByIdentity(String identity) {
        return messages.getOrDefault(identity, new TreeSet<>());
    }

    /**
     * gets all direct messages
     */
    public TreeSet<IMessage> getAll() {
        TreeSet<IMessage> messageList = new TreeSet<>();

        for (TreeSet<IMessage> set : messages.values()) {
            messageList.addAll(set);
        }

        return messageList;
    }
}
