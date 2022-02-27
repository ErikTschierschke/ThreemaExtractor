package de.hsmw.threemaextractor.service.data.message;

import de.hsmw.threemaextractor.service.data.group.Group;

import java.util.Date;

/**
 * interface for all data records<p>
 * <p>
 * extends {@code Comparable} for ordered storage in {@code TreeSet}s (see {@link DirectMessageStore}, {@link Group#messages()})
 */
public interface IMessage extends Comparable<IMessage> {

    /**
     * <i>(automatically implemented by the message type data records)</i>
     *
     * @return the message's UID from the database
     */
    String uid();

    /**
     * <i>(automatically implemented by the message type data records)</i>
     *
     * @return in <b>direct chats</b>: Threema ID of the chat partner<p>
     * in <b>groups</b>: Threema ID of the message sender, <b>null</b> if message is outgoing
     */
    String identity();

    /**
     * <i>(automatically implemented by the message type data records)</i>
     *
     * @return whether the message was sent by the user
     */
    boolean isOutgoing();

    //TODO fix javadoc for timestamps

    /**
     * <i>(automatically implemented by the message type data records)</i>
     *
     * @return timestamp when the message was sent, <b>null</b> if message wasn't sent
     */
    Date utcSent();

    /**
     * <i>(automatically implemented by the message type data records)</i>
     *
     * @return timestamp when the message was received
     */
    Date utcReceived();

    /**
     * <i>(automatically implemented by the message type data records)</i>
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
