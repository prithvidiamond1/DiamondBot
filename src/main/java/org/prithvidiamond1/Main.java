package org.prithvidiamond1;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;
import org.prithvidiamond1.DB.Models.DiscordServer;
import org.prithvidiamond1.DB.Repositories.DiscordServerRepository;
import org.prithvidiamond1.GuildCommands.GuildCommandHandler;
import org.prithvidiamond1.GuildCommands.GuildCommandRunner;
import org.prithvidiamond1.HelperHandlers.ServerJoinHandler;
import org.prithvidiamond1.HelperHandlers.ServerLeaveHandler;
import org.prithvidiamond1.SlashCommands.SlashCommandHandler;
import org.prithvidiamond1.SlashCommands.SlashCommandRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.awt.*;
import java.util.Collection;

/**
 * The main class of the Discord bot
 */
@SpringBootApplication
public class Main {
    @Autowired
    private GuildCommandHandler guildCommandRegistry;

    @Autowired
    private SlashCommandHandler slashCommandRegistry;

    @Autowired
    private ServerJoinHandler serverJoinHandler;

    @Autowired
    private ServerLeaveHandler serverLeaveHandler;

    /**
     * The start point for the Discord bot application
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    public static Color botAccentColor = new Color(60, 220, 255);
    public static String defaultGuildPrefix = "!";   // would probably require a database to implement separate guild command prefixes
    public static String botIconURL = "https://i.imgur.com/ERxQB6z.png";

    public static DiscordServerRepository discordServerRepository = null;

    /**
     * Constructor for the Main class
     * @param discordServerRepo the backend MongoDB repository used for storing Discord server preferences
     */
    public Main(DiscordServerRepository discordServerRepo) {
        discordServerRepository = discordServerRepo;
    }

    /**
     * Method that runs the Discord bot instance
     * @return the Discord API instance used by the bot which is then dependency injected using SpringBoot
     */
    @Bean
    @ConfigurationProperties(value = "discord-api")
    public DiscordApi discordApi() {

        String botToken = System.getenv().get("BOT_TOKEN");

        DiscordApi api = new DiscordApiBuilder()
                .setToken(botToken)
                .setAllIntents()
                .setWaitForServersOnStartup(true)
                .setWaitForUsersOnStartup(true)
                .login().exceptionally(exception -> {
                    exception.printStackTrace();    // Error message for any failed actions from the above
                    return null;
                })
                .join();

        System.out.println("Bot has started!");

        // Handling server entries in the database
        if (discordServerRepository.findAll().isEmpty()) {
            Collection<Server> servers = api.getServers();
            for (Server server : servers) {
                discordServerRepository.save(new DiscordServer(String.valueOf(server.getId()), defaultGuildPrefix));
            }
        }

        // Server join and leave handlers
        api.addServerJoinListener(serverJoinHandler);
        api.addServerLeaveListener(serverLeaveHandler);

        // Guild Commands
        GuildCommandRunner.run(api, guildCommandRegistry);

        // Slash Commands
        SlashCommandRunner.run(api, slashCommandRegistry);

        // Self mention listener
        AuxiliaryListeners.selfMentionListener(api);

        return api;
    }
}
