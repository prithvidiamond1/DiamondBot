package org.prithvidiamond1.Commands;

import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;

/**
 * Interface that can be used create commands that have both guild and slash command functionalities
 */
public interface Command {
    /**
     * Method that runs the guild version of the command
     * @param event the guild command trigger event
     */
    void runCommand(MessageCreateEvent event);

    /**
     * Method that runs the slash version of the command
     * @param event the slash command trigger event
     */
    void runCommand(SlashCommandCreateEvent event);
}
