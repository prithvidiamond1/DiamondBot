package org.prithvidiamond1.GuildCommands.RegisteredGuildCommands;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.prithvidiamond1.GuildCommands.GuildCommandInterface;
import org.prithvidiamond1.Main;

public class GuildPingCommand implements GuildCommandInterface {
    @Override
    public void runCommand(MessageCreateEvent event){
        new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setTitle("Hello " + event.getMessageAuthor().getDisplayName() + "!")
                        .setThumbnail(event.getMessageAuthor().getAvatar())
                        .setColor(Main.botAccentColor))
                .send(event.getChannel());
    }
}
