package org.prithvidiamond1.GuildCommands.EventListeners.EventImplement;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.prithvidiamond1.CommandFunctions;
import org.prithvidiamond1.GuildCommands.EventListeners.SimprateEvent;
import org.prithvidiamond1.Main;
import org.springframework.stereotype.Component;

@Component
public class SimprateEventImplement implements SimprateEvent {

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getMessageContent().equalsIgnoreCase("!simprate")) {
            int rate = CommandFunctions.randomRate();
            new MessageBuilder().setEmbed(new EmbedBuilder()
                            .setAuthor(event.getMessageAuthor()).setTitle("Simp Calculator")
                            .setDescription(event.getMessageAuthor().getDisplayName()+" is "+rate+"% simp")
                            .setColor(Main.botAccentColor))
                    .send(event.getChannel());
        }
    }
}
