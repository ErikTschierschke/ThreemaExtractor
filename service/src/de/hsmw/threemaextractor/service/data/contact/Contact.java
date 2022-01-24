package de.hsmw.threemaextractor.service.data.contact;


import de.hsmw.threemaextractor.service.data.Avatar;

import java.io.File;

/**
 * contact data record
 *
 * @param identity          Threema ID
 * @param firstName         first name (specified by user), <b>null</b> if not set
 * @param lastName          last name (specified by user), <b>null</b> if not set
 * @param nickname          nickname (specified by contact)
 * @param androidContactId  ID of associated android contact, <b>null</b> if contact is not saved in Android Contacts app
 * @param verificationLevel contacts trust level<p>
 *                          <ul>
 *                          <li><i>0</i> - contact is only known by Threema ID
 *                          <li><i>1</i> - contact is linked to Android Contacts app by phone number or e-mail
 *                          <li><i>2</i> - id and public key of contact have been manually checked by scanning the contact's QR code
 *                          </ul>
 * @param isHidden          contact is hidden in app (requires Android display lock to see)
 * @param contactAvatar     avatar of the contact, <b>null</b> if the contact has no avatar or the encrypted file was deleted (see {@link Avatar#getContactAvatarFile(File, String, boolean)})
 */
public record Contact(String identity, String firstName, String lastName,
                      String nickname, String androidContactId, int verificationLevel,
                      boolean isHidden, Avatar contactAvatar) {
}
