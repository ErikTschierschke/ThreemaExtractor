package de.hsmw.threemaextractor.service.data.message;

import java.util.Date;

/**
 * interface for all data records<p>
 * <p>
 * extends {@code Comparable} for ordered storage in {@code TreeSet}s (see {@link DirectMessageStore}, {@link GroupMessageStore})
 */
public interface IMessage extends Comparable<IMessage> {

    /**
     * <i>(automaticly implemented by record classes)</i>
     *
     * @return the message's UID from the database
     */
    String uid();

    /**
     * <i>(automaticly implemented by record classes)</i>
     *
     * @return in <b>direct chats</b>: Threema ID of the chat partner<p>
     * in <b>groups</b>: Threema ID of the message sender
     */
    String identity();

    /**
     * <i>(automaticly implemented by record classes)</i>
     *
     * @return whether the message was sent by the user
     */
    boolean isOutgoing();

    //TODO fix javadoc for timestamps

    /**
     * <i>(automaticly implemented by record classes)</i>
     *
     * @return timestamp when the message was sent, <b>null</b> if message wasn't sent
     */
    Date utcSent();

    /**
     * <i>(automaticly implemented by record classes)</i>
     *
     * @return timestamp when the message was received
     */
    Date utcReceived();

    /**
     * <i>(automaticly implemented by record classes)</i>
     *
     * @return timestamp when the message was read, <b>null</b> if message wasn't read
     */
    Date utcRead();

    /**
     * message state<p>
     * see <a href="https://github.com/threema-ch/threema-android/blob/main/app/src/main/java/ch/threema/storage/models/MessageState.java">MessageState.java</a>
     */
    enum State {
        PENDING,
        TRANSCODING,
        SENDING,
        SENDFAILED,
        SENT,
        DELIVERED,
        READ,
        CONSUMED,
        USERACK,
        USERDEC
    }
}
