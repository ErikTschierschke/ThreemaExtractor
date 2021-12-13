package de.hsmw.threemaextractor.service.data.group;

import de.hsmw.threemaextractor.service.data.message.GroupMessageStore;

import java.io.File;
import java.util.HashMap;

/**
 * group data record
 *
 * @param id group ID in database
 * @param name group name
 * @param creatorIdentity Threema ID of group owner (concurrently the only admin - Threema doesn't support multiple admins)
 * @param members Map of current and former group members in format {@code [Threema ID, isActive?]}<p>
 *                if {@code isActive} is {@code false} the contact has left the group or was removed
 * @param messages store of group messages (see {@link GroupMessageStore})
 * @param groupAvatar avatar of the group, <b>null</b> if the group has no avatar or the encrypted file was deleted (see {@link GroupAvatar#getFileByGroupId(File, int)})
 */
public record Group(int id, String name, String creatorIdentity, HashMap<String, Boolean> members,
                    GroupMessageStore messages, GroupAvatar groupAvatar) {
}
