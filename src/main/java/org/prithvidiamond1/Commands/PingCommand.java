package org.prithvidiamond1.Commands;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.prithvidiamond1.BotConstants;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * This class contains the actions of the ping command
 * <br>
 * Pings the bot for a greeting!
 */
@Component
public class PingCommand extends BaseCommand{

    private final String name = "ping";

    private final String description = "A command that will make the bot greet you!";

    private final List<SlashCommandOption> slashCommandOptions = null;

    public PingCommand(Logger logger) {
        super(logger);
    }

    /**
     * the guild version of the ping command
     * @param event the guild command trigger event
     */
    @Override
    public void runCommand(MessageCreateEvent event) {
        new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setTitle(String.format("Hello %s!", event.getMessageAuthor().getDisplayName()))
                        .setThumbnail(event.getMessageAuthor().getAvatar())
                        .setColor(BotConstants.botAccentColor))
                .send(event.getChannel())
                .exceptionally(exception -> {   // Error message for failing to respond to the guild command
                    getLogger().error("Unable to respond to the guild command!");
                    getLogger().error(exception.getMessage());
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
                        .setColor(BotConstants.botAccentColor))
                .respond()
                .exceptionally(exception -> {   // Error message for failing to respond to the slash command interaction
                    getLogger().error("Unable to respond to the slash command interaction");
                    getLogger().error(exception.getMessage());
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
