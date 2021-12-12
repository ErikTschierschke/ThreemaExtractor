package de.hsmw.threemaextractor.service.data.message;

import java.util.Date;

public interface IMessage extends Comparable<IMessage> {

    // automaticly implemented by record classes
    String uid();

    String identity();

    boolean isOutgoing();

    Date utcSent();

    Date utcReceived();

    Date utcRead();

    //see https://github.com/threema-ch/threema-android/blob/main/app/src/main/java/ch/threema/storage/models/MessageState.java
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
