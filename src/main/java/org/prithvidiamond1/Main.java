package org.prithvidiamond1;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;
import org.prithvidiamond1.DB.Models.DiscordServer;
import org.prithvidiamond1.DB.Repositories.DiscordServerRepository;
import org.prithvidiamond1.HelperHandlers.ServerJoinHandler;
import org.prithvidiamond1.HelperHandlers.ServerLeaveHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    /**
     * The bot's accent color (currently a shade of cyan)
     */
    public static Color botAccentColor = new Color(60, 220, 255);

    /**
     * The default guild prefix for bot guild commands
     */
    public static String defaultGuildPrefix = "!";

    /**
     * String containing a URL to the bot's icon image
     */
    public static String botIconURL = "https://i.imgur.com/ERxQB6z.png";

    /**
     * Supported Audio Sources for voice channel audio playback
     */
    public static String[] audioSources = {
            "youtube",
    };

    /**
     * The YouTube API key
     */
    public static String youtubeApiKey = System.getenv().get("YT_API_KEY");

    /**
     * The main logger object
     */
    public static Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * The backend repository object for storing server preferences
     */
    public static DiscordServerRepository discordServerRepository = null;

    /**
     * Enumerations for the different voice channel connection states
     */
    public enum VoiceConnectionStatus {
        /**
         * State for successful audio connection
         */
        Successful,
        /**
         * State for unsuccessful audio connection
         */
        Unsuccessful,
        /**
         * State for a pre-existing audio connection
         */
        AlreadyConnected
    }

    /**
     * The command handler instance
     */
    @Autowired
    private CommandHandler commandHandler;

    /**
     * The server join handler instance
     */
    @Autowired
    private ServerJoinHandler serverJoinHandler;

    /**
     * The server leave handler instance
     */
    @Autowired
    private ServerLeaveHandler serverLeaveHandler;

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
                .login().exceptionally(exception -> {    // Error message for any failed actions from the above
                    logger.error("Error setting up DiscordApi instance!");
                    logger.error(exception.getMessage());
                    return null;
                })
                .join();

        logger.info("Bot has started!");

        // Handling server entries in the database
        if (discordServerRepository.findAll().isEmpty()) {
            logger.trace("Bot server data repository empty, initializing data repository...");
            Collection<Server> servers = api.getServers();
            for (Server server : servers) {
                discordServerRepository.save(new DiscordServer(String.valueOf(server.getId()), defaultGuildPrefix));
            }
            logger.trace("Bot server data repository initialized");
        }

        // Server join and leave handlers
        api.addServerJoinListener(serverJoinHandler);
        api.addServerLeaveListener(serverLeaveHandler);

        // Commands
        CommandRegister.run(api, commandHandler);

        // Self mention listener
        AuxiliaryListeners.selfMentionListener(api);

        return api;
    }

    /**
     * The start point for the Discord bot application
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
