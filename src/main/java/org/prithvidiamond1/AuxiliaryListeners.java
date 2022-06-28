package org.prithvidiamond1;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.util.List;

import static org.prithvidiamond1.ServerHelperFunctions.resolveServerModelById;

/**
 * This class contains event listeners apart from the main event listeners
 */
public class AuxiliaryListeners {
    /**
     * Method that creates a listener for standalone self-mentions
     * @param api the Discord API instance used by the Discord bot
     */
    public static void selfMentionListener(DiscordApi api){
        api.addMessageCreateListener(event -> event.getServer().ifPresent(server -> {
            String currentPrefix = resolveServerModelById(server).getGuildPrefix();
            List<User> mentionedUsers = event.getMessage().getMentionedUsers();
            if (mentionedUsers.size() == 1) {
                if (event.getMessageContent().startsWith("<@") && event.getMessageContent().endsWith(String.format("%s>", api.getYourself().getId()))) {
                    if (mentionedUsers.get(0).isBot() && mentionedUsers.get(0).isYourself()) {
                        Main.logger.info("Bot mention (standalone) - Request for greeting received");
                        new MessageBuilder().setEmbed(new EmbedBuilder()
                                .setTitle("Hi! My name is Diamond bot!")
                                .setThumbnail(Main.botIconURL)
                                .setDescription(String.format("Type **/help** for a list of slash commands or **%shelp** for a list of guild commands", currentPrefix))
                                .setColor(Main.botAccentColor))
                                .send(event.getChannel())
                                .exceptionally(exception -> {   // Error message for failing to respond to the self-mention
                                    Main.logger.error("Failed to send greeting!");
                                    Main.logger.error(exception.getMessage());
                                    return null;
                                });
                    }
                }
            }
        }));
    }
}
