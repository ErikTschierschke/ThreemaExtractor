package de.hsmw.threemaextractor.service.data.group;

import de.hsmw.threemaextractor.service.data.message.GroupMessageStore;

import java.util.HashMap;

/**
 * data class for groups
 * <p>
 * members stores (memberIdentity, isActive)
 */
public record Group(int id, String name, String creatorIdentity, HashMap<String, Boolean> members,
                    GroupMessageStore messages, GroupAvatarFile groupAvatarFile) {
}
