package org.prithvidiamond1.SlashCommands.EventListeners.EventImplements;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.prithvidiamond1.Main;
import org.prithvidiamond1.SlashCommands.EventListeners.SlashPingEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SlashPingEventImplement implements SlashPingEvent {

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event){
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        if (slashCommandInteraction.getCommandName().equals("ping")){
            Optional<Server> server = slashCommandInteraction.getServer();
            server.ifPresent(value -> slashCommandInteraction.createImmediateResponder()
                    .addEmbed(new EmbedBuilder()
                            .setTitle("Hello " + slashCommandInteraction.getUser().getDisplayName(server.get()) + "!")
                            .setThumbnail(slashCommandInteraction.getUser().getAvatar())
                            .setColor(Main.botAccentColor))
                    .respond());
        }
    }
}
