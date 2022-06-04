package org.prithvidiamond1;

import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.VideoSnippet;
import org.testng.internal.collections.Pair;

/**
 * This class holds all the secondary functions required for the bot commands
 */
public class CommandFunctions {
    /**
     * Method to calculate gay rate and to get url for the respective gayness image
     * @return returns a Pair object containing a String and an Integer: the string contains the url for the gayness image and the integer is the calculated gay rate
     */
    public static Pair<String, Integer> gayRate(){
        String gayness;
        int rate= randomRate();
        if(rate<20)
            gayness="https://img.buzzfeed.com/buzzfeed-static/static/2015-11/20/10/campaign_images/webdr12/worlds-leading-anxiety-expert-found-curing-people-2-8748-1448032226-1_dblbig.jpg";
        else if(20<rate && rate<50)
            gayness="https://i.ytimg.com/vi/qO_Dk_Z2zRM/maxresdefault.jpg";
        else if(50<rate && rate<80)
            gayness="https://i0.wp.com/www.culturesonar.com/wp-content/uploads/2021/06/drew-pisarra-book.jpg";
        else
            gayness="https://i1.sndcdn.com/artworks-000655332292-x1ui3u-t500x500.jpg";
        return new Pair<>(gayness, rate);
    }

    /**
     * Method to calculate a random rate between 1 and 100
     * @return returns a random integer between 1 and 100
     */
    public static int randomRate(){
        return (int)(Math.random()*100+1);
    }

    public static String getYoutubeVideoUrl(VideoSnippet video){
        Thumbnail thumbnail = video.getThumbnails().getStandard();
        String thumbnailUrl = "https://i.imgur.com/OkGXI5L.png"; // replacement image.
        if (thumbnail != null){
            thumbnailUrl = thumbnail.getUrl();
        } else {
            thumbnail = video.getThumbnails().getDefault();
            if (thumbnail != null){
                thumbnailUrl = thumbnail.getUrl();
            }
        }
        return thumbnailUrl;
    }
}
