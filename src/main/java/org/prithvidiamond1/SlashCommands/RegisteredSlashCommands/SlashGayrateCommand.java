package org.prithvidiamond1.SlashCommands.RegisteredSlashCommands;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.prithvidiamond1.CommandFunctions;
import org.prithvidiamond1.SlashCommands.SlashCommandInterface;
import org.testng.internal.collections.Pair;

import java.util.Optional;

import static org.prithvidiamond1.Main.botAccentColor;

public class SlashGayrateCommand implements SlashCommandInterface {
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
                        .setAuthor(user).setTitle("Gay Calculator")
                        .setDescription(user.getDisplayName(server.get())+" is "+rate+"% gay")
                        .setThumbnail(gayness)
                        .setColor(botAccentColor))
                .respond());
    }
}
