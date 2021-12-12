package de.hsmw.threemaextractor.service.data.message;

/**
 * interface for DirectMessageStore and GroupMessageStore to implement add()
 */
public interface IMessageStore {

    void add(IMessage message);
}
