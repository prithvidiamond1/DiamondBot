package org.prithvidiamond1.AudioPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.audio.AudioSourceBase;

public class PlayerAudioSource extends AudioSourceBase {
    private final ParsedEvent event;
    private final AudioPlayerManager manager;
    private AudioFrame lastFrame;

    public final TrackScheduler scheduler;

    /**
     * Creates a new audio source base.
     *
     * @param api The discord api instance.
     */
    public PlayerAudioSource(DiscordApi api, AudioPlayerManager manager, ParsedEvent event) {
        super(api);
        this.event = event;
        this.manager = manager;
        this.scheduler = new TrackScheduler(this.manager.createPlayer(), event);
        this.scheduler.player.addListener(scheduler);
    }

    @Override
    public byte[] getNextFrame() {
        if (this.lastFrame == null){
            return null;
        }
        return applyTransformers(this.lastFrame.getData());
    }

    @Override
    public boolean hasFinished(){
        return false;
    }

    @Override
    public boolean hasNextFrame() {
        this.lastFrame = this.scheduler.player.provide();
        return lastFrame != null;
    }

    @Override
    public AudioSource copy() {
        return new PlayerAudioSource(getApi(), this.manager, this.event);
    }
}
