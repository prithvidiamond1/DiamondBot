package org.prithvidiamond1.AudioPlayer.TrackQueuer;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class AudioTrackQueuerImpl implements AudioTrackQueuer{
    private final Logger logger;
    private final BlockingDeque<AudioTrack> trackQueue;

    private AudioTrack lastPlayedTrack;

    private static final int MAX_DEQUE_CAPACITY = 100;

    public AudioTrackQueuerImpl(Logger logger){
        this.logger = logger;
        this.trackQueue = new LinkedBlockingDeque<>(MAX_DEQUE_CAPACITY);
    }

    @Override
    public int getQueueSize() {
        return this.trackQueue.size();
    }

    @Override
    public AudioTrack getNextTrackInQueue() {
        return this.trackQueue.peek();
    }

    @Override
    public AudioTrack removeNextTrackInQueue() {
        return this.trackQueue.poll();
    }

    @Override
    public void clearTrackQueue() {
        this.trackQueue.clear();
    }

    @Override
    public AudioTrack getLastPlayedTrack() {
        return this.lastPlayedTrack;
    }

    @Override
    public void setLastPlayedTrack(AudioTrack track) {
        this.lastPlayedTrack = track;
    }

    @Override
    public boolean addTrackToTheFront(AudioTrack track) {
        return this.trackQueue.offerFirst(track);
    }

    @Override
    public boolean addTrackToTheBack(AudioTrack track) {
        return this.trackQueue.offer(track);
    }

    @Override
    public Iterator<AudioTrack> getTracksInQueue() {
        return this.trackQueue.iterator();
    }
}
