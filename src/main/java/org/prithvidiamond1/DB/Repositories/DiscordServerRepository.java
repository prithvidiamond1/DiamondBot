package org.prithvidiamond1.DB.Repositories;

import org.prithvidiamond1.DB.Models.DiscordServer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface that inherits the MongoRepository template interface
 * <br>
 * Can be used to create a Discord server repository that uses MongoDB backends such as Atlas to store each Discord server's preferences
 */
@Repository
public interface DiscordServerRepository extends MongoRepository<DiscordServer, String> {
}
