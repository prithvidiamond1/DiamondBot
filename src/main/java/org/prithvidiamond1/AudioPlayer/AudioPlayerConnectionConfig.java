package org.prithvidiamond1.AudioPlayer;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class AudioPlayerConnectionConfig {
    private final Logger logger;

    private ServerVoiceChannel voiceChannel;
    private TextChannel textChannel;
    private AudioSourceManager currentSourceManager;

    public AudioPlayerConnectionConfig(Logger logger){
        this.logger = logger;
    }

    public ServerVoiceChannel getVoiceChannel() {
        return voiceChannel;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }

    public AudioSourceManager getCurrentSourceManager() {
        return currentSourceManager;
    }

    public void setVoiceChannel(ServerVoiceChannel voiceChannel) {
        this.voiceChannel = voiceChannel;
    }

    public void setTextChannel(TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    public void setCurrentSourceManager(AudioSourceManager currentSourceManager) {
        this.currentSourceManager = currentSourceManager;
    }
}