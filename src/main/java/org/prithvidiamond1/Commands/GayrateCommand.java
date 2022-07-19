package org.prithvidiamond1.Commands;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.prithvidiamond1.CommandFunctions;
import org.prithvidiamond1.Main;
import org.testng.internal.collections.Pair;

import java.util.List;
import java.util.Optional;

/**
 * This class contains the actions of the gayrate command
 * <br>
 * Calculates how gay a person is using pseudo-RNG
 */
public class GayrateCommand implements Command{
    private final String name = "gayrate";

    private final String description = "A command that will make the bot rate how gay you are!";

    private final List<SlashCommandOption> slashCommandOptions = null;

    /**
     * the guild version of the gayrate command
     * @param event the guild command trigger event
     */
    @Override
    public void runCommand(MessageCreateEvent event) {
        Pair<String, Integer> gaynessAndRate = CommandFunctions.gayRate();
        String gayness = gaynessAndRate.first();
        int rate = gaynessAndRate.second();
        new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setAuthor(event.getMessageAuthor())
                        .setTitle("Gay Calculator")
                        .setDescription(String.format("%s is **%d%%** gay", event.getMessageAuthor().getDisplayName(), rate))
                        .setThumbnail(gayness)
                        .setColor(Main.botAccentColor))
                .send(event.getChannel())
                .exceptionally(exception -> {   // Error message for failing to respond to the guild command
                    Main.logger.error("Unable to respond to the guild command!");
                    Main.logger.error(exception.getMessage());
                    return null;
                });
    }

    /**
     * the slash version of the gayrate command
     * @param event the slash command trigger event
     */
    @Override
    public void runCommand(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        Optional<Server> server = slashCommandInteraction.getServer();
        User user = slashCommandInteraction.getUser();
        Pair<String, Integer> gaynessAndRate = CommandFunctions.gayRate();
        String gayness = gaynessAndRate.first();
        int rate = gaynessAndRate.second();
        server.ifPresent(value -> slashCommandInteraction.createImmediateResponder()
                        .addEmbed(new EmbedBuilder()
                                .setAuthor(user)
                                .setTitle("Gay Calculator")
                                .setDescription(String.format("%s is **%d%%** gay", user.getDisplayName(server.get()), rate))
                                .setThumbnail(gayness)
                                .setColor(Main.botAccentColor))
                        .respond()
                        .exceptionally(exception -> {    // Error message for failing to respond to the slash command interaction
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
