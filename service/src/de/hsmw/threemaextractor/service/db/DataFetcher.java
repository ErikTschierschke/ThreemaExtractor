package de.hsmw.threemaextractor.service.db;

import de.hsmw.threemaextractor.service.data.Contact;
import de.hsmw.threemaextractor.service.data.ContactAvatar;
import de.hsmw.threemaextractor.service.data.ContactStore;
import de.hsmw.threemaextractor.service.data.distribution_list.DistributionList;
import de.hsmw.threemaextractor.service.data.distribution_list.DistributionListStore;
import de.hsmw.threemaextractor.service.data.group.Group;
import de.hsmw.threemaextractor.service.data.group.GroupAvatar;
import de.hsmw.threemaextractor.service.data.group.GroupStore;
import de.hsmw.threemaextractor.service.data.message.*;
import de.hsmw.threemaextractor.service.main.JsonUtils;
import de.hsmw.threemaextractor.service.file.MasterKey;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DataFetcher {

    private final Connection connection;
    private final ContactStore contactStore;
    private final DirectMessageStore directMessageStore;
    private final GroupStore groupStore;
    private final DistributionListStore distributionListStore;

    private final MasterKey masterKey;
    private final File mediaDir;

    public DataFetcher(Connection connection, ContactStore contactStore, DirectMessageStore messageStore, GroupStore groupStore, DistributionListStore distributionListStore,
                       MasterKey masterKey, File mediaDir) {
        this.connection = connection;
        this.contactStore = contactStore;
        this.directMessageStore = messageStore;
        this.groupStore = groupStore;
        this.distributionListStore = distributionListStore;
        this.masterKey = masterKey;
        this.mediaDir = mediaDir;
    }

    public void fetchAll() throws SQLException {

        fetchContacts();
        fetchDirectMessages();
        fetchGroups();
        fetchDistributionLists();
    }

    /**
     * fetches threema contacts from contacts table
     */
    public void fetchContacts() throws SQLException {

        ResultSet results = connection.createStatement().executeQuery(Query.GET_ALL_CONTACTS);

        while (results.next()) {

            String identity = results.getString("identity");

            // try to retrieve contact avatar file
            ContactAvatar avatarFile = null;
            if (!identity.equals("ECHOECHO")) {
                try {
                    
                    avatarFile = new ContactAvatar(ContactAvatar.getFileByIdentity(mediaDir, identity, false),
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

    public void fetchDirectMessages() throws SQLException {

        ResultSet results = connection.createStatement().executeQuery(Query.GET_ALL_MESSAGES);
        addMessagesToStore(results, directMessageStore);
    }

    private void addMessagesToStore(ResultSet results, IMessageStore store) throws SQLException {
        IMessage message = null;

        while (results.next()) {

            switch (results.getInt("type")) {
                case 0 -> message = fetchTextMessage(results);
                case 4 -> message = fetchLocationMessage(results);
                case 8 -> message = fetchMediaMessage(results);
                case 9 -> message = fetchVoipMessage(results);
                case 7 -> {
                } //TODO parse ballots
                default -> System.err.println("Unknown type code " + results.getInt("type"));
            }
            store.add(message);
        }

    }

    private TextMessage fetchTextMessage(ResultSet result) throws SQLException {

        return new TextMessage(
                result.getString("uid"),
                result.getString("identity"),
                result.getBoolean("outbox"),
                getState(result.getString("state")),
                // TODO fetch correct times from updated dataset
                result.getDate("postedAtUtc"),
                result.getDate("createdAtUtc"),
                result.getDate("modifiedAtUtc"),
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
                result.getDate("postedAtUtc"),
                result.getDate("createdAtUtc"),
                result.getDate("modifiedAtUtc"),
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
                result.getDate("postedAtUtc"),
                result.getDate("createdAtUtc"),
                result.getDate("modifiedAtUtc"),
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
                result.getDate("postedAtUtc"),
                result.getDate("createdAtUtc"),
                result.getDate("modifiedAtUtc"),

                // default value -1 results status/reason null
                VoipMessage.getStatusByInt(body.getInt("status", -1)),
                VoipMessage.getRejectReasonByInt(body.getInt("reason", -1)),
                body.getInt("duration", -1)
        );
    }

    /**
     * fetches group data from database
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
            GroupMessageStore groupMessageStore = new GroupMessageStore();
            addMessagesToStore(messageResults, groupMessageStore);

            //try to retrieve group avatar
            GroupAvatar avatarFile = null;
            try {
                avatarFile = new GroupAvatar(GroupAvatar.getFileByGroupId(mediaDir, id), masterKey);
            } catch (IOException e) {
                System.out.println("[WARNING] Avatar for group\"" + groupsResults.getString("name") + "\" was not found." +
                        " The file was either deleted or the group has no avatar set.");
            }

            groupStore.add(new Group(
                    id,
                    groupsResults.getString("name"),
                    groupsResults.getString("creatorIdentity"),
                    members,
                    groupMessageStore,
                    avatarFile
            ));

        }
    }

    /**
     * fetch distribution lists
     */
    public void fetchDistributionLists() throws SQLException {

        ResultSet results = connection.createStatement().executeQuery(Query.GET_ALL_DISTRIBUTION_LISTS);
        while (results.next()) {
            int id = results.getInt("id");
            System.out.println(id);
            // fetch members
            ResultSet memberResults = connection.createStatement().executeQuery(String.format(Query.GET_MEMBERS_BY_DISTRIBUTION_LIST_ID, id));
            HashMap<String, Boolean> members = new HashMap<>();
            while (memberResults.next()) {
                members.put(memberResults.getString("identity"), memberResults.getBoolean("isActive"));
            }

            // fetch messages
            ResultSet messageResults = connection.createStatement().executeQuery(String.format(Query.GET_MESSAGES_BY_DISTRIBUTION_LIST_ID, id));
            GroupMessageStore tempMessageStore = new GroupMessageStore();
            addMessagesToStore(messageResults, tempMessageStore);

            distributionListStore.add(new DistributionList(
                    id,
                    results.getString("name"),
                    results.getString("createdAt"),
                    members,
                    tempMessageStore.getMessages()
            ));
            System.out.println(distributionListStore.getByName(results.getString("name")));
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
}
