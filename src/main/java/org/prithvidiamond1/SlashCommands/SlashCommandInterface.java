package org.prithvidiamond1.SlashCommands;

import org.javacord.api.event.interaction.SlashCommandCreateEvent;

/**
 * Interface that can be used to create slash command event listeners
 */
public interface SlashCommandInterface {
    /**
     * Method that dictates what a command should do after being processed
     * @param event the event that called the command
     */
    void runCommand(SlashCommandCreateEvent event);
}
