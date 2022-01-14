package de.hsmw.threemaextractor.service.data.contact;

import de.hsmw.threemaextractor.service.file.MainDatabase;

import java.util.Collection;
import java.util.HashMap;

/**
 * stores contacts from main database
 *
 * @see MainDatabase#getContacts()
 */
public class ContactStore {

    private final HashMap<String, Contact> contacts = new HashMap<>();

    /**
     * add a {@link Contact} to the store
     *
     * @hidden
     */
    public void add(Contact contact) {
        contacts.put(contact.identity(), contact);
    }

    /**
     * @return retrieve all {@link Contact}s from the store
     */
    public Collection<Contact> getAll() {
        return contacts.values();
    }

    /**
     * @return a specific {@link Contact} by the given {@code threemaId}
     */
    public Contact getById(String threemaId) {
        return contacts.get(threemaId);
    }
}
