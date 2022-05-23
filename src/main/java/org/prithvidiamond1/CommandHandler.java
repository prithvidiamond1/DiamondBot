package org.prithvidiamond1;

import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.javacord.api.listener.message.MessageCreateListener;
import org.prithvidiamond1.Commands.Command;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.prithvidiamond1.ServerHelperFunctions.resolveServerModelById;

@Component
public class CommandHandler implements MessageCreateListener, SlashCommandCreateListener {
    private final Map<String, Command> commands = new ConcurrentHashMap<>();
    private static final Map<String, String> commandDescriptions = new ConcurrentHashMap<>();

    public SlashCommandBuilder registerCommand(String name,
                                               String description,
                                               Command command){
        this.commands.put(name, command);
        commandDescriptions.put(name, description);
        return SlashCommand.with(name, description);
    }


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

    public static String generateHelpDescription(String commandPrefix){
        StringBuilder helpDescription = new StringBuilder();
        for (String commandName: commandDescriptions.keySet()){
            helpDescription.append(String.format("**%s%s** - %s%n", commandPrefix, commandName, commandDescriptions.get(commandName)));
        }
        return helpDescription.toString();
    }
}
