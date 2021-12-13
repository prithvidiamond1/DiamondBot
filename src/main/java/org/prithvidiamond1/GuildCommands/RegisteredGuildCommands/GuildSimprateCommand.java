package org.prithvidiamond1.GuildCommands.RegisteredGuildCommands;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.prithvidiamond1.CommandFunctions;
import org.prithvidiamond1.GuildCommands.GuildCommandInterface;
import org.prithvidiamond1.Main;

/**
 * This class contains the actions of the simprate guild command
 */
public class GuildSimprateCommand implements GuildCommandInterface {
    /**
     * Method that contains and runs the actions of the simprate guild command
     * @param event the event that called the command
     */
    @Override
    public void runCommand(MessageCreateEvent event){
        int rate = CommandFunctions.randomRate();
        new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setAuthor(event.getMessageAuthor())
                        .setTitle("Simp Calculator")
                        .setDescription(String.format("%s is **%d%%** simp", event.getMessageAuthor().getDisplayName(), rate))
                        .setColor(Main.botAccentColor))
                .send(event.getChannel())
                .exceptionally(exception -> {
                    exception.printStackTrace();
                    return null;
                });
    }
}
