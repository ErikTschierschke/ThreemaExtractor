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
}
