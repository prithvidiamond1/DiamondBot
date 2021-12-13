package org.prithvidiamond1.GuildCommands;

import org.javacord.api.DiscordApi;
import org.prithvidiamond1.GuildCommands.RegisteredGuildCommands.*;

/**
 * This class holds all the command registrations and runs them as required
 */
public class GuildCommandRunner {
    /**
     * Method that registers a command to a provided guild command registry and runs the command on the provided Discord API
     * @param api the provided Discord API
     * @param guildCommandRegistry the provided guild command registry
     */
    public static void run(DiscordApi api, GuildCommandHandler guildCommandRegistry){
        guildCommandRegistry.registerCommand("ping",
                "A command that will make the bot greet you!",
                new GuildPingCommand());

        guildCommandRegistry.registerCommand("gayrate",
                "A command that will make the bot rate how gay you are!",
                new GuildGayrateCommand());

        guildCommandRegistry.registerCommand("simprate",
                "A command that will make the bot rate how much of a simp you are!",
                new GuildSimprateCommand());

        guildCommandRegistry.registerCommand("prefix",
                "A command to change the guild prefix of the bot",
                new GuildPrefixCommand());
        
        guildCommandRegistry.registerCommand("help",
                "A command that shows all the commands of the bot and their descriptions",
                new GuildHelpCommand(guildCommandRegistry));

        api.addMessageCreateListener(guildCommandRegistry);
    }
}
