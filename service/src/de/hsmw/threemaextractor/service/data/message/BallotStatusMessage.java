package de.hsmw.threemaextractor.service.data.message;

import de.hsmw.threemaextractor.service.data.ballot.Ballot;

import java.util.Date;

/**
 * ballot status message data record<p>
 * <p>
 * for generic attributes see {@link IMessage}
 *
 * @param action   the {@link Action} that the status message represents
 * @param ballotId the ID of the corresponding ballot (see {@link Ballot#id()})
 */
public record BallotStatusMessage(String uid, String identity, boolean isOutgoing,
                                  State state, Date utcSent, Date utcReceived, Date utcRead,
                                  Action action, int ballotId) implements IMessage {


    /**
     * gets the action by the corresponding number in the database
     * (see https://github.com/threema-ch/threema-android/blob/0b6543eafe325c37d25ae06e87802c5479bee099/app/src/main/java/ch/threema/storage/models/data/media/BallotDataModel.java#L40)
     *
     * @hidden
     */
    public static Action getActionByInt(int actionNumber) {
        return switch (actionNumber) {
            case 1 -> Action.CREATED;
            case 2 -> Action.MODIFIED;
            case 3 -> Action.CLOSED;
            default -> null;
        };
    }

    /**
     * @hidden
     */
    @Override
    public int compareTo(IMessage message) {
        return utcSent.compareTo(message.utcSent());
    }

    public enum Action {
        CREATED, MODIFIED, CLOSED
    }
}

