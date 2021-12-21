package de.hsmw.threemaextractor.service.data.message;

import java.util.Date;

/**
 * voip status message data record<p>
 * <p>
 * for generic attributes see {@link IMessage}
 *
 * @param status       {@link Status} of the voip call
 * @param rejectReason {@link RejectReason} of the voip call, <b>null</b> if {@code status} is not {@code REJECTED}
 * @param duration     call duration in seconds, <b>-1</b> if {@code status} is not {@code FINISHED}
 */
public record VoipMessage(String uid, String identity, boolean isOutgoing,
                          State state, Date utcSent, Date utcReceived, Date utcRead,
                          Status status, RejectReason rejectReason, int duration) implements IMessage {

    /**
     * gets the call status by the status number from the database
     * see <a href="https://github.com/threema-ch/threema-android/blob/0b6543eafe325c37d25ae06e87802c5479bee099/app/src/main/java/ch/threema/storage/models/data/status/VoipStatusDataModel.java#L29">VoipStatusDataModel.java</a>
     *
     * @hidden
     */
    public static Status getStatusByInt(int statusNumber) {
        return switch (statusNumber) {
            case 1 -> Status.MISSED;
            case 2 -> Status.FINISHED;
            case 3 -> Status.REJECTED;
            case 4 -> Status.ABORTED;
            default -> null;
        };
    }

    /**
     * gets the reject reason from the reject reason number in the database
     * see <a href="https://github.com/threema-ch/threema-android/blob/0b6543eafe325c37d25ae06e87802c5479bee099/domain/src/main/java/ch/threema/domain/protocol/csp/messages/voip/VoipCallAnswerData.java#L176">RejectReason</a>
     *
     * @hidden
     */
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

    /**
     * @hidden
     */
    @Override
    public int compareTo(IMessage message) {
        return utcSent().compareTo(message.utcSent());
    }


    public enum Status {
        MISSED, FINISHED, REJECTED, ABORTED
    }

    public enum RejectReason {
        UNKNOWN, BUSY, TIMEOUT, REJECTED, DISABLED, OFF_HOURS
    }

}
