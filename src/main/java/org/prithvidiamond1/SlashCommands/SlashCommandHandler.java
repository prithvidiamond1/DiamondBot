package org.prithvidiamond1.SlashCommands;

import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class handles the registering and processing of slash commands as well as a few other smaller functions
 */
@Component
public class SlashCommandHandler implements SlashCommandCreateListener {
    private final Map<String, SlashCommandInterface> commands = new ConcurrentHashMap<>();
    private final Map<String, String> commandDescriptions = new ConcurrentHashMap<>();

    /**
     * Method to register a slash command
     * @param name the command's name
     * @param description the command's description
     * @param command the command itself
     * @return returns a slash command builder object that can be used to customize the command further using an appropriate customizer
     */
    public SlashCommandBuilder registerCommand(String name,
                                               String description,
                                               SlashCommandInterface command){
        this.commands.put(name, command);
        this.commandDescriptions.put(name, description);
        return SlashCommand.with(name, description);
    }

    /**
     * Method that dictates how the command must be processed once the listener has heard a command
     * @param event the listened event
     */
    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event){
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        SlashCommandInterface command = commands.get(slashCommandInteraction.getCommandName().toLowerCase());
        if (command != null){
            command.runCommand(event);
        }
    }

    /**
     * Method that generates a brief description of all the registered slash commands
     * @return a brief description of all the registered slash commands
     */
    public String generateHelpDescription(){
        StringBuilder helpDescription = new StringBuilder();
        for (String commandName: this.commandDescriptions.keySet()){
            helpDescription.append(String.format("**/%s** - %s%n", commandName, this.commandDescriptions.get(commandName)));
        }
        return helpDescription.toString();
    }

}
