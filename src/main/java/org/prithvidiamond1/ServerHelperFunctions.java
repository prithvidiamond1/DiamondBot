package org.prithvidiamond1;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.DiscordEntity;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageSet;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.SlashCommand;
import org.prithvidiamond1.DB.Models.DiscordServer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


/**
 * This class holds all the server related bot helper functions
 */
public class ServerHelperFunctions {
    /**
     * Method to remove audio player controls from bot messages sent in a server text channel
     * @param textChannel the server text channel where the audio player controls need to be removed
     */
    public static void removeAudioPlayerControls(TextChannel textChannel){
        try {
            MessageSet messageSet = textChannel.getMessages(50).get();
            List<Message> messageList = new ArrayList<>(messageSet.stream().filter(message -> !message.getComponents().isEmpty()).toList());

            messageList.sort(Comparator.comparing(DiscordEntity::getCreationTimestamp));

            messageList.get(messageList.size() - 1).createUpdater().removeAllComponents().applyChanges().exceptionally(exception -> {
                Main.logger.error("Unable to remove player controls from the last sent embed!");
                Main.logger.error(exception.getMessage());
                return null;
            });
        } catch (Exception exception){
            Main.logger.error("An error occurred when trying to remove audio player controls!");
            Main.logger.error(exception.getMessage());
        }
    }

    /**
     * Method that resolves a Javacord Server entity into its corresponding database model
     * @param server the Javacord Server entity object
     * @return returns as DiscordServer database model
     */
    public static DiscordServer resolveServerModelById(Server server){
        DiscordServer discordServer = null;
        Optional<DiscordServer> serverModel = Main.discordServerRepository.findById(String.valueOf(server.getId()));
        if (serverModel.isPresent()) {
            Main.logger.trace("Server model present, getting server model...");
            discordServer = serverModel.get();
        } else {
            Main.logger.trace("Server model not present, returning null...");
        }
        return discordServer;
    }

    /**
     * Method to remove a global slash command from the bot
     * @param api Discord bot api object
     * @param commandName String of the command to be deleted
     */
    public static void removeGlobalSlashCommand(DiscordApi api, String commandName){
        Main.logger.info(String.format("Request to remove a global slash command by the name '%s' received", commandName));
        boolean commandFound = false;
        List<SlashCommand> slashCommands = api
                .getGlobalSlashCommands()
                .exceptionally(exception -> {   // Error message for failing to get the list of global slash commands
                    Main.logger.error("Unable to retrieve list of global slash commands!");
                    Main.logger.error(exception.getMessage());
                    return null;
                })
                .join();
        for (SlashCommand slashCommand: slashCommands){
            if(slashCommand.getName().equals(commandName)){
                commandFound = true;
                long commandId = slashCommand.getId();
                SlashCommand commandToBeRemoved = api
                        .getGlobalSlashCommandById(commandId)
                        .exceptionally(exception -> {   // Error message for failing to get global slash command by command id
                            Main.logger.error("Unable to retrieve global slash command by id!");
                            Main.logger.error(exception.getMessage());
                            return null;
                        })
                        .join();
                commandToBeRemoved
                        .deleteGlobal()
                        .exceptionally(exception -> {   // Error message for failing to delete command that needed to be removed
                            Main.logger.error("Unable to delete the requested global slash command!");
                            Main.logger.error(exception.getMessage());
                            return null;
                        })
                        .join();
                break;
            }
        }
        if (commandFound){
            Main.logger.info("The requested command was successfully found and deleted");
        }
        else{
            Main.logger.info("The requested command was NOT found");
        }
    }
}
