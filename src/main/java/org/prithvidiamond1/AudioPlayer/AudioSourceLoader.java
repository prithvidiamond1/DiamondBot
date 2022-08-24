package org.prithvidiamond1.AudioPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.audio.AudioSource;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Class that handles the player source and its related functions such track and playlist loading.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) // Could be made singleton
public class AudioSourceLoader implements AudioLoadResultHandler {
    /**
     * The audio source to which the audio player is connected to
     */
    private final Logger logger;
    private final AudioPlayer audioPlayer;

    /**
     * Constructor for this class
     * @param logger
     * @param audioPlayer
     */
    public AudioSourceLoader(Logger logger, AudioPlayer audioPlayer){
        this.logger = logger;
        this.audioPlayer = audioPlayer;
    }

    /**
     * Method to load a track
     * @param track The loaded track
     */
    @Override
    public void trackLoaded(AudioTrack track) {
        this.audioPlayer.playTrack(track);
    }

    /**
     * Method to load a playlist
     * @param playlist The loaded playlist
     */
    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        for (AudioTrack track: playlist.getTracks()){
            this.audioPlayer.playTrack(track);
        }
    }

    /**
     * Method to call in case of no matched audio sources
     */
    @Override
    public void noMatches() {
        this.logger.info("Audio Load Result - No matches found!");
    }

    /**
     * Method to call in case of an exception occurring during track or playlist loading
     * @param exception The exception that was thrown
     */
    @Override
    public void loadFailed(FriendlyException exception) {
        this.logger.error("An error has occurred while loading the requested audio");
        this.logger.error(exception.getMessage());
    }
}
