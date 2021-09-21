package org.prithvidiamond1;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.prithvidiamond1.EventListeners.GayrateEvent;
import org.prithvidiamond1.EventListeners.PingEvent;
import org.prithvidiamond1.EventListeners.SimprateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.awt.*;

@SpringBootApplication
public class Main {

    @Autowired
    private PingEvent pingEvent;
    @Autowired
    private GayrateEvent gayrateEvent;
    @Autowired
    private SimprateEvent simprateEvent;

    public static void main(String[] args) {SpringApplication.run(Main.class,args);}

    public static Color botAccentColor = new Color(60,220,255);

    @Bean
    @ConfigurationProperties(value="discord-api")
    public DiscordApi discordApi() {

        String botToken = System.getenv().get("BOT_TOKEN");

        DiscordApi api = new DiscordApiBuilder().setToken(botToken).setAllNonPrivilegedIntents().login().join();

        System.out.println("Bot has started!");

        // Commands
        api.addMessageCreateListener(pingEvent);
        api.addMessageCreateListener(gayrateEvent);
        api.addMessageCreateListener(simprateEvent);

        return api;
    }
}
