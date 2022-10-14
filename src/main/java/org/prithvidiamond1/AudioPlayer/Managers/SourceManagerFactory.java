package org.prithvidiamond1.AudioPlayer.Managers;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class SourceManagerFactory {
    private final Logger logger;


    public SourceManagerFactory(Logger logger) {
        this.logger = logger;
    }

    @Bean(name = "youtubeAudioSourceManager")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) // this could be a singleton
    public AudioSourceManager generateYoutubeSourceManager(){
        return new YoutubeAudioSourceManager();
    }
}
