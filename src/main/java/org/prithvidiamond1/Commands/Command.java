package org.prithvidiamond1.Commands;

import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandOption;
import org.slf4j.Logger;

import java.util.List;

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

    /**
     * Method that gets the command's name
     * @return the command's name
     */
    String getName();

    /**
     * Method that gets the command's description
     * @return the command's description
     */
    String getDescription();

    /**
     * Method that gets the command's slash command options
     * @return the command's slash command options
     */
    List<SlashCommandOption> getSlashCommandOptions();

    Logger getLogger();
}
