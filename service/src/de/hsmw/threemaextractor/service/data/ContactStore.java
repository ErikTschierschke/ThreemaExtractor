package de.hsmw.threemaextractor.service.data;

import java.util.Collection;
import java.util.HashMap;

/**
 * stores contacts from main database
 */
public class ContactStore {

    private final HashMap<String, Contact> contacts = new HashMap<>();

    public void add(Contact contact) {
        contacts.put(contact.identity(), contact);
    }

    public Collection<Contact> getAll() {
        return contacts.values();
    }

    public Contact getById(String threemaId) {
        return contacts.get(threemaId);
    }
}
