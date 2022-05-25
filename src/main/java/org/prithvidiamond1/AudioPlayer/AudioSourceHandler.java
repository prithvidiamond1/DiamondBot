package org.prithvidiamond1.AudioPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.prithvidiamond1.Main;

public class AudioSourceHandler implements AudioLoadResultHandler {
    public final PlayerAudioSource playerAudioSource;

    public AudioSourceHandler(PlayerAudioSource playerAudioSource){
        this.playerAudioSource = playerAudioSource;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        this.playerAudioSource.audioPlayer.playTrack(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        for (AudioTrack track: playlist.getTracks()){
            this.playerAudioSource.audioPlayer.playTrack(track);
        }
    }

    @Override
    public void noMatches() {
        Main.logger.info("Audio Load Result - No matches found!");
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        Main.logger.error("An error has occurred while loading the requested audio");
        Main.logger.error(exception.getMessage());
    }
}
