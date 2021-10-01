package org.prithvidiamond1;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;
import org.prithvidiamond1.DB.Models.DiscordServer;
import org.prithvidiamond1.DB.Repositories.DiscordServerRepository;
import org.prithvidiamond1.GuildCommands.GuildCommandHandler;
import org.prithvidiamond1.GuildCommands.RegisteredGuildCommands.GuildGayrateCommand;
import org.prithvidiamond1.GuildCommands.RegisteredGuildCommands.GuildPingCommand;
import org.prithvidiamond1.GuildCommands.RegisteredGuildCommands.GuildSimprateCommand;
import org.prithvidiamond1.SlashCommands.RegisteredSlashCommands.SlashGayrateCommand;
import org.prithvidiamond1.SlashCommands.RegisteredSlashCommands.SlashPingCommand;
import org.prithvidiamond1.SlashCommands.RegisteredSlashCommands.SlashSimprateCommand;
import org.prithvidiamond1.SlashCommands.SlashCommandHandler;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.awt.*;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class Main implements InitializingBean {
    @Autowired
    private GuildCommandHandler guildCommandRegistry;

    @Autowired
    private SlashCommandHandler slashCommandRegistry;

    public static void main(String[] args) {SpringApplication.run(Main.class, args);}

    public static Logger logger = Logger.getAnonymousLogger();

    public static Color botAccentColor = new Color(60, 220, 255);
    public static String defaultGuildPrefix = "!";   // would probably require a database to implement separate guild command prefixes

    public static DiscordServerRepository discordServerRepository;

    @Autowired
    public Main(DiscordServerRepository discordServerRepository){
        Main.discordServerRepository = discordServerRepository;
    }

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Override
    public void afterPropertiesSet() throws BeanCreationException {
        logger.info("Mongo URI: {}"+mongoUri);
    }

    @Bean
    @ConfigurationProperties(value="discord-api")
    public DiscordApi discordApi() {

        String botToken = System.getenv().get("BOT_TOKEN");

        DiscordApi api = new DiscordApiBuilder().setToken(botToken).setAllNonPrivilegedIntents().login().join();

        System.out.println("Bot has started!");

        if (discordServerRepository.findAll().isEmpty()){
            Collection<Server> servers = api.getServers();
            for (Server server: servers){
                discordServerRepository.save(new DiscordServer(String.valueOf(server.getId()), defaultGuildPrefix));
            }
        }

        // Guild Commands
        guildCommandRegistry.registerCommand("ping", new GuildPingCommand());
        guildCommandRegistry.registerCommand("gayrate", new GuildGayrateCommand());
        guildCommandRegistry.registerCommand("simprate", new GuildSimprateCommand());
        api.addMessageCreateListener(guildCommandRegistry);

        // Slash Commands
        slashCommandRegistry.registerCommand("ping",
                "A command that will make the bot greet you!",
                new SlashPingCommand()).createGlobal(api).join();
        slashCommandRegistry.registerCommand("gayrate",
                "A command that will make the bot rate how gay you are!",
                new SlashGayrateCommand()).createGlobal(api).join();
        slashCommandRegistry.registerCommand("simprate",
                "A command that will make the bot rate how much of a simp you are!",
                new SlashSimprateCommand()).createGlobal(api).join();
        api.addSlashCommandCreateListener(slashCommandRegistry);

        return api;
    }
}
