package org.prithvidiamond1.DB.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * This class manages the backend repository (MongoDB Atlas) that holds each Discord server's preferences.
 */
@Document
public class DiscordServer {

    @Id
    private String id;

    @Field
    private String guildPrefix;

    /**
     * The constructor for the repository
     * @param id the Discord server's ID
     * @param guildPrefix the Discord server's guild prefix
     */
    public DiscordServer(String id, String guildPrefix) {
        this.id = id;
        this.guildPrefix = guildPrefix;
    }

    /**
     * Method to get the Discord server's ID
     * @return the Discord server's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Method to set the Discord server's ID
     * @param id the Discord server's ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Method to get the Discord server's current guild prefix
     * @return the Discord server's current guild prefix
     */
    public String getGuildPrefix() {
        return guildPrefix;
    }

    /**
     * Method to set the Discord server's current guild prefix
     * @param guildPrefix the Discord server's current guild prefix
     */
    public void setGuildPrefix(String guildPrefix) {
        this.guildPrefix = guildPrefix;
    }

    /**
     * Method that overrides the repository's existing toString method
     * @return returns a string with the Discord server's id and current guild prefix
     */
    @Override
    public String toString() {
        return String.format("Server[id='%s', guildPrefix='%s']", id, guildPrefix);
    }
}
