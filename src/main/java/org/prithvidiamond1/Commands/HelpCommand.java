package org.prithvidiamond1.Commands;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.prithvidiamond1.CommandHandler;
import org.prithvidiamond1.Main;

import static org.prithvidiamond1.ServerHelperFunctions.resolveServerModelById;

public class HelpCommand implements Command{
    @Override
    public void runCommand(MessageCreateEvent event) {
        event.getServer().ifPresent(server -> {
            String commandPrefix = resolveServerModelById(server).getGuildPrefix();
            new MessageBuilder().setEmbed(new EmbedBuilder()
                            .setTitle("List of guild commands and their descriptions")
                            .setDescription(CommandHandler.generateHelpDescription(commandPrefix))
                            .setThumbnail(Main.botIconURL)
                            .setColor(Main.botAccentColor))
                    .send(event.getChannel())
                    .exceptionally(exception -> {   // Error message for failing to respond to the guild command
                        Main.logger.error("Unable to respond to the guild command!");
                        Main.logger.error(exception.getMessage());
//                      exception.printStackTrace();
                        return null;
                    });
        });
    }

    @Override
    public void runCommand(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        slashCommandInteraction.getServer().ifPresent(server -> slashCommandInteraction.createImmediateResponder()
                        .addEmbed(new EmbedBuilder()
                                .setTitle("List of slash commands and their descriptions")
                                .setDescription(CommandHandler.generateHelpDescription("")) // Slash commands don't have command prefixes
                                .setThumbnail(Main.botIconURL)
                                .setColor(Main.botAccentColor)
                        ).respond()
                        .exceptionally(exception -> {   // Error message for failing to respond to the slash command interaction
                            Main.logger.error("Unable to respond to the slash command interaction");
                            Main.logger.error(exception.getMessage());
//                          exception.printStackTrace();
                            return null;
                        })
        );
    }
}
