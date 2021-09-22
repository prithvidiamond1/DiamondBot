package org.prithvidiamond1;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommand;

import java.util.List;

public class ServerHelperFunctions {
    /**
     * Method to remove a global slash command from the bot
     * @param api Discord bot api object
     * @param commandName String of the command to be deleted
     */
    public static void removeGlobalSlashCommand(DiscordApi api, String commandName){
        boolean commandFound = false;
        List<SlashCommand> slashCommands = api.getGlobalSlashCommands().join();
        for (SlashCommand slashCommand: slashCommands){
            if(slashCommand.getName().equals(commandName)){
                commandFound = true;
                long commandId = slashCommand.getId();
                SlashCommand commandToBeRemoved = api.getGlobalSlashCommandById(commandId).join();
                commandToBeRemoved.deleteGlobal().join();
                break;
            }
        }
        if (commandFound){
            System.out.println("The requested command was successfully found and deleted");
        }
        else{
            System.out.println("The request command was NOT found");
        }
    }
}
