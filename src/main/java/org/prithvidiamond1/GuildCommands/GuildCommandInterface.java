package org.prithvidiamond1.GuildCommands;

import org.javacord.api.event.message.MessageCreateEvent;

public interface GuildCommandInterface {
    void runCommand(MessageCreateEvent event);
}
