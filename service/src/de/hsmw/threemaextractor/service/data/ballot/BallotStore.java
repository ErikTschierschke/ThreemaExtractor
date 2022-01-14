package de.hsmw.threemaextractor.service.data.ballot;

import de.hsmw.threemaextractor.service.file.MainDatabase;

import java.util.Collection;
import java.util.HashMap;

/**
 * stores ballots from main database
 *
 * @see MainDatabase#getBallots()
 */
public class BallotStore {

    private final HashMap<Integer, Ballot> ballots = new HashMap<>();

    /**
     * add a ballot
     *
     * @hidden
     */
    public void add(Ballot ballot) {
        ballots.put(ballot.id(), ballot);
    }

    /**
     * @return a specific ballot by ID
     */
    public Ballot getById(int id) {
        return ballots.get(id);
    }

    /**
     * @return all stored ballots
     */
    public Collection<Ballot> getAll() {
        return ballots.values();
    }
}
