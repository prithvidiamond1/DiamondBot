package org.prithvidiamond1.Listeners;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.javacord.api.listener.message.MessageCreateListener;
import org.prithvidiamond1.Commands.Command;
import org.prithvidiamond1.DB.Repositories.ServerRepository.ServerRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class that contains methods and fields for handling command calls
 */
@Component
public class CommandListener extends BaseListener implements MessageCreateListener, SlashCommandCreateListener {
    private final Map<String, Command> commands = new ConcurrentHashMap<>();
    private static final Map<String, String> commandDescriptions = new ConcurrentHashMap<>();

    public CommandListener(Logger logger,
                           List<Command> commands,
                           DiscordApi api,
                           ServerRepository serverRepository){
        super(logger, serverRepository);
        
        for (var command: commands){
            registerCommand(command, api);
        }
    }

    /**
     * Method to register a command
     *
     * @param command the command's instance
     * @param api the Discord API instance
     */
    private void registerCommand(Command command, DiscordApi api){
        this.commands.put(command.getName(), command);
        commandDescriptions.put(command.getName(), command.getDescription());
        SlashCommandBuilder slashCommandBuilder = SlashCommand.with(command.getName(), command.getDescription());
        if (command.getSlashCommandOptions() != null) {
            slashCommandBuilder.setOptions(command.getSlashCommandOptions());
        }

        slashCommandBuilder.createGlobal(api).exceptionally(exception -> {   // Error message for failing to create the slash command globally
                    getLogger().error("Unable to register slash command to the registry");
                    getLogger().error(exception.getMessage());
                    return null;
                })
                .join();
    }

    /**
     * Method that runs a command as a slash command upon receiving its call
     * @param event the slash command trigger event
     */
    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        String commandName = slashCommandInteraction.getCommandName();
        Command command = commands.get(commandName.toLowerCase());
        if (command != null){
            getLogger().info(String.format("Received slash command - '%s'", commandName.toLowerCase()));
            command.runCommand(event);
        }
    }

    /**
     * Method that runs a command as a guild command upon receiving its call
     * @param event the guild command trigger event
     */
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String message = event.getMessageContent();
        Optional<Server> server = event.getServer();
        server.ifPresent(value -> {
            String prefix = getServerRepository().resolveServerModelById(value).getGuildPrefix();
            if (message.startsWith(prefix)) {
                String prefixStrippedMessage = message.substring(prefix.length());
                String firstWord = prefixStrippedMessage.split(" ")[0];
                Command command = commands.get(firstWord.toLowerCase());
                if (command != null) {
                    getLogger().info(String.format("Received guild command - '%s'", firstWord.toLowerCase()));
                    command.runCommand(event);
                }
            }
        });
    }

    /**
     * Method that generates a help command description based on the descriptions given to each command
     * @param commandPrefix the guild prefix for the commands
     * @return a string that can be used as a help command embed description
     */
    public static String generateHelpDescription(String commandPrefix){
        StringBuilder helpDescription = new StringBuilder();
        for (String commandName: commandDescriptions.keySet()){
            helpDescription.append(String.format("**%s%s** - %s%n", commandPrefix, commandName, commandDescriptions.get(commandName)));
        }
        helpDescription.append(String.format("To learn how to use a command that takes arguments, run the command with no arguments as follows: **%s<command name>**", commandPrefix));
        return helpDescription.toString();
    }

    /**
     * Method to remove a global slash command from the bot
     * @param api Discord bot api object
     * @param commandName String of the command to be deleted
     */
    public void removeGlobalSlashCommand(DiscordApi api, String commandName){
        getLogger().info(String.format("Request to remove a global slash command by the name '%s' received", commandName));
        boolean commandFound = false;
        List<SlashCommand> slashCommands = api
                .getGlobalSlashCommands()
                .exceptionally(exception -> {   // Error message for failing to get the list of global slash commands
                    getLogger().error("Unable to retrieve list of global slash commands!");
                    getLogger().error(exception.getMessage());
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
                            getLogger().error("Unable to retrieve global slash command by id!");
                            getLogger().error(exception.getMessage());
                            return null;
                        })
                        .join();
                commandToBeRemoved
                        .deleteGlobal()
                        .exceptionally(exception -> {   // Error message for failing to delete command that needed to be removed
                            getLogger().error("Unable to delete the requested global slash command!");
                            getLogger().error(exception.getMessage());
                            return null;
                        })
                        .join();
                break;
            }
        }
        if (commandFound){
            getLogger().info("The requested command was successfully found and deleted");
        }
        else{
            getLogger().info("The requested command was NOT found");
        }
    }
}
