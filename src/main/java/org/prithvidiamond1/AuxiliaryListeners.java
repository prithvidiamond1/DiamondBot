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
            // User mentions are stored as follows in message text: <@!USER_ID>, hence the +4
            if (event.getMessage().getContent().strip().length() == String.valueOf(api.getYourself().getId()).length()+4) {
                if (mentionedUsers.size() == 1) {
                    if (mentionedUsers.get(0).isBot() && mentionedUsers.get(0).isYourself()) {
                        new MessageBuilder().setEmbed(new EmbedBuilder()
                                .setTitle("Hi! My name is Diamond bot!")
                                .setThumbnail(Main.botIconURL)
                                .setDescription(String.format("Type **/help** for a list of slash commands or **%shelp** for a list of guild commands", currentPrefix))
                                .setColor(Main.botAccentColor)).send(event.getChannel());
                    }
                }
            }
        }));
    }
}
