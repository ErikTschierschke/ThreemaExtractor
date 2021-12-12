package de.hsmw.threemaextractor.service.data.message;

import java.util.Date;

public record VoipMessage(String uid, String identity, boolean isOutgoing,
                          State state, Date utcSent, Date utcReceived, Date utcRead,
                          Status status, RejectReason rejectReason, int duration) implements IMessage {

    public static Status getStatusByInt(int statusNumber) {
        return switch (statusNumber) {
            case 1 -> Status.MISSED;
            case 2 -> Status.FINISHED;
            case 3 -> Status.REJECTED;
            case 4 -> Status.ABORTED;
            default -> null;
        };
    }

    public static RejectReason getRejectReasonByInt(int rejectReasonNumber) {
        return switch (rejectReasonNumber) {
            case 0 -> RejectReason.UNKNOWN;
            case 1 -> RejectReason.BUSY;
            case 2 -> RejectReason.TIMEOUT;
            case 3 -> RejectReason.REJECTED;
            case 4 -> RejectReason.DISABLED;
            case 5 -> RejectReason.OFF_HOURS;
            default -> null;
        };
    }

    @Override
    public int compareTo(IMessage message) {
        return utcSent().compareTo(message.utcSent());
    }

    /**
     * call status (represented by int in database)
     * <p>
     * see https://github.com/threema-ch/threema-android/blob/0b6543eafe325c37d25ae06e87802c5479bee099/app/src/main/java/ch/threema/storage/models/data/status/VoipStatusDataModel.java#L29
     */
    public enum Status {
        MISSED, FINISHED, REJECTED, ABORTED
    }

    /**
     * reject reason (represented by int in database)
     * <p>
     * see https://github.com/threema-ch/threema-android/blob/0b6543eafe325c37d25ae06e87802c5479bee099/domain/src/main/java/ch/threema/domain/protocol/csp/messages/voip/VoipCallAnswerData.java#L176
     */
    public enum RejectReason {
        UNKNOWN, BUSY, TIMEOUT, REJECTED, DISABLED, OFF_HOURS
    }

}
