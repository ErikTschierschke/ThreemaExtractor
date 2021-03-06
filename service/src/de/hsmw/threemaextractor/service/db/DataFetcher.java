package de.hsmw.threemaextractor.service.db;

import de.hsmw.threemaextractor.service.data.Avatar;
import de.hsmw.threemaextractor.service.data.ballot.Ballot;
import de.hsmw.threemaextractor.service.data.ballot.BallotStore;
import de.hsmw.threemaextractor.service.data.contact.Contact;
import de.hsmw.threemaextractor.service.data.contact.ContactStore;
import de.hsmw.threemaextractor.service.data.distribution_list.DistributionList;
import de.hsmw.threemaextractor.service.data.distribution_list.DistributionListStore;
import de.hsmw.threemaextractor.service.data.group.Group;
import de.hsmw.threemaextractor.service.data.group.GroupStore;
import de.hsmw.threemaextractor.service.data.message.*;
import de.hsmw.threemaextractor.service.file.MasterKey;
import de.hsmw.threemaextractor.service.main.JsonUtils;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * automatically fetches all available data from the database
 */
public class DataFetcher {

    private final Connection connection;
    private final ContactStore contactStore;
    private final DirectMessageStore directMessageStore;
    private final GroupStore groupStore;
    private final DistributionListStore distributionListStore;
    private final BallotStore ballotStore;

    private final MasterKey masterKey;
    private final File mediaDir;

    /**
     * initialize the DataFetcher by passing a SQL connection to the database, the master key and (at best empty)
     * object stores
     *
     * @param connection            a {@code Connection} to the Threema database
     * @param contactStore
     * @param messageStore
     * @param groupStore
     * @param distributionListStore
     * @param ballotStore
     * @param masterKey             the master key for the database
     * @param mediaDir              the media directory
     */
    public DataFetcher(Connection connection, ContactStore contactStore, DirectMessageStore messageStore, GroupStore groupStore,
                       DistributionListStore distributionListStore, BallotStore ballotStore,
                       MasterKey masterKey, File mediaDir) {
        this.connection = connection;
        this.contactStore = contactStore;
        this.directMessageStore = messageStore;
        this.groupStore = groupStore;
        this.distributionListStore = distributionListStore;
        this.ballotStore = ballotStore;
        this.masterKey = masterKey;
        this.mediaDir = mediaDir;
    }

    /**
     * fetches all available data
     */
    public void fetchAll() throws SQLException {

        fetchContacts();
        fetchDirectMessages();
        fetchGroups();
        fetchDistributionLists();
        fetchBallots();
    }

    /**
     * fetches threema contacts from contacts table
     */
    public void fetchContacts() throws SQLException {

        ResultSet results = connection.createStatement().executeQuery(Query.GET_ALL_CONTACTS);

        while (results.next()) {

            String identity = results.getString("identity");

            // try to retrieve contact avatar file
            Avatar avatarFile = null;
            if (!identity.equals("ECHOECHO")) {
                try {

                    avatarFile = new Avatar(Avatar.getContactAvatarFile(mediaDir, identity, false),
                            masterKey);
                } catch (IOException e) {
                    System.out.println("[WARNING] User avatar file not found. It was either deleted or the user hat no avatar set.");
                }
            }


            Contact contact = new Contact(
                    results.getString("identity"),
                    results.getString("firstName"),
                    results.getString("lastName"),
                    results.getString("publicNickName"),
                    results.getString("androidContactId"),
                    results.getInt("verificationLevel"),
                    results.getBoolean("isHidden"),
                    avatarFile);

            contactStore.add(contact);
        }
    }

    /**
     * fetches available direct messages from the message table
     */
    public void fetchDirectMessages() throws SQLException {

        ResultSet results = connection.createStatement().executeQuery(Query.GET_ALL_MESSAGES);
        TreeSet<IMessage> messages = new TreeSet<>();
        addMessagesToSet(results, messages);
        directMessageStore.setMessages(messages);
    }

    private void addMessagesToSet(ResultSet results, TreeSet<IMessage> messageSet) throws SQLException {
        IMessage message = null;

        while (results.next()) {
            // switch message class by type code
            // (see https://github.com/threema-ch/threema-android/blob/bfdef5ff49447f587c6f99008a01bf6a394a9fa2/app/src/main/java/ch/threema/storage/models/MessageType.java#L24)
            // (some types are no longer used)
            switch (results.getInt("type")) {
                case 0 -> message = fetchTextMessage(results);
                case 4 -> message = fetchLocationMessage(results);
                case 8 -> message = fetchMediaMessage(results);
                case 9 -> message = fetchVoipMessage(results);
                case 7 -> message = fetchBallotStatusMessage(results);
                default -> System.err.println("Unknown type code " + results.getInt("type"));
            }
            messageSet.add(message);
        }

    }

    private TextMessage fetchTextMessage(ResultSet result) throws SQLException {

        return new TextMessage(
                result.getString("uid"),
                result.getString("identity"),
                result.getBoolean("outbox"),
                getState(result.getString("state")),
                result.getDate("createdAtUtc"),
                getReceivedTimestamp(result),
                getReadTimestamp(result),
                result.getString("body"));
    }

    private LocationMessage fetchLocationMessage(ResultSet result) throws SQLException {

        // parse json array in body
        // structure: [latitude (double), longitude (double), accuracy (long), address (String), poi (String)]
        // see https://github.com/threema-ch/threema-android/blob/23f00dbb2ef2869a12f95f2cb285740f0aef0154/app/src/main/java/ch/threema/storage/models/data/LocationDataModel.java#L85
        JsonArray body = JsonUtils.parseJsonBody(result.getString("body"));

        return new LocationMessage(
                result.getString("uid"),
                result.getString("identity"),
                result.getBoolean("outbox"),
                getState(result.getString("state")),
                result.getDate("createdAtUtc"),
                getReceivedTimestamp(result),
                getReadTimestamp(result),
                result.getString("caption"),
                body.getJsonNumber(0).doubleValue(),
                body.getJsonNumber(1).doubleValue(),
                body.getJsonNumber(2).longValue(),
                body.getString(3));

    }

    private MediaMessage fetchMediaMessage(ResultSet result) throws SQLException {

        // parse json array in body
        // structure: [fileBlobId (byte[]), encryptionKey (byte[]), mimeType (String), fileSize (int), fileName (String),
        //             typeId (int), isDownloaded (bool), caption (String), thumbnailMimeType (String), metaData (Map)]
        // see https://github.com/threema-ch/threema-android/blob/0b6543eafe325c37d25ae06e87802c5479bee099/app/src/main/java/ch/threema/storage/models/data/media/FileDataModel.java#L272
        JsonArray body = JsonUtils.parseJsonBody(result.getString("body"));

        return new MediaMessage(
                result.getString("uid"),
                result.getString("identity"),
                result.getBoolean("outbox"),
                getState(result.getString("state")),
                result.getDate("createdAtUtc"),
                getReceivedTimestamp(result),
                getReadTimestamp(result),
                body.getString(4),
                body.getString(2),
                result.getString("caption")
        );
    }

    private VoipMessage fetchVoipMessage(ResultSet result) throws SQLException {

        // parse json array in body
        // structure: [1, {status?: int, duration?: int, reason?: int}]
        // see https://github.com/threema-ch/threema-android/blob/23f00dbb2ef2869a12f95f2cb285740f0aef0154/app/src/main/java/ch/threema/storage/models/data/status/VoipStatusDataModel.java#L76
        JsonObject body = JsonUtils.parseJsonBody(result.getString("body")).getJsonObject(1);

        return new VoipMessage(
                result.getString("uid"),
                result.getString("identity"),
                result.getBoolean("outbox"),
                getState(result.getString("state")),
                result.getDate("createdAtUtc"),
                getReceivedTimestamp(result),
                getReadTimestamp(result),

                // default value -1 results status/reason null
                VoipMessage.getStatusByInt(body.getInt("status", -1)),
                VoipMessage.getRejectReasonByInt(body.getInt("reason", -1)),
                body.getInt("duration", -1)
        );
    }

    private BallotStatusMessage fetchBallotStatusMessage(ResultSet result) throws SQLException {

        // parse json array in body
        // structure: [Action : int, ballotId : int]
        // see https://github.com/threema-ch/threema-android/blob/0b6543eafe325c37d25ae06e87802c5479bee099/app/src/main/java/ch/threema/storage/models/data/media/BallotDataModel.java#L69
        JsonArray body = JsonUtils.parseJsonBody(result.getString("body"));

        return new BallotStatusMessage(
                result.getString("uid"),
                result.getString("identity"),
                result.getBoolean("outbox"),
                getState(result.getString("state")),
                result.getDate("createdAtUtc"),
                getReceivedTimestamp(result),
                getReadTimestamp(result),

                BallotStatusMessage.getActionByInt(body.getInt(0)),
                body.getInt(1)
        );
    }

    /**
     * fetches group data from m_group, m_group_message and group_member tables
     */
    public void fetchGroups() throws SQLException {

        ResultSet groupsResults = connection.createStatement().executeQuery(Query.GET_ALL_GROUPS);

        while (groupsResults.next()) {
            int id = groupsResults.getInt("id");

            // fetch group members
            ResultSet memberResults = connection.createStatement().executeQuery(String.format(Query.GET_MEMBERS_BY_GROUP_ID, id));
            HashMap<String, Boolean> members = new HashMap<>();
            while (memberResults.next()) {
                members.put(memberResults.getString("identity"),
                        memberResults.getBoolean("isActive"));
            }
            // fetch group messages
            ResultSet messageResults = connection.createStatement().executeQuery(String.format(Query.GET_MESSAGES_BY_GROUP_ID, id));
            TreeSet<IMessage> messages = new TreeSet<>();
            addMessagesToSet(messageResults, messages);

            //try to retrieve group avatar
            Avatar avatarFile = null;
            try {
                avatarFile = new Avatar(Avatar.getGroupAvatarFile(mediaDir, id), masterKey);
            } catch (IOException e) {
                System.out.println("[WARNING] Avatar for group \"" + groupsResults.getString("name") + "\" was not found." +
                        " The file was either deleted or the group has no avatar set.");
            }

            groupStore.add(new Group(
                    id,
                    groupsResults.getString("name"),
                    groupsResults.getString("creatorIdentity"),
                    members,
                    messages,
                    avatarFile
            ));

        }
    }

    /**
     * fetch distribution lists from distribution_list, distribution_list_member and distribution_list_message tables
     */
    public void fetchDistributionLists() throws SQLException {

        ResultSet results = connection.createStatement().executeQuery(Query.GET_ALL_DISTRIBUTION_LISTS);
        while (results.next()) {
            int id = results.getInt("id");
            // fetch members
            ResultSet memberResults = connection.createStatement().executeQuery(String.format(Query.GET_MEMBERS_BY_DISTRIBUTION_LIST_ID, id));
            HashMap<String, Boolean> members = new HashMap<>();
            while (memberResults.next()) {
                members.put(memberResults.getString("identity"), memberResults.getBoolean("isActive"));
            }

            // fetch messages
            ResultSet messageResults = connection.createStatement().executeQuery(String.format(Query.GET_MESSAGES_BY_DISTRIBUTION_LIST_ID, id));
            TreeSet<IMessage> messages = new TreeSet<>();
            addMessagesToSet(messageResults, messages);

            distributionListStore.add(new DistributionList(
                    id,
                    results.getString("name"),
                    results.getString("createdAt"),
                    members,
                    messages
            ));
        }
    }

    /**
     * fetch ballots from ballot, ballot_choice and ballot_vote tables
     */
    public void fetchBallots() throws SQLException {

        ResultSet results = connection.createStatement().executeQuery(Query.GET_ALL_BALLOTS);
        while (results.next()) {
            int id = results.getInt("id");

            // fetch votes
            HashMap<String, String[]> votes = new HashMap<>();
            ResultSet voteResults = connection.createStatement().executeQuery(String.format(Query.GET_VOTES_BY_BALLOT_ID, id));
            while (voteResults.next()) {

                //convert String from voters column to array
                String votesString = voteResults.getString("identities");
                String[] votesArray;
                if (votesString != null) {
                    votesArray = votesString.split(",");
                } else {
                    votesArray = null;
                }

                votes.put(voteResults.getString("name"), votesArray);
            }

            ballotStore.add(new Ballot(
                    id,
                    results.getString("creatorIdentity"),
                    results.getString("name"),
                    results.getString("state").equals("CLOSED"),
                    results.getString("assessment").equals("MULTIPLE_CHOICE"),
                    results.getString("type").equals("RESULT_ON_CLOSE"),
                    results.getDate("createdAt"),
                    results.getDate("modifiedAt"),
                    results.getDate("lastViewedAt"),
                    votes
            ));
        }
    }

    private IMessage.State getState(String state) {
        if (state == null) {
            return null;
        }
        try {
            return IMessage.State.valueOf(state);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid message state: " + state);
            e.printStackTrace();
        }

        return null;
    }

    // Until app version 4.5 the received and read timespamps were using the colums "postedAtUtc" and "modifiedAtUtc".
    // The 4.6 update introduces the columns "deliveredAtUtc" and "readUtc".
    // Since old message records are not updated, both need to be parsed.
    private Date getReceivedTimestamp(ResultSet result) throws SQLException {

        Date date = null;

        try {
            date = result.getDate("deliveredAtUtc");
        } catch (SQLException e) {
            /*
                if "deliveredAtUtc" is not found, message is from version <4.6
                parse "postedAtUtc" instead
                may throw another SQLException -> handled by MainDatabase constructor
             */
        } finally {
            if (date == null) {
                date = result.getDate("postedAtUtc");
            }
        }

        return date;
    }

    private Date getReadTimestamp(ResultSet result) throws SQLException {
        Date date = null;
        try {
            date = result.getDate("readAtUtc");
        } catch (SQLException e) {
            // if not present parse "modifiedAtUtc instead"
        } finally {
            if (date == null) {
                date = result.getDate("modifiedAtUtc");
            }
        }

        return date;
    }
}
