package de.hsmw.threemaextractor.service.data.message;

/**
 * interface for DirectMessageStore and GroupMessageStore
 *
 * @see DirectMessageStore
 * @see GroupMessageStore
 */
public interface IMessageStore {

    void add(IMessage message);
}
