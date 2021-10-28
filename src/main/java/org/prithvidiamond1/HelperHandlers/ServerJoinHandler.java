package org.prithvidiamond1.HelperHandlers;

import org.javacord.api.entity.server.Server;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.listener.server.ServerJoinListener;
import org.prithvidiamond1.DB.Models.DiscordServer;
import org.prithvidiamond1.Main;
import org.springframework.stereotype.Component;

@Component
public class ServerJoinHandler implements ServerJoinListener {

    @Override
    public void onServerJoin(ServerJoinEvent event) {
        Server joinedServer = event.getServer();
        Main.discordServerRepository.save(new DiscordServer(String.valueOf(joinedServer.getId()), Main.defaultGuildPrefix));
    }
}
