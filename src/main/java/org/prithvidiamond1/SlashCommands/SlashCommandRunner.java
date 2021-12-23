package org.prithvidiamond1.SlashCommands;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.prithvidiamond1.Main;
import org.prithvidiamond1.SlashCommands.Customizers.SlashCommandCustomizer;
import org.prithvidiamond1.SlashCommands.RegisteredSlashCommands.*;

import java.util.List;

/**
 * This class holds all the command registrations and runs them as required
 */
public class SlashCommandRunner {
    public SlashPlayCommand slashPlayCommand = new SlashPlayCommand();
    /**
     * Method that registers a command to a provided slash command registry and runs the command on the provided Discord API
     * @param api the provided Discord API
     * @param slashCommandRegistry the provided slash command registry
     */
    public void run(DiscordApi api, SlashCommandHandler slashCommandRegistry){
        slashCommandRegistry.registerCommand("ping",
                "A command that will make the bot greet you!",
                new SlashPingCommand())
                .createGlobal(api)
                .exceptionally(exception -> {   // Error message for failing to register the slash command to the registry
                    Main.logger.error("Unable to register slash command to the registry");
                    Main.logger.error(exception.getMessage());
                    return null;
                })
                .join();

        slashCommandRegistry.registerCommand("gayrate",
                "A command that will make the bot rate how gay you are!",
                new SlashGayrateCommand())
                .createGlobal(api)
                .exceptionally(exception -> {   // Error message for failing to register the slash command to the registry
                    Main.logger.error("Unable to register slash command to the registry");
                    Main.logger.error(exception.getMessage());
                    return null;
                })
                .join();

        slashCommandRegistry.registerCommand("simprate",
                "A command that will make the bot rate how much of a simp you are!",
                new SlashSimprateCommand())
                .createGlobal(api)
                .exceptionally(exception -> {   // Error message for failing to register the slash command to the registry
                    Main.logger.error("Unable to register slash command to the registry");
                    Main.logger.error(exception.getMessage());
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
                .exceptionally(exception -> {   // Error message for failing to register the customized slash command to the registry
                    Main.logger.error("Unable to register customized slash command to the registry");
                    Main.logger.error(exception.getMessage());
                    return null;
                })
                .join();

        slashCommandRegistry.registerCommand("help",
                "A command that shows all the commands of the bot and their descriptions",
                new SlashHelpCommand(slashCommandRegistry))
                .createGlobal(api)
                .exceptionally(exception -> {   // Error message for failing to register the slash command to the registry
                    Main.logger.error("Unable to register slash command to the registry");
                    Main.logger.error(exception.getMessage());
                    return null;
                })
                .join();

        SlashCommandBuilder playCommand = slashCommandRegistry.registerCommand("play",
                "A command to play music",
                slashPlayCommand);
        SlashCommandCustomizer playCommandCustomizer = new SlashCommandCustomizer(playCommand);
        playCommandCustomizer.addCommandOptionWithChoices(SlashCommandOptionType.STRING,
                "audio-source",
                "Name of the audio source to be searched",
                true,
                List.of(SlashCommandOptionChoice.create("YouTube", "youtube"))
        );
        playCommandCustomizer.addCommandOption(SlashCommandOptionType.STRING,
                "search-string",
                "String to be searched using the set audio source",
                true
        );
        playCommandCustomizer.setCustomizations()
                .createGlobal(api)
                .exceptionally(exception -> {   // Error message for failing to register the customized slash command to the registry
                    Main.logger.error("Unable to register customized slash command to the registry");
                    Main.logger.error(exception.getMessage());
                    return null;
                })
                .join();

        api.addSlashCommandCreateListener(slashCommandRegistry);
    }
}
