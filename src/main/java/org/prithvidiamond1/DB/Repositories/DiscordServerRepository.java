package org.prithvidiamond1.DB.Repositories;

import org.prithvidiamond1.DB.Models.DiscordServer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscordServerRepository extends MongoRepository<DiscordServer, String> {
}
