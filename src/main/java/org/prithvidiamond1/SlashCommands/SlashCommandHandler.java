package org.prithvidiamond1.SlashCommands;

import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SlashCommandHandler implements SlashCommandCreateListener {
    final private Map<String, SlashCommandInterface> commands = new ConcurrentHashMap<>();

    public SlashCommandBuilder registerCommand(String name, String description, SlashCommandInterface command){
        commands.put(name, command);
        return SlashCommand.with(name, description);
    }

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event){
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        SlashCommandInterface command = commands.get(slashCommandInteraction.getCommandName().toLowerCase());
        if (command != null){
            command.runCommand(event);
        }
    }
}
