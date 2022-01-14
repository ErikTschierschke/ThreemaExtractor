package de.hsmw.threemaextractor.service.data.ballot;

import java.util.Date;
import java.util.HashMap;

/**
 * ballot data record
 *
 * @param id               ballot id from the database
 * @param creatorIdentity  Threema ID of the ballot creator
 * @param text             ballot text
 * @param isClosed         whether the ballot is closed (ballot closes when all group members have voted or the creator chooses to close it)
 * @param isMultipleChoice <b>true</b>: ballot is multiple choice, <b>false</b>: ballot is single choice
 * @param isResultsOnClose <b>true</b>: results are visible when ballot closes, <b>false</b>: results are always visible
 * @param createdAt        timestamp when ballot was created
 * @param modifiedAt       timestamp when ballot was last edited
 * @param lastViewedAt     timestamp when ballot was last viewed (by the user)
 * @param results          the vote results<p>
 *                         format: {@code [option : String, voterIdentities : String[]]} (if nobody voted for an option {@code voterIdentities} is <b>null</b>)
 */
public record Ballot(int id, String creatorIdentity, String text,
                     boolean isClosed, boolean isMultipleChoice, boolean isResultsOnClose,
                     Date createdAt, Date modifiedAt, Date lastViewedAt,
                     HashMap<String, String[]> results) {

}
