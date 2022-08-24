package org.prithvidiamond1.Listeners;

import org.prithvidiamond1.DB.Repositories.ServerRepository.ServerRepository;
import org.slf4j.Logger;

public abstract class BaseListener implements Listener {
    private final Logger logger;
    private final ServerRepository serverRepository;

    protected BaseListener(Logger logger, ServerRepository serverRepository) {
        this.logger = logger;
        this.serverRepository = serverRepository;
    }

    public Logger getLogger(){
        return this.logger;
    }

    public ServerRepository getServerRepository(){
        return this.serverRepository;
    }
}
