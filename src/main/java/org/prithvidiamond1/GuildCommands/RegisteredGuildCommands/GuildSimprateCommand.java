package org.prithvidiamond1.GuildCommands.RegisteredGuildCommands;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.prithvidiamond1.CommandFunctions;
import org.prithvidiamond1.GuildCommands.GuildCommandInterface;
import org.prithvidiamond1.Main;

public class GuildSimprateCommand implements GuildCommandInterface {
    @Override
    public void runCommand(MessageCreateEvent event){
        int rate = CommandFunctions.randomRate();
        new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setAuthor(event.getMessageAuthor()).setTitle("Simp Calculator")
                        .setDescription(event.getMessageAuthor().getDisplayName()+" is "+rate+"% simp")
                        .setColor(Main.botAccentColor))
                .send(event.getChannel());
    }
}
