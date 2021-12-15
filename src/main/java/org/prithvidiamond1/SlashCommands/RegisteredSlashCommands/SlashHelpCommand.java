package org.prithvidiamond1.SlashCommands.RegisteredSlashCommands;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.prithvidiamond1.Main;
import org.prithvidiamond1.SlashCommands.SlashCommandHandler;
import org.prithvidiamond1.SlashCommands.SlashCommandInterface;

/**
 * This class contains the actions of the help slash command
 */
public class SlashHelpCommand implements SlashCommandInterface {

    final private SlashCommandHandler slashCommandRegistry;

    /**
     * Constructor of the help slash command
     * @param slashCommandRegistry the registry the command registers to
     */
    public SlashHelpCommand(SlashCommandHandler slashCommandRegistry){
        this.slashCommandRegistry = slashCommandRegistry;
    }

    /**
     * Method that contains and runs the actions of the help slash command
     * @param event the event that called the command
     */
    @Override
    public void runCommand(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        slashCommandInteraction.getServer().ifPresent(server -> slashCommandInteraction.createImmediateResponder()
                .addEmbed(new EmbedBuilder()
                        .setTitle("List of slash commands and their descriptions")
                        .setDescription(slashCommandRegistry.generateHelpDescription())
                        .setThumbnail(Main.botIconURL)
                        .setColor(Main.botAccentColor)
                ).respond()
                .exceptionally(exception -> {   // Error message for failing to respond to the slash command interaction
                    Main.logger.error("Unable to respond to the slash command interaction");
                    Main.logger.error(exception.getMessage());
//                    exception.printStackTrace();
                    return null;
                })
        );
    }
}
