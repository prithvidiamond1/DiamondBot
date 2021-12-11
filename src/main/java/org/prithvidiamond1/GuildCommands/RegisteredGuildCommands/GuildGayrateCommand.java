package org.prithvidiamond1.GuildCommands.RegisteredGuildCommands;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.prithvidiamond1.CommandFunctions;
import org.prithvidiamond1.GuildCommands.GuildCommandInterface;
import org.prithvidiamond1.Main;
import org.testng.internal.collections.Pair;

/**
 * This class contains the actions of the gayrate guild command
 */
public class GuildGayrateCommand implements GuildCommandInterface {
    /**
     * Method that contains and runs the actions of the gayrate guild command
     * @param event the event that called the command
     */
    @Override
    public void runCommand(MessageCreateEvent event){
        Pair<String, Integer> gaynessAndRate = CommandFunctions.gayRate();
        String gayness = gaynessAndRate.first();
        int rate = gaynessAndRate.second();
        new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setAuthor(event.getMessageAuthor())
                        .setTitle("Gay Calculator")
                        .setDescription(String.format("%s is **%d%%** gay", event.getMessageAuthor().getDisplayName(), rate))
                        .setThumbnail(gayness)
                        .setColor(Main.botAccentColor))
                .send(event.getChannel());
    }
}
