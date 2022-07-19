package org.prithvidiamond1;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;
import org.prithvidiamond1.DB.Models.DiscordServer;
import org.prithvidiamond1.DB.Repositories.DiscordServerRepository;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class BotApplication {
    private final DiscordApi api;
    private final DiscordServerRepository serverRepository;

    public BotApplication(DiscordServerRepository serverRepository, Logger logger){
        String botToken = System.getenv().get("BOT_TOKEN");

        this.api = new DiscordApiBuilder()
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

        this.serverRepository = serverRepository;

        appRuntime(logger);
    }

    public void appRuntime(Logger logger){
        logger.info("Bot has started!");

        // Handling server entries in the database
        if (this.serverRepository.findAll().isEmpty()) {
            logger.trace("Bot server data repository empty, initializing data repository...");
            Collection<Server> servers = api.getServers();
            for (Server server : servers) {
                this.serverRepository.save(new DiscordServer(String.valueOf(server.getId()), Main.defaultGuildPrefix));
            }
            logger.trace("Bot server data repository initialized");
        }

//        // Server join and leave handlers
//        api.addServerJoinListener(serverJoinHandler);
//        api.addServerLeaveListener(serverLeaveHandler);
//
//        // Commands
//        CommandRegister.run(api, commandHandler);
//
//        // Self mention listener
//        AuxiliaryListeners.selfMentionListener(api);

    }

    @Bean
    public DiscordApi getApi() {
        return this.api;
    }
}
