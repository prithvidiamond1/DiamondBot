package org.prithvidiamond1.GuildCommands.EventListeners.EventImplement;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.prithvidiamond1.GuildCommands.EventListeners.PingEvent;
import org.prithvidiamond1.Main;
import org.springframework.stereotype.Component;

@Component
public class PingEventImplement implements PingEvent {

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getMessageContent().equalsIgnoreCase("!ping")) {
            new MessageBuilder().setEmbed(new EmbedBuilder()
                            .setTitle("Hello " + event.getMessageAuthor().getDisplayName() + "!")
                            .setThumbnail(event.getMessageAuthor().getAvatar())
                            .setColor(Main.botAccentColor))
                    .send(event.getChannel());
        }
    }
}
