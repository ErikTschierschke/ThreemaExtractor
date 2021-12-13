package de.hsmw.threemaextractor.service.data.message;

import java.util.Date;

/**
 * text message data record<p>
 *
 * for generic attributes see {@link IMessage}
 *
 * @param text the message content
 */
public record TextMessage(String uid, String identity, boolean isOutgoing,
                          State state, Date utcSent, Date utcReceived, Date utcRead,
                          String text) implements IMessage {


    /**
     * @hidden
     */
    @Override
    public int compareTo(IMessage message) {
        return utcSent.compareTo(message.utcSent());
    }
}
