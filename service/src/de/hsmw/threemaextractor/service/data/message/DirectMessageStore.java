package de.hsmw.threemaextractor.service.data.message;

import java.util.HashMap;
import java.util.TreeSet;

public class DirectMessageStore implements IMessageStore {

    private final HashMap<String, TreeSet<IMessage>> messages = new HashMap<>();

    @Override
    public void add(IMessage message) {

        TreeSet<IMessage> conversation = messages.getOrDefault(message.identity(), new TreeSet<>());
        conversation.add(message);
        messages.put(message.identity(), conversation);
    }

    public TreeSet<IMessage> getByIdentity(String identity) {
        return messages.get(identity);
    }
}
