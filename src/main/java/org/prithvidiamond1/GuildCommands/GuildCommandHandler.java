package org.prithvidiamond1.GuildCommands;

import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.prithvidiamond1.ServerHelperFunctions.resolveServerModelById;

/**
 * This class handles the registering and processing of guild commands as well as a few other smaller functions
 */
@Component
public class GuildCommandHandler implements MessageCreateListener {
    final private Map<String, GuildCommandInterface> commands = new ConcurrentHashMap<>();
    final private Map<String, String> commandDescriptions = new ConcurrentHashMap<>();

    /**
     * Method to register a guild command
     * @param name the command's name
     * @param description the command's description
     * @param command the command itself
     */
    public void registerCommand(String name, String description, GuildCommandInterface command){
        this.commands.put(name, command);
        this.commandDescriptions.put(name, description);
    }

    /**
     * Method that dictates how the command must be processed once the listener has heard a command
     * @param event the listened event
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
                GuildCommandInterface command = commands.get(firstWord.toLowerCase());
                if (command != null) {
                    command.runCommand(event);
                }
            }
        });
    }

    /**
     * Method that generates a brief description of all the registered guild commands
     * @param commandPrefix the current guild prefix of the Discord server
     * @return a brief description of all the registered guild commands
     */
    public String generateHelpDescription(String commandPrefix){
        StringBuilder helpDescription = new StringBuilder();
        for (String commandName: this.commandDescriptions.keySet()){
            helpDescription.append(String.format("**%s%s** - %s%n", commandPrefix, commandName, this.commandDescriptions.get(commandName)));
        }
        return helpDescription.toString();
    }
}
