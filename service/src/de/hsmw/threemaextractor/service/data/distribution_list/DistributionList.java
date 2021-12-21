package de.hsmw.threemaextractor.service.data.distribution_list;


import de.hsmw.threemaextractor.service.data.group.Group;
import de.hsmw.threemaextractor.service.data.message.IMessage;
import de.hsmw.threemaextractor.service.file.MainDatabase;

import java.util.HashMap;
import java.util.TreeSet;

/**
 * distribution list data record
 *
 * @param id distribution list in database
 * @param name distribution list name
 * @param createdAt creation timestamp in local time (format: {@code YYYY-MM-DD hh:mm:ss.SSS000})
 * @param members distribution list members in format {@code [Threema ID, isActive?]} (see {@link Group#members()})
 * @param messages set of messages (<i>Note</i> that these are redundantly saved in {@link MainDatabase#getDirectMessages()})
 */
public record DistributionList(int id, String name, String createdAt,
                               HashMap<String, Boolean> members, TreeSet<IMessage> messages) { }
