package org.prithvidiamond1;

import java.awt.*;

public class BotConstants {
    /**
     * The bot's accent color (currently a shade of cyan)
     */
    public static Color botAccentColor = new Color(60, 220, 255);

    /**
     * The default guild prefix for bot guild commands
     */
    public static String defaultGuildPrefix = "!";

    /**
     * String containing a URL to the bot's icon image
     */
    public static String botIconURL = "https://i.imgur.com/ERxQB6z.png";

    /**
     * Supported Audio Sources for voice channel audio playback
     */
    public static String[] audioSources = {
            "youtube",
    };

    /**
     * The YouTube API key
     */
    public static String youtubeApiKey = System.getenv().get("YT_API_KEY");


    public static String defaultThumbnailUrl = "https://i.imgur.com/OkGXI5L.png"; // replacement image.

//    /**
//     * Enumerations for the different voice channel connection states
//     */
//    public enum VoiceConnectionStatus {
//        /**
//         * State for successful audio connection
//         */
//        Successful,
//        /**
//         * State for unsuccessful audio connection
//         */
//        Unsuccessful,
//        /**
//         * State for a pre-existing audio connection
//         */
//        AlreadyConnected
//    }

}
