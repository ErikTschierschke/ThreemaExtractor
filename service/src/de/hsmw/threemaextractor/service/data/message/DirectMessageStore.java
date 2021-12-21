package de.hsmw.threemaextractor.service.data.message;

import java.util.HashMap;
import java.util.TreeSet;

/**
 * stores messages from all direct conversations
 */
public class DirectMessageStore implements IMessageStore {

    private final HashMap<String, TreeSet<IMessage>> messages = new HashMap<>();

    /**
     * adds a message
     *
     * @hidden
     */
    @Override
    public void add(IMessage message) {

        TreeSet<IMessage> conversation = messages.getOrDefault(message.identity(), new TreeSet<>());
        conversation.add(message);
        messages.put(message.identity(), conversation);
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
