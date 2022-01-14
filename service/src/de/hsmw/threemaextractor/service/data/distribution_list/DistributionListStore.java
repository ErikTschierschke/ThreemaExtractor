package de.hsmw.threemaextractor.service.data.distribution_list;

import de.hsmw.threemaextractor.service.file.MainDatabase;

import java.util.HashMap;

/**
 * stores distribution lists from main database
 *
 * @see MainDatabase#getDistributionLists() ()
 */
public class DistributionListStore {

    private final HashMap<String, DistributionList> distributionLists = new HashMap<>();

    /**
     * add distribution list
     *
     * @hidden
     */
    public void add(DistributionList distributionList) {
        distributionLists.put(distributionList.name(), distributionList);
    }

    /**
     * @return a specific distribution list by name
     */
    public DistributionList getByName(String name) {
        return distributionLists.get(name);
    }

    /**
     * @return all distribution lists in format {@code HashMap<list name, DistributionList>}
     */
    public HashMap<String, DistributionList> getAll() {
        return distributionLists;
    }
}
