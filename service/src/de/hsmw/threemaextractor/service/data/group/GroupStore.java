package de.hsmw.threemaextractor.service.data.group;

import de.hsmw.threemaextractor.service.file.MainDatabase;

import java.util.HashMap;

/**
 * stores groups from main database
 *
 * @see MainDatabase#getGroups()
 */
public class GroupStore {

    private final HashMap<String, Group> groups = new HashMap<>();

    /**
     * add a group to the store
     *
     * @hidden
     */
    public void add(Group group) {
        groups.put(group.name(), group);
    }

    /**
     * @return a specific group by name
     */
    public Group getByName(String name) {
        return groups.get(name);
    }

    /**
     * @return all groups in format {@code HashMap<group name, Group>}
     */
    public HashMap<String, Group> getAll() {
        return groups;
    }
}
