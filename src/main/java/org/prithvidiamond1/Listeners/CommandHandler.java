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
import org.prithvidiamond1.Main;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.prithvidiamond1.ServerHelperFunctions.resolveServerModelById;

/**
 * Class that contains methods and fields for handling command calls
 */
@Component
public class CommandHandler implements MessageCreateListener, SlashCommandCreateListener {
    private final Map<String, Command> commands = new ConcurrentHashMap<>();
    private static final Map<String, String> commandDescriptions = new ConcurrentHashMap<>();

    public CommandHandler(List<Command> commands, DiscordApi api){
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
                    Main.logger.error("Unable to register slash command to the registry");
                    Main.logger.error(exception.getMessage());
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
            Main.logger.info(String.format("Received slash command - '%s'", commandName.toLowerCase()));
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
            String prefix = resolveServerModelById(value).getGuildPrefix();
            if (message.startsWith(prefix)) {
                String prefixStrippedMessage = message.substring(prefix.length());
                String firstWord = prefixStrippedMessage.split(" ")[0];
                Command command = commands.get(firstWord.toLowerCase());
                if (command != null) {
                    Main.logger.info(String.format("Received guild command - '%s'", firstWord.toLowerCase()));
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
}
