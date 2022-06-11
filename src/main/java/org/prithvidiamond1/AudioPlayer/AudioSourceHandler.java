package org.prithvidiamond1.AudioPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.prithvidiamond1.Main;

/**
 * Class that handles the player source and its related functions such track and playlist loading.
 */
public class AudioSourceHandler implements AudioLoadResultHandler {
    /**
     * The audio source to which the audio player is connected to
     */
    public final PlayerAudioSource playerAudioSource;

    /**
     * Constructor for this class
     * @param playerAudioSource an audio source for this audio source handler
     */
    public AudioSourceHandler(PlayerAudioSource playerAudioSource){
        this.playerAudioSource = playerAudioSource;
    }

    /**
     * Method to load a track
     * @param track The loaded track
     */
    @Override
    public void trackLoaded(AudioTrack track) {
        this.playerAudioSource.audioPlayer.playTrack(track);
    }

    /**
     * Method to load a playlist
     * @param playlist The loaded playlist
     */
    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        for (AudioTrack track: playlist.getTracks()){
            this.playerAudioSource.audioPlayer.playTrack(track);
        }
    }

    /**
     * Method to call in case of no matched audio sources
     */
    @Override
    public void noMatches() {
        Main.logger.info("Audio Load Result - No matches found!");
    }

    /**
     * Method to call in case of an exception occurring during track or playlist loading
     * @param exception The exception that was thrown
     */
    @Override
    public void loadFailed(FriendlyException exception) {
        Main.logger.error("An error has occurred while loading the requested audio");
        Main.logger.error(exception.getMessage());
    }
}
