package org.prithvidiamond1.AudioPlayer.TrackQueuer;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Iterator;

public interface AudioTrackQueuer {
    /**
     * Method that gets the current size of the queue
     * @return the size of the queue
     */
    int getQueueSize();

    /**
     * Method that gets the next track in the queue (from the front)
     * @return the next track in the queue
     */
    AudioTrack getNextTrackInQueue();

    /**
     * Method that removes the next track in the queue (from the front)
     * @return the removed track
     */
    AudioTrack removeNextTrackInQueue();

    /**
     * Method that clears the track queue
     */
    void clearTrackQueue();

    /**
     * Method that gets the last played track
     * @return the last played track
     */
    AudioTrack getLastPlayedTrack();

    /**
     * Method that sets the last played track
     * @param track the last played track
     */
    void setLastPlayedTrack(AudioTrack track);

    /**
     * Method that adds a track to the front of the deque (jumps the queue)
     * @param track the AudioTrack to be added
     * @return the success status of the operation
     */
    boolean addTrackToTheFront(AudioTrack track);

    /**
     * Method that adds a track to the back of the deque (normal queuing)
     * @param track the AudioTrack to be added
     * @return the success status of the operation
     */
    boolean addTrackToTheBack(AudioTrack track);

    /**
     * Method that gets all the tracks in the queue in the form an iterator
     * @return an iterator to iter through the tracks in the queue
     */
    Iterator<AudioTrack> getTracksInQueue();

}
