package org.prithvidiamond1.Listeners;

import org.prithvidiamond1.DB.Repositories.ServerRepository.ServerRepository;
import org.slf4j.Logger;

public interface Listener {
    Logger getLogger();

    ServerRepository getServerRepository();
}
