package org.prithvidiamond1.HelperHandlers;

import org.javacord.api.entity.server.Server;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.listener.server.ServerJoinListener;
import org.prithvidiamond1.DB.Models.DiscordServer;
import org.prithvidiamond1.Main;
import org.springframework.stereotype.Component;

/**
 * This class handles events related to Discord server joins done by the Discord bot
 * <br>
 * It does this by implementing a listener for it
 */
@Component
public class ServerJoinHandler implements ServerJoinListener {
    /**
     * Method that dictates what happens when the Discord bot joins a server
     * @param event the join event listened by the listener
     */
    @Override
    public void onServerJoin(ServerJoinEvent event) {
        Server joinedServer = event.getServer();
        Main.discordServerRepository.save(new DiscordServer(String.valueOf(joinedServer.getId()), Main.defaultGuildPrefix));
    }
}
