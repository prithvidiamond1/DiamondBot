package org.prithvidiamond1.SlashCommands;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.prithvidiamond1.SlashCommands.Customizers.SlashCommandCustomizer;
import org.prithvidiamond1.SlashCommands.RegisteredSlashCommands.*;

/**
 * This class holds all the command registrations and runs them as required
 */
public class SlashCommandRunner {
    /**
     * Method that registers a command to a provided slash command registry and runs the command on the provided Discord API
     * @param api the provided Discord API
     * @param slashCommandRegistry the provided slash command registry
     */
    public static void run(DiscordApi api, SlashCommandHandler slashCommandRegistry){
        slashCommandRegistry.registerCommand("ping",
                "A command that will make the bot greet you!",
                new SlashPingCommand())
                .createGlobal(api)
                .exceptionally(exception -> {
                    exception.printStackTrace();
                    return null;
                })
                .join();

        slashCommandRegistry.registerCommand("gayrate",
                "A command that will make the bot rate how gay you are!",
                new SlashGayrateCommand())
                .createGlobal(api)
                .exceptionally(exception -> {
                    exception.printStackTrace();
                    return null;
                })
                .join();

        slashCommandRegistry.registerCommand("simprate",
                "A command that will make the bot rate how much of a simp you are!",
                new SlashSimprateCommand())
                .createGlobal(api)
                .exceptionally(exception -> {
                    exception.printStackTrace();
                    return null;
                })
                .join();

        SlashCommandBuilder prefixCommand = slashCommandRegistry.registerCommand("prefix",
                "A command to change the guild prefix of the bot",
                new SlashPrefixCommand());
        SlashCommandCustomizer prefixCommandCustomizer = new SlashCommandCustomizer(prefixCommand);
        prefixCommandCustomizer.addCommandOption(SlashCommandOptionType.STRING,
                "prefix-string",
                "A command to change the guild prefix of the bot",
                true);
        prefixCommandCustomizer.setCustomizations()
                .createGlobal(api)
                .exceptionally(exception -> {
                    exception.printStackTrace();
                    return null;
                })
                .join();

        slashCommandRegistry.registerCommand("help",
                "A command that shows all the commands of the bot and their descriptions",
                new SlashHelpCommand(slashCommandRegistry))
                .createGlobal(api)
                .exceptionally(exception -> {
                    exception.printStackTrace();
                    return null;
                })
                .join();

        api.addSlashCommandCreateListener(slashCommandRegistry);
    }
}
