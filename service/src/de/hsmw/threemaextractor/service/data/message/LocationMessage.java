package de.hsmw.threemaextractor.service.data.message;

import java.util.Date;

/**
 * location message data class
 */
public record LocationMessage(String uid, String identity, boolean isOutgoing,
                              State state, Date utcSent, Date utcReceived, Date utcRead,
                              String caption, double lat, double lon, long accuracy,
                              String address) implements IMessage {


    @Override
    public int compareTo(IMessage message) {
        return utcSent().compareTo(message.utcSent());
    }
}
