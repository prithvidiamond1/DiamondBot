package org.prithvidiamond1;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.prithvidiamond1.DB.Models.DiscordServer;

import java.util.List;
import java.util.Optional;

public class ServerHelperFunctions {

    /**
     * Method that resolves a Javacord Server entity into its corresponding database model
     * @param server the Javacord Server entity object
     * @return returns as DiscordServer database model
     */
    public static DiscordServer resolveServerModelById(Server server){
        DiscordServer discordServer = null;
        Optional<DiscordServer> serverModel = Main.discordServerRepository.findById(String.valueOf(server.getId()));
        if (serverModel.isPresent()){
            discordServer = serverModel.get();
        }
        return discordServer;
    }

    /**
     * Method to remove a global slash command from the bot
     * @param api Discord bot api object
     * @param commandName String of the command to be deleted
     */
    public static void removeGlobalSlashCommand(DiscordApi api, String commandName){
        boolean commandFound = false;
        List<SlashCommand> slashCommands = api
                .getGlobalSlashCommands()
                .exceptionally(exception -> {
                    exception.printStackTrace();
                    return null;
                })
                .join();
        for (SlashCommand slashCommand: slashCommands){
            if(slashCommand.getName().equals(commandName)){
                commandFound = true;
                long commandId = slashCommand.getId();
                SlashCommand commandToBeRemoved = api
                        .getGlobalSlashCommandById(commandId)
                        .exceptionally(exception -> {
                            exception.printStackTrace();
                            return null;
                        })
                        .join();
                commandToBeRemoved
                        .deleteGlobal()
                        .exceptionally(exception -> {
                            exception.printStackTrace();
                            return null;
                        })
                        .join();
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
