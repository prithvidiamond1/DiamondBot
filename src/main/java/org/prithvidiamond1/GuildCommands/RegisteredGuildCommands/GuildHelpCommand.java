package org.prithvidiamond1.GuildCommands.RegisteredGuildCommands;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.prithvidiamond1.GuildCommands.GuildCommandHandler;
import org.prithvidiamond1.GuildCommands.GuildCommandInterface;
import org.prithvidiamond1.Main;

import static org.prithvidiamond1.ServerHelperFunctions.resolveServerModelById;

/**
 * This class contains the actions of the help guild command
 */
public class GuildHelpCommand implements GuildCommandInterface {

    final private GuildCommandHandler guildCommandRegistry;

    /**
     * Constructor of the help guild command
     * @param guildCommandRegistry the registry that the command registers to
     */
    public GuildHelpCommand(GuildCommandHandler guildCommandRegistry){
        this.guildCommandRegistry = guildCommandRegistry;
    }

    /**
     * Method that contains and runs the actions of the help guild command
     * @param event the event that called the command
     */
    @Override
    public void runCommand(MessageCreateEvent event) {
        event.getServer().ifPresent(server -> {
            String commandPrefix = resolveServerModelById(server).getGuildPrefix();
            new MessageBuilder().setEmbed(new EmbedBuilder()
                    .setTitle("List of guild commands and their descriptions")
                    .setDescription(guildCommandRegistry.generateHelpDescription(commandPrefix))
                    .setThumbnail(Main.botIconURL)
                    .setColor(Main.botAccentColor)).send(event.getChannel());
        });
    }

}
