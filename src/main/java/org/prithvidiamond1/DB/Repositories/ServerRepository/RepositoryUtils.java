package org.prithvidiamond1.DB.Repositories.ServerRepository;

import org.javacord.api.entity.server.Server;
import org.prithvidiamond1.DB.Models.DiscordServer;

public interface RepositoryUtils {
    DiscordServer resolveServerModelById(Server server);
}
