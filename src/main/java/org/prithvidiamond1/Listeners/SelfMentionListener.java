package org.prithvidiamond1.Listeners;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.prithvidiamond1.BotConstants;
import org.prithvidiamond1.DB.Repositories.ServerRepository.ServerRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SelfMentionListener extends BaseListener implements MessageCreateListener {
    public SelfMentionListener(Logger logger, ServerRepository serverRepository) {
        super(logger, serverRepository);
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        event.getServer().ifPresent(server -> {
            String currentPrefix = getServerRepository().resolveServerModelById(server).getGuildPrefix();
            List<User> mentionedUsers = event.getMessage().getMentionedUsers();
            if (mentionedUsers.size() == 1) {
                if (event.getMessageContent().startsWith("<@") && event.getMessageContent().endsWith(String.format("%s>", event.getApi().getYourself().getId()))) {
                    if (mentionedUsers.get(0).isBot() && mentionedUsers.get(0).isYourself()) {
                        getLogger().info("Bot mention (standalone) - Request for greeting received");
                        new MessageBuilder().setEmbed(new EmbedBuilder()
                                        .setTitle("Hi! My name is Diamond bot!")
                                        .setThumbnail(BotConstants.botIconURL)
                                        .setDescription(String.format("Type **/help** for a list of slash commands or **%shelp** for a list of guild commands", currentPrefix))
                                        .setColor(BotConstants.botAccentColor))
                                .send(event.getChannel())
                                .exceptionally(exception -> {   // Error message for failing to respond to the self-mention
                                    getLogger().error("Failed to send greeting!");
                                    getLogger().error(exception.getMessage());
                                    return null;
                                });
                    }
                }
            }
        });
    }
}
