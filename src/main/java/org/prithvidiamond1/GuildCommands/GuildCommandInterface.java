package org.prithvidiamond1.GuildCommands;

import org.javacord.api.event.message.MessageCreateEvent;

/**
 * Interface that can be used to create message event listeners
 */
public interface GuildCommandInterface {
    /**
     * Method that dictates what a command should do after being processed
     * @param event the event that called the command
     */
    void runCommand(MessageCreateEvent event);
}
