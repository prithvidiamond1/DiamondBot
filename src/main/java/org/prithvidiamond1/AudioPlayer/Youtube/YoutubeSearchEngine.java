package org.prithvidiamond1.AudioPlayer.Youtube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import org.prithvidiamond1.Main;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Class that implements methods for using the YouTube Search Engine API
 */
public class YoutubeSearchEngine {
    /**
     * The YouTube service API instance
     */
    public YouTube youtube;

    /**
     * Constructor that initializes the search engine API service
     */
    public YoutubeSearchEngine(){
        try {
            this.youtube = YoutubeSearchEngineInitializer.getService();
        } catch (GeneralSecurityException | IOException exception) {
            Main.logger.error("Error trying to initialize Youtube Search Engine object!");
            Main.logger.error(exception.getMessage());
            this.youtube = null;
        }
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
                    .setKey(Main.youtubeApiKey)
                    .execute();
        } catch (IOException exception) {
            Main.logger.error("Error trying to search Youtube!");
            Main.logger.error(exception.getMessage());
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
                    .setKey(Main.youtubeApiKey)
                    .execute();
        } catch (IOException exception) {
            Main.logger.error("Error trying to retrieve Youtube API VideoListResponse!");
            Main.logger.error(exception.getMessage());
        }
        assert response != null;
        Video result = response.getItems().get(0);
        return result.getSnippet();
    }
}
