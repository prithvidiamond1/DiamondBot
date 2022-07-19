package org.prithvidiamond1.Listeners;

import org.javacord.api.entity.server.Server;
import org.javacord.api.event.server.ServerLeaveEvent;
import org.javacord.api.listener.server.ServerLeaveListener;
import org.prithvidiamond1.Main;
import org.springframework.stereotype.Component;

import static org.prithvidiamond1.ServerHelperFunctions.resolveServerModelById;

/**
 * This class handles events related to Discord server leave(s) done by the Discord bot
 * <br>
 * It does this by implementing a listener for it
 */
@Component
public class ServerLeaveHandler implements ServerLeaveListener {
    /**
     * Method that dictates what happens when the Discord bot leaves a server
     * @param event the leave event listened by the listener
     */
    @Override
    public void onServerLeave(ServerLeaveEvent event) {
        Server leftServer = event.getServer();
        Main.logger.info(String.format("Bot has left the following server: %s", leftServer.getName()));
        Main.discordServerRepository.delete(resolveServerModelById(leftServer));
    }
}
