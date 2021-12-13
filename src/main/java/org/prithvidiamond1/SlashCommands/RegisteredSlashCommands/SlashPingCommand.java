package org.prithvidiamond1.SlashCommands.RegisteredSlashCommands;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.prithvidiamond1.Main;
import org.prithvidiamond1.SlashCommands.SlashCommandInterface;

import java.util.Optional;

/**
 * This class contains the actions of the ping slash command
 */
public class SlashPingCommand implements SlashCommandInterface {
    /**
     * Method that contains and runs the actions of the ping slash command
     * @param event the event that called the command
     */
    @Override
    public void runCommand(SlashCommandCreateEvent event){
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        Optional<Server> server = slashCommandInteraction.getServer();
        server.ifPresent(value -> slashCommandInteraction.createImmediateResponder()
                .addEmbed(new EmbedBuilder()
                        .setTitle(String.format("Hello %s!", slashCommandInteraction.getUser().getDisplayName(server.get())))
                        .setThumbnail(slashCommandInteraction.getUser().getAvatar())
                        .setColor(Main.botAccentColor))
                .respond()
                .exceptionally(exception -> {
                    exception.printStackTrace();
                    return null;
                })
        );
    }
}
