package de.hsmw.threemaextractor.service.data.message;

import java.util.Date;

/**
 * location message data record<p>
 *
 * for generic attributes see {@link IMessage}
 *
 * @param caption description of the location
 * @param lat latitude in decimal degrees
 * @param lon longitude in decimal degrees
 * @param accuracy accuracy in meters
 * @param address address of the location
 */
public record LocationMessage(String uid, String identity, boolean isOutgoing,
                              State state, Date utcSent, Date utcReceived, Date utcRead,
                              String caption, double lat, double lon, long accuracy,
                              String address) implements IMessage {


    /**
     * @hidden
     */
    @Override
    public int compareTo(IMessage message) {
        return utcSent().compareTo(message.utcSent());
    }
}
