package org.prithvidiamond1.Commands;

import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

public interface Command {
    void runCommand(MessageCreateEvent event);

    void runCommand(SlashCommandCreateEvent event);
}
