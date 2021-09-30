package org.prithvidiamond1.SlashCommands;

import org.javacord.api.event.interaction.SlashCommandCreateEvent;

public interface SlashCommandInterface {
    void runCommand(SlashCommandCreateEvent event);
}
