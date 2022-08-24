package org.prithvidiamond1.DB.Repositories.ServerRepository;

import org.javacord.api.entity.server.Server;
import org.prithvidiamond1.DB.Models.DiscordServer;
import org.slf4j.Logger;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class RepositoryUtilsImpl implements RepositoryUtils {
    private final MongoOperations operations;
    private final Logger logger;

    public RepositoryUtilsImpl(MongoOperations operations, Logger logger) {
        this.operations = operations;
        this.logger = logger;
    }

    /**
     * Method that resolves a Javacord Server entity into its corresponding database model
     * @param server the Javacord Server entity object
     * @return returns as DiscordServer database model
     */
    public DiscordServer resolveServerModelById(Server server){
        DiscordServer discordServer = null;

        Query query = new Query(Criteria.where("id").is(String.valueOf(server.getId())));

        if (operations.exists(query, DiscordServer.class)){
            this.logger.trace("Server model present, getting server model...");
            discordServer = operations.findOne(query, DiscordServer.class);
        } else {
            this.logger.trace("Server model not present, returning null...");
        }

        return discordServer;
    }
}
