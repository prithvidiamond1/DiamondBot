package org.prithvidiamond1.AudioPlayer.Youtube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import org.prithvidiamond1.Main;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class YoutubeSearchEngine {
    public YouTube youtube;

    public YoutubeSearchEngine(){
        try {
            this.youtube = YoutubeSearchEngineInitializer.getService();
        } catch (GeneralSecurityException | IOException exception) {
            Main.logger.error("Error trying to initialize Youtube Search Engine object!");
            Main.logger.error(exception.getMessage());
            this.youtube = null;
        }
    }

    public SearchResult getBestSearchResult(String searchString){
        YouTube.Search.List searchRequest;
        SearchListResponse searchResponse = null;

        try {
            searchRequest = this.youtube.search().list("snippet");
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

    public VideoSnippet getVideoSnippetById(String videoId){
        YouTube.Videos.List request;
        VideoListResponse response = null;
        try {
            request = this.youtube.videos().list("snippet");
            response = request.setMaxResults(1L)
                    .setId(videoId)
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
