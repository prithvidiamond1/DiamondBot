package org.prithvidiamond1.Listeners;

import org.javacord.api.entity.server.Server;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.listener.server.ServerJoinListener;
import org.prithvidiamond1.BotConstants;
import org.prithvidiamond1.DB.Models.DiscordServer;
import org.prithvidiamond1.DB.Repositories.ServerRepository.ServerRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/**
 * This class handles events related to Discord server joins done by the Discord bot
 * <br>
 * It does this by implementing a listener for it
 */
@Component
public class ServerJoinListenerImpl extends BaseListener implements ServerJoinListener {
    public ServerJoinListenerImpl(Logger logger, ServerRepository serverRepository) {
        super(logger, serverRepository);
    }

    /**
     * Method that dictates what happens when the Discord bot joins a server
     * @param event the join event listened by the listener
     */
    @Override
    public void onServerJoin(ServerJoinEvent event) {
        Server joinedServer = event.getServer();
        getLogger().info(String.format("Bot has joined the following server: %s", joinedServer.getName()));
        getServerRepository().save(new DiscordServer(String.valueOf(joinedServer.getId()), BotConstants.defaultGuildPrefix));
    }
}
