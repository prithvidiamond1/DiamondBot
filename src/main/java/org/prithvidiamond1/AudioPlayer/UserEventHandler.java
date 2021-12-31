package org.prithvidiamond1.AudioPlayer;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.channel.VoiceChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;

public class UserEventHandler {
    final private DiscordApi api;
    final private Server server;
    private VoiceChannel voiceChannel;
    private TextChannel textChannel;

    public UserEventHandler(DiscordApi api, Server server, VoiceChannel voiceChannel, TextChannel textChannel){
        this.api = api;
        this.server = server;
        this.voiceChannel = voiceChannel;
        this.textChannel = textChannel;
    }

    // Other methods
    // Check if I get SlashCommandInteractions working with MessageBuilder
//    public void nextTrackPlayingMessage(){
//        new MessageBuilder()
//    }

    // Getters and Setters
    public DiscordApi getApi() {
        return api;
    }

    public Server getServer() {
        return server;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }

    public VoiceChannel getVoiceChannel() {
        return voiceChannel;
    }

    public void setTextChannel(TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    public void setVoiceChannel(VoiceChannel voiceChannel) {
        this.voiceChannel = voiceChannel;
    }
}
