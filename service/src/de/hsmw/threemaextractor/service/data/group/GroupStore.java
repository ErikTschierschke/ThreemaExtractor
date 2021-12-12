package de.hsmw.threemaextractor.service.data.group;

import java.util.HashMap;

/**
 * stores groups from main database
 */
public class GroupStore {

    private final HashMap<String, Group> groups = new HashMap<>();

    public void add(Group group) {
        groups.put(group.name(), group);
    }

    public HashMap<String, Group> getAll() {
        return groups;
    }
}
