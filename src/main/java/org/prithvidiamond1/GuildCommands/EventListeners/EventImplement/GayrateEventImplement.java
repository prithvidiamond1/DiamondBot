package org.prithvidiamond1.GuildCommands.EventListeners.EventImplement;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.prithvidiamond1.CommandFunctions;
import org.prithvidiamond1.GuildCommands.EventListeners.GayrateEvent;
import org.prithvidiamond1.Main;
import org.springframework.stereotype.Component;
import org.testng.internal.collections.Pair;

@Component
public class GayrateEventImplement implements GayrateEvent {

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if(event.getMessageContent().equalsIgnoreCase("!gayrate")) {
            Pair<String, Integer> gaynessAndRate = CommandFunctions.gayRate();
            String gayness = gaynessAndRate.first();
            int rate = gaynessAndRate.second();
            new MessageBuilder().setEmbed(new EmbedBuilder()
                            .setAuthor(event.getMessageAuthor()).setTitle("Gay Calculator")
                            .setDescription(event.getMessageAuthor().getDisplayName()+" is "+rate+"% gay")
                            .setThumbnail(gayness)
                            .setColor(Main.botAccentColor))
                    .send(event.getChannel());
        }
    }
}
