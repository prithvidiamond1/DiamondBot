package org.prithvidiamond1.HelperHandlers;

import org.javacord.api.entity.server.Server;
import org.javacord.api.event.server.ServerLeaveEvent;
import org.javacord.api.listener.server.ServerLeaveListener;
import org.prithvidiamond1.DB.Models.DiscordServer;
import org.prithvidiamond1.Main;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ServerLeaveHandler implements ServerLeaveListener {

    @Override
    public void onServerLeave(ServerLeaveEvent event) {
        Server leftServer = event.getServer();
        Optional<DiscordServer> deletableServerModel = Main.discordServerRepository.findById(String.valueOf(leftServer.getId()));
        deletableServerModel.ifPresent(value-> Main.discordServerRepository.delete(value));
    }
}
