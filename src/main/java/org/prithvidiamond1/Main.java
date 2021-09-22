package org.prithvidiamond1;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.interaction.SlashCommand;
import org.prithvidiamond1.GuildCommands.EventListeners.GayrateEvent;
import org.prithvidiamond1.GuildCommands.EventListeners.PingEvent;
import org.prithvidiamond1.GuildCommands.EventListeners.SimprateEvent;
import org.prithvidiamond1.SlashCommands.EventListeners.SlashGayrateEvent;
import org.prithvidiamond1.SlashCommands.EventListeners.SlashPingEvent;
import org.prithvidiamond1.SlashCommands.EventListeners.SlashSimprateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.awt.*;

@SpringBootApplication
public class Main {

    @Autowired
    private PingEvent pingEvent;
    @Autowired
    private GayrateEvent gayrateEvent;
    @Autowired
    private SimprateEvent simprateEvent;

    @Autowired
    private SlashPingEvent slashPingEvent;
    @Autowired
    private SlashGayrateEvent slashGayrateEvent;
    @Autowired
    private SlashSimprateEvent slashSimprateEvent;

    public static void main(String[] args) {SpringApplication.run(Main.class, args);}

    public static Color botAccentColor = new Color(60, 220, 255);

    @Bean
    @ConfigurationProperties(value="discord-api")
    public DiscordApi discordApi() {

        String botToken = System.getenv().get("BOT_TOKEN");

        DiscordApi api = new DiscordApiBuilder().setToken(botToken).setAllNonPrivilegedIntents().login().join();

        System.out.println("Bot has started!");

        // creating a one time instruction to remove the /bing command
        ServerHelperFunctions.removeGlobalSlashCommand(api, "bing");

        // Commands
        api.addMessageCreateListener(pingEvent);
        api.addMessageCreateListener(gayrateEvent);
        api.addMessageCreateListener(simprateEvent);

        // Slash Commands
        SlashCommand.with("ping", "A command that will make the bot greet you!").createGlobal(api).join();
        api.addSlashCommandCreateListener(slashPingEvent);
        SlashCommand.with("gayrate", "A command that will make the bot rate how gay you are!").createGlobal(api).join();
        api.addSlashCommandCreateListener(slashGayrateEvent);
        SlashCommand.with("simprate", "A command that will make the bot rate how much of a simp you are!").createGlobal(api).join();
        api.addSlashCommandCreateListener(slashSimprateEvent);

        return api;
    }
}
