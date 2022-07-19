package org.prithvidiamond1.Commands;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.prithvidiamond1.Main;

import java.util.List;
import java.util.Optional;

/**
 * This class contains the actions of the ping command
 * <br>
 * Pings the bot for a greeting!
 */
public class PingCommand implements Command{

    private final String name = "ping";

    private final String description = "A command that will make the bot greet you!";

    private final List<SlashCommandOption> slashCommandOptions = null;

    /**
     * the guild version of the ping command
     * @param event the guild command trigger event
     */
    @Override
    public void runCommand(MessageCreateEvent event) {
        new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setTitle(String.format("Hello %s!", event.getMessageAuthor().getDisplayName()))
                        .setThumbnail(event.getMessageAuthor().getAvatar())
                        .setColor(Main.botAccentColor))
                .send(event.getChannel())
                .exceptionally(exception -> {   // Error message for failing to respond to the guild command
                    Main.logger.error("Unable to respond to the guild command!");
                    Main.logger.error(exception.getMessage());
                    return null;
                });
    }

    /**
     * the slash version of the ping command
     * @param event the slash command trigger event
     */
    @Override
    public void runCommand(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        Optional<Server> server = slashCommandInteraction.getServer();
        server.ifPresent(value -> slashCommandInteraction.createImmediateResponder()
                .addEmbed(new EmbedBuilder()
                        .setTitle(String.format("Hello %s!", slashCommandInteraction.getUser().getDisplayName(server.get())))
                        .setThumbnail(slashCommandInteraction.getUser().getAvatar())
                        .setColor(Main.botAccentColor))
                .respond()
                .exceptionally(exception -> {   // Error message for failing to respond to the slash command interaction
                    Main.logger.error("Unable to respond to the slash command interaction");
                    Main.logger.error(exception.getMessage());
                    return null;
                })
        );
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public List<SlashCommandOption> getSlashCommandOptions() {
        return this.slashCommandOptions;
    }
}
