package org.prithvidiamond1;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.prithvidiamond1.Commands.*;
import org.prithvidiamond1.SlashCommands.Customizers.SlashCommandCustomizer;

import java.util.List;

public class CommandRegister {
    public void run(DiscordApi api, CommandHandler commandHandler){
        commandHandler.registerCommand("ping",
                        "A command that will make the bot greet you!",
                        new PingCommand())
                .createGlobal(api)
                .exceptionally(exception -> {   // Error message for failing to register the slash command to the registry
                    Main.logger.error("Unable to register slash command to the registry");
                    Main.logger.error(exception.getMessage());
                    return null;
                })
                .join();

        commandHandler.registerCommand("gayrate",
                        "A command that will make the bot rate how gay you are!",
                        new GayrateCommand())
                .createGlobal(api)
                .exceptionally(exception -> {   // Error message for failing to register the slash command to the registry
                    Main.logger.error("Unable to register slash command to the registry");
                    Main.logger.error(exception.getMessage());
                    return null;
                })
                .join();

        commandHandler.registerCommand("simprate",
                        "A command that will make the bot rate how much of a simp you are!",
                        new SimprateCommand())
                .createGlobal(api)
                .exceptionally(exception -> {   // Error message for failing to register the slash command to the registry
                    Main.logger.error("Unable to register slash command to the registry");
                    Main.logger.error(exception.getMessage());
                    return null;
                })
                .join();

        SlashCommandBuilder prefixCommand = commandHandler.registerCommand("prefix",
                "A command to change the guild prefix of the bot",
                new PrefixCommand());
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

        commandHandler.registerCommand("help",
                        "A command that shows all the commands of the bot and their descriptions",
                        new HelpCommand())
                .createGlobal(api)
                .exceptionally(exception -> {   // Error message for failing to register the slash command to the registry
                    Main.logger.error("Unable to register slash command to the registry");
                    Main.logger.error(exception.getMessage());
                    return null;
                })
                .join();

        SlashCommandBuilder playCommand = commandHandler.registerCommand("play",
                "A command to play music",
                new PlayCommand());
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


        api.addMessageCreateListener(commandHandler);
        api.addSlashCommandCreateListener(commandHandler);
    }
}
