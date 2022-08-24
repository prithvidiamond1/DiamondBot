package org.prithvidiamond1.AudioPlayer.Sources;

public interface AudioSourceEngine {
    String fetchSourceId(String searchString);

    String fetchSourceLink(String searchString);

    String getThumbnailUrl(String Id);


}
