package org.prithvidiamond1.AudioPlayer;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.Event;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.prithvidiamond1.Main;

public class ParsedEvent{
    private MessageCreateEvent messageCreateEvent = null;
    private SlashCommandCreateEvent slashCommandCreateEvent = null;

    public ParsedEvent(Event inputEvent){
        if (inputEvent instanceof MessageCreateEvent){
            this.messageCreateEvent = (MessageCreateEvent) inputEvent;
        } else if (inputEvent instanceof SlashCommandCreateEvent){
            this.slashCommandCreateEvent = (SlashCommandCreateEvent) inputEvent;
        }
    }

    public DiscordApi getApi(){
        DiscordApi api = null;
        if (this.messageCreateEvent != null) {
            api = this.messageCreateEvent.getApi();
        } else if (this.slashCommandCreateEvent != null) {
            api = this.slashCommandCreateEvent.getApi();
        }
        return api;
    }

    public Server getServer(){
        Server server = null;
        if (this.messageCreateEvent != null) {
            if (this.messageCreateEvent.getServer().isPresent()){
                server = this.messageCreateEvent.getServer().get();
            }
        } else if (slashCommandCreateEvent != null) {
            if (this.slashCommandCreateEvent.getSlashCommandInteraction().getServer().isPresent()){
                server = this.slashCommandCreateEvent.getSlashCommandInteraction().getServer().get();
            }
        }
        return server;
    }

    public void sendEmbed(EmbedBuilder embed, ActionRow actionRow){
        if (actionRow == null){
            actionRow = ActionRow.of();
        }

        if (this.messageCreateEvent != null){
            new MessageBuilder()
                    .addEmbed(embed)
                    .addComponents(actionRow)
                    .send(messageCreateEvent.getChannel())
                    .exceptionally(exception -> {
                        Main.logger.error("Error trying to send embed!");
                        Main.logger.error(exception.getMessage());
                        return null;
                    });
        }
        else if (this.slashCommandCreateEvent != null){
            ActionRow finalActionRow = actionRow;
            this.slashCommandCreateEvent.getSlashCommandInteraction().respondLater().thenAccept(InteractionOriginalResponseUpdater->{
                InteractionOriginalResponseUpdater.addEmbed(embed)
                        .addComponents(finalActionRow)
                        .update();
            });

//            slashCommandCreateEvent.getSlashCommandInteraction().getChannel().ifPresent(textChannel -> {
//                new MessageBuilder()
//                        .addEmbed(embed)
//                        .addComponents(finalActionRow)
//                        .send(textChannel)
//                        .exceptionally(exception -> {
//                            Main.logger.error("Error trying to send embed!");
//                            Main.logger.error(exception.getMessage());
//                            return null;
//                        });
//            });
//            this.slashCommandCreateEvent.getSlashCommandInteraction().createImmediateResponder()
//                    .addEmbed(embed)
//                    .addComponents(actionRow)
//                    .respond()
//                    .exceptionally(exception -> {
//                        Main.logger.error("Error trying to send embed!");
//                        Main.logger.error(exception.getMessage());
//                        return null;
//                    });
        }
    }
}
