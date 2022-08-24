package org.prithvidiamond1;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;
import org.prithvidiamond1.DB.Models.DiscordServer;
import org.prithvidiamond1.DB.Repositories.ServerRepository.ServerRepository;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;

@Component
public class BotApplication {
    private final DiscordApi api;
    private final ServerRepository serverRepository;
    private final Logger logger;

    public BotApplication(ServerRepository serverRepository, Logger logger){
        this.logger = logger;

        String botToken = System.getenv().get("BOT_TOKEN");

        this.api = new DiscordApiBuilder()
                .setToken(botToken)
                .setAllIntents()
                .setWaitForServersOnStartup(true)
                .setWaitForUsersOnStartup(true)
                .login().exceptionally(exception -> {    // Error message for any failed actions from the above
                    this.logger.error("Error setting up DiscordApi instance!");
                    this.logger.error(exception.getMessage());
                    return null;
                })
                .join();

        this.serverRepository = serverRepository;
    }

    @PostConstruct
    public void appRuntime(){
        this.logger.info("Bot has started!");

        // Handling server entries in the database
        if (this.serverRepository.findAll().isEmpty()) {
            this.logger.trace("Bot server data repository empty, initializing data repository...");
            Collection<Server> servers = api.getServers();
            for (Server server : servers) {
                this.serverRepository.save(new DiscordServer(String.valueOf(server.getId()), BotConstants.defaultGuildPrefix));
            }
            this.logger.trace("Bot server data repository initialized");
        }
    }

    @Bean
    public DiscordApi getApi() {
        return this.api;
    }
}
