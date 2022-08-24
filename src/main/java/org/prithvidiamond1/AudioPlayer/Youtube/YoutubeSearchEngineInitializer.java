package org.prithvidiamond1.AudioPlayer.Youtube;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Class for initializing a YouTube Search Engine API instance
 */
@Component
public class YoutubeSearchEngineInitializer {
    private final Logger logger;
    private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public YoutubeSearchEngineInitializer(Logger logger){
        this.logger = logger;
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     */
    @Bean
    public YouTube getService() {
        YouTube youtube = null;
        try {
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            youtube = new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                    .setApplicationName("BotYoutubeSearchEngine")
                    .build();
        } catch (GeneralSecurityException | IOException exception) {
            this.logger.error("Error trying to initialize Youtube Search Engine object!");
            this.logger.error(exception.getMessage());
        }
        return youtube;
    }

}
