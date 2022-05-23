package org.prithvidiamond1.Commands;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.prithvidiamond1.CommandFunctions;
import org.prithvidiamond1.Main;
import org.testng.internal.collections.Pair;

import java.util.Optional;

public class GayrateCommand implements Command{

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
//                    exception.printStackTrace();
                    return null;
                });
    }

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
//                    exception.printStackTrace();
                            return null;
                        })
        );
    }
}
