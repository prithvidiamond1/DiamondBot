package org.prithvidiamond1.AudioPlayer.Sources.Youtube;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.VideoSnippet;
import org.prithvidiamond1.AudioPlayer.Sources.AudioSourceEngine;
import org.prithvidiamond1.AudioPlayer.Youtube.YoutubeSearchEngine;
import org.prithvidiamond1.BotConstants;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class YoutubeSourceEngine implements AudioSourceEngine {
    private final Logger logger;
    private final YoutubeSearchEngine youtubeSearchEngine;

    public YoutubeSourceEngine(Logger logger, YoutubeSearchEngine youtubeSearchEngine){
        this.logger = logger;
        this.youtubeSearchEngine = youtubeSearchEngine;
    }

    @Override
    public String fetchSourceId(String searchString) {
        String id = null;
        try {
            SearchResult topResult = this.youtubeSearchEngine.getBestSearchResult(searchString);
             id = topResult.getId().getVideoId();
        } catch (Exception exception){
            this.logger.error(exception.getMessage());
        }
        return id;
    }

    @Override
    public String fetchSourceLink(String searchString) {
        return String.format("https://www.youtube.com/watch?v=%s", fetchSourceId(searchString));
    }

    @Override
    public String getThumbnailUrl(String id) {
        String thumbnailUrl = BotConstants.defaultThumbnailUrl; // replacement image.

        try {
            VideoSnippet video = this.youtubeSearchEngine.getVideoSnippetById(id);

            Thumbnail thumbnail = video.getThumbnails().getStandard();

            if (thumbnail != null) {
                thumbnailUrl = thumbnail.getUrl();
            } else {
                thumbnail = video.getThumbnails().getDefault();
                if (thumbnail != null) {
                    thumbnailUrl = thumbnail.getUrl();
                }
            }
        } catch (Exception exception){
            this.logger.error(exception.getMessage());
        }

        return thumbnailUrl;
    }
}
