package org.prithvidiamond1.SlashCommands.RegisteredSlashCommands;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.prithvidiamond1.CommandFunctions;
import org.prithvidiamond1.Main;
import org.prithvidiamond1.SlashCommands.SlashCommandInterface;
import org.testng.internal.collections.Pair;

import java.util.Optional;

/**
 * This class contains the actions of the gayrate slash command
 */
public class SlashGayrateCommand implements SlashCommandInterface {
    /**
     * Method that contains and runs the actions of the gayrate slash command
     * @param event the event that called the command
     */
    @Override
    public void runCommand(SlashCommandCreateEvent event){
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
                .respond());
    }
}
