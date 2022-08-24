package org.prithvidiamond1.AudioPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.audio.AudioSourceBase;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * This class holds functions related to a player's audio source
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) // Could be made singleton
public class AudioSourceImpl extends AudioSourceBase {

    private AudioFrame lastFrame;

    /**
     * The audio player instance
     */
    public final AudioPlayer audioPlayer;

    /**
     * The logger object
     */
    private final Logger logger;

    /**
     * Creates a new audio source base.
     *
     * @param logger
     * @param api The discord api instance.
     * @param audioPlayer
     */
    public AudioSourceImpl(Logger logger, DiscordApi api, AudioPlayer audioPlayer) {
        super(api);
        this.logger = logger;
        this.audioPlayer = audioPlayer;
    }

    /**
     * Method that gets the next frame in the audio source
     * @return a byte array
     */
    @Override
    public byte[] getNextFrame() {
        if (this.lastFrame == null){
            return null;
        }
        return applyTransformers(this.lastFrame.getData());
    }

    /**
     * Method that checks if an audio source has finished
     * @return a boolean (although this implementation defaults to false all the time)
     */
    @Override
    public boolean hasFinished(){
        return false;
    }

    /**
     * Method that checks if an audio source has another frame ahead
     * @return a boolean
     */
    @Override
    public boolean hasNextFrame() {
        this.lastFrame = this.audioPlayer.provide();
        return lastFrame != null;
    }

    /**
     * Method that returns a copy of the current audio source
     * @return an audio source copy
     */
    @Override
    public AudioSource copy() {
        return new AudioSourceImpl(this. logger, getApi(), this.audioPlayer);
    }
}
