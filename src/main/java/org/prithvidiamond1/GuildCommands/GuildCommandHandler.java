package org.prithvidiamond1.GuildCommands;

import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.prithvidiamond1.DB.Models.DiscordServer;
import org.prithvidiamond1.Main;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GuildCommandHandler implements MessageCreateListener {
    final private Map<String, GuildCommandInterface> commands = new ConcurrentHashMap<>();

    public void registerCommand(String name, GuildCommandInterface command){
        commands.put(name, command);
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String message = event.getMessageContent();
        Optional<Server> server = event.getServer();
        server.ifPresent(value -> {
            Optional<DiscordServer> serverModel = Main.discordServerRepository.findById(String.valueOf(value.getId()));
            String prefix = serverModel.map(DiscordServer::getGuildPrefix).orElseGet(() -> Main.defaultGuildPrefix);

            if (message.startsWith(prefix)) {
                String prefixStrippedMessage = message.substring(prefix.length());
                GuildCommandInterface command = commands.get(prefixStrippedMessage.toLowerCase());
                if (command != null) {
                    command.runCommand(event);
                }
            }
        });
    }
}
