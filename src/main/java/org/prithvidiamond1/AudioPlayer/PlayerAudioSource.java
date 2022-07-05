package org.prithvidiamond1.AudioPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.audio.AudioSourceBase;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.TextChannel;

/**
 * This class holds functions related to a player's audio source
 */
public class PlayerAudioSource extends AudioSourceBase {

    private AudioFrame lastFrame;
    private final TextChannel textChannel;
    private final ServerVoiceChannel serverVoiceChannel;
    private final AudioPlayerManager audioPlayerManager;

    /**
     * The audio player instance
     */
    public final AudioPlayer audioPlayer;

    /**
     * The track scheduler instance that connects to the {@link AudioPlayer}
     */
    public final TrackScheduler trackScheduler;

    /**
     * Creates a new audio source base.
     *
     * @param api The discord api instance.
     * @param audioPlayerManager the audio player manager
     * @param serverVoiceChannel the server voice channel to which the bot connected to
     * @param textChannel the text channel in which the play command was invoked
     */
    public PlayerAudioSource(DiscordApi api, TextChannel textChannel, ServerVoiceChannel serverVoiceChannel, AudioPlayerManager audioPlayerManager) {
        super(api);
        this.audioPlayerManager = audioPlayerManager;
        this.textChannel = textChannel;
        this.serverVoiceChannel = serverVoiceChannel;
        this.trackScheduler = new TrackScheduler(this.textChannel, this.serverVoiceChannel, this.audioPlayerManager.createPlayer());
        this.trackScheduler.audioPlayer.addListener(trackScheduler);
        this.audioPlayer = this.trackScheduler.audioPlayer;
    }

    /**
     * Method to link a player controls handler to this player audio source's track scheduler
     * @param playerControlsHandler the player control handler
     */
    public void addPlayerControlsHandler (PlayerControlsHandler playerControlsHandler){
        this.trackScheduler.addPlayerControlsHandler(playerControlsHandler);
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
        this.lastFrame = this.trackScheduler.audioPlayer.provide();
        return lastFrame != null;
    }

    /**
     * Method that returns a copy of the current audio source
     * @return an audio source copy
     */
    @Override
    public AudioSource copy() {
        return new PlayerAudioSource(getApi(), this.textChannel, this.serverVoiceChannel, this.audioPlayerManager);
    }
}
