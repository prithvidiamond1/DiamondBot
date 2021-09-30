package org.prithvidiamond1.GuildCommands.RegisteredGuildCommands;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.prithvidiamond1.CommandFunctions;
import org.prithvidiamond1.GuildCommands.GuildCommandInterface;
import org.prithvidiamond1.Main;
import org.testng.internal.collections.Pair;

public class GuildGayrateCommand implements GuildCommandInterface {
    @Override
    public void runCommand(MessageCreateEvent event){
        Pair<String, Integer> gaynessAndRate = CommandFunctions.gayRate();
        String gayness = gaynessAndRate.first();
        int rate = gaynessAndRate.second();
        new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setAuthor(event.getMessageAuthor()).setTitle("Gay Calculator")
                        .setDescription(event.getMessageAuthor().getDisplayName()+" is "+rate+"% gay")
                        .setThumbnail(gayness)
                        .setColor(Main.botAccentColor))
                .send(event.getChannel());
    }
}
