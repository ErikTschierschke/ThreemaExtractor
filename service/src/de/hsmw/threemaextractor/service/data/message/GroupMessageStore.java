package de.hsmw.threemaextractor.service.data.message;

import java.util.TreeSet;

/**
 * stores messages of a group
 */
public class GroupMessageStore implements IMessageStore {

    private final TreeSet<IMessage> messages = new TreeSet<>();

    @Override
    public void add(IMessage message) {
        messages.add(message);
    }

    public TreeSet<IMessage> getMessages() {
        return messages;
    }
}
