package de.hsmw.threemaextractor.service.data.message;

import de.hsmw.threemaextractor.service.data.group.Group;

import java.util.TreeSet;

/**
 * stores messages for a group conversation
 *
 * @see Group#messages()
 */
public class GroupMessageStore implements IMessageStore {

    private final TreeSet<IMessage> messages = new TreeSet<>();

    /**
     * adds a message
     *
     * @hidden
     */
    @Override
    public void add(IMessage message) {
        messages.add(message);
    }

    /**
     * get all messages of the group conversation as {@code TreeSet}
     */
    public TreeSet<IMessage> getMessages() {
        return messages;
    }
}
