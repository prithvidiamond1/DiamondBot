package org.prithvidiamond1.AudioPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.audio.AudioSourceBase;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.TextChannel;

public class PlayerAudioSource extends AudioSourceBase {

    private AudioFrame lastFrame;
    private TextChannel textChannel;
    private ServerVoiceChannel serverVoiceChannel;
    private final AudioPlayerManager audioPlayerManager;

    public final AudioPlayer audioPlayer;
    public final TrackScheduler trackScheduler;

    /**
     * Creates a new audio source base.
     *
     * @param api The discord api instance.
     * @param audioPlayerManager the audio player manager
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
        this.lastFrame = this.trackScheduler.audioPlayer.provide();
        return lastFrame != null;
    }

    @Override
    public AudioSource copy() {
        return new PlayerAudioSource(getApi(), this.textChannel, this.serverVoiceChannel, this.audioPlayerManager);
    }
}
