package org.prithvidiamond1.Listeners;

import org.javacord.api.entity.server.Server;
import org.javacord.api.event.server.ServerLeaveEvent;
import org.javacord.api.listener.server.ServerLeaveListener;
import org.prithvidiamond1.DB.Repositories.ServerRepository.ServerRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/**
 * This class handles events related to Discord server leave(s) done by the Discord bot
 * <br>
 * It does this by implementing a listener for it
 */
@Component
public class ServerLeaveListenerImpl extends BaseListener implements ServerLeaveListener {
    public ServerLeaveListenerImpl(Logger logger, ServerRepository serverRepository) {
        super(logger, serverRepository);
    }

    /**
     * Method that dictates what happens when the Discord bot leaves a server
     * @param event the leave event listened by the listener
     */
    @Override
    public void onServerLeave(ServerLeaveEvent event) {
        Server leftServer = event.getServer();
        getLogger().info(String.format("Bot has left the following server: %s", leftServer.getName()));
        getServerRepository().delete(getServerRepository().resolveServerModelById(leftServer));
    }
}
