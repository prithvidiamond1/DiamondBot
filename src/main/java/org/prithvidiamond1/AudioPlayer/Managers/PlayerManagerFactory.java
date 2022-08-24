package org.prithvidiamond1.AudioPlayer.Managers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class PlayerManagerFactory {
    private final Logger logger;


    public PlayerManagerFactory(Logger logger) {
        this.logger = logger;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) // Perhaps this can be Singleton Scope
    public AudioPlayerManager generatePlayerManager(){
        return new DefaultAudioPlayerManager();
    }

    @Bean
//    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) // Perhaps this can be Singleton Scope
    public AudioPlayer generateAudioPlayer(AudioPlayerManager playerManager, AudioEventListener audioEventListener){
        AudioPlayer player = playerManager.createPlayer();
        player.addListener(audioEventListener);
        return player;
    }
}
