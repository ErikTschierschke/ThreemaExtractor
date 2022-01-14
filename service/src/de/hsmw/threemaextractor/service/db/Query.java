package de.hsmw.threemaextractor.service.db;

class Query {

    final static String GET_ALL_CONTACTS = "SELECT * FROM contacts;";
    final static String GET_ALL_MESSAGES = "SELECT * FROM message;";

    final static String GET_ALL_GROUPS = "SELECT * FROM m_group;";
    final static String GET_MEMBERS_BY_GROUP_ID =
            "SELECT * FROM group_member " +
                    "WHERE groupId = %d;";
    final static String GET_MESSAGES_BY_GROUP_ID =
            "SELECT * FROM m_group_message " +
                    "WHERE groupId = %d;";

    final static String GET_ALL_DISTRIBUTION_LISTS = "SELECT * FROM distribution_list;";
    final static String GET_MEMBERS_BY_DISTRIBUTION_LIST_ID =
            "SELECT * FROM distribution_list_member " +
                    "WHERE distributionListId = %d;";
    final static String GET_MESSAGES_BY_DISTRIBUTION_LIST_ID =
            "SELECT * FROM distribution_list_message " +
                    "WHERE distributionListId = %d;";

    final static String GET_ALL_BALLOTS = "SELECT * FROM ballot;";
    final static String GET_VOTES_BY_BALLOT_ID =
            "SELECT *, GROUP_CONCAT(votingIdentity) as identities " +
                    "FROM ballot_choice LEFT JOIN " +
                    "    (SELECT * FROM ballot_vote WHERE choice = 1) " +
                    "    ON ballot_choice.id = ballotChoiceId " +
                    "WHERE ballot_choice.ballotId = %d " +
                    "GROUP BY ballot_choice.id;";
}
