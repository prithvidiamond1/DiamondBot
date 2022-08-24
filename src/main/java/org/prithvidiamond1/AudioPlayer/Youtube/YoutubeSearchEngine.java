package org.prithvidiamond1.AudioPlayer.Youtube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import org.prithvidiamond1.BotConstants;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Class that implements methods for using the YouTube Search Engine API
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class YoutubeSearchEngine {
    /**
     * The YouTube service API instance
     */
    private final YouTube youtube;
    private final Logger logger;

    /**
     * Constructor that initializes the search engine API service
     */
    public YoutubeSearchEngine(Logger logger, YouTube youtube){
        this.logger = logger;
        this.youtube = youtube;
    }

    /**
     * Method that returns the best YouTube search result based on a search string
     * @param searchString a user defined search string
     * @return the best YouTube search result
     */
    public SearchResult getBestSearchResult(String searchString){
        YouTube.Search.List searchRequest;
        SearchListResponse searchResponse = null;

        try {
            searchRequest = this.youtube.search().list(List.of("snippet"));
            searchResponse = searchRequest.setMaxResults(25L)
                    .setQ(searchString)
                    .setKey(BotConstants.youtubeApiKey)
                    .execute();
        } catch (IOException exception) {
            this.logger.error("Error trying to search Youtube!");
            this.logger.error(exception.getMessage());
        }

        assert searchResponse != null;
        return searchResponse.getItems().get(0);
    }

    /**
     * Method that gets a YouTube video snippet based on its video ID
     * @param videoId the YouTube video's ID
     * @return a video snippet of the YouTube video
     */
    public VideoSnippet getVideoSnippetById(String videoId){
        YouTube.Videos.List request;
        VideoListResponse response = null;
        try {
            request = this.youtube.videos().list(List.of("snippet"));
            response = request.setMaxResults(1L)
                    .setId(List.of(videoId))
                    .setKey(BotConstants.youtubeApiKey)
                    .execute();
        } catch (IOException exception) {
            this.logger.error("Error trying to retrieve Youtube API VideoListResponse!");
            this.logger.error(exception.getMessage());
        }
        assert response != null;
        Video result = response.getItems().get(0);
        return result.getSnippet();
    }
}
