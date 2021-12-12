package de.hsmw.threemaextractor.service.data;


/**
 * Threema contact data class
 */
public record Contact(String identity, String firstName, String lastName,
                      String nickname, String androidContactId, int verificationLevel,
                      boolean isHidden) {
}
