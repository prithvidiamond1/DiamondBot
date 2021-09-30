package org.prithvidiamond1.DB.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class DiscordServer {

    @Id
    private String id;

    @Field
    private String guildPrefix;

    public DiscordServer(){}

    public DiscordServer(String id, String guildPrefix) {
        this.id = id;
        this.guildPrefix = guildPrefix;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGuildPrefix() {
        return guildPrefix;
    }

    public void setGuildPrefix(String guildPrefix) {
        this.guildPrefix = guildPrefix;
    }

    @Override
    public String toString() {
        return String.format("Server[id='%s', guildPrefix='%s']", id, guildPrefix);
    }
}
