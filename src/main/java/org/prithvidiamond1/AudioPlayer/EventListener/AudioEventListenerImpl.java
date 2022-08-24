package org.prithvidiamond1.AudioPlayer.EventListener;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.prithvidiamond1.AudioPlayer.TrackQueuer.AudioTrackQueuer;
import org.prithvidiamond1.AudioPlayer.TrackQueuer.AudioTrackQueuerImpl;
import org.prithvidiamond1.AudioPlayer.VoiceChannelDisconnection;
import org.prithvidiamond1.Commands.PlayCommand;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * This class holds all the functions related to scheduling player tracks such as the track queue and player control functions
 */
@Component
public class AudioEventListenerImpl implements AudioEventListener {

    /**
     * The audio player instance that connects with the {@link AudioEventListenerImpl}
     */
    private final AudioPlayer audioPlayer;
    private final Logger logger;
    private final ApplicationContext appContext;
    private final VoiceChannelDisconnection channelDisconnection;

    /**
     * A simple constructor for the track scheduler
     *
     * @param audioPlayer          the audio player to be linked to this track scheduler
     * @param appContext           the application context object provided by Spring
     * @param channelDisconnection the channel disconnection object
     */
    public AudioEventListenerImpl(Logger logger, AudioPlayer audioPlayer, ApplicationContext appContext, VoiceChannelDisconnection channelDisconnection) {
        this.logger = logger;
        this.audioPlayer = audioPlayer;
        this.appContext = appContext;
        this.channelDisconnection = channelDisconnection;
    }
    
    /**
     * Method that runs when the linked audio player is paused
     */
    private void onPlayerPause() {
        PlayCommand playCommand = this.appContext.getBean(PlayCommand.class);
        ServerVoiceChannel voiceChannel = playCommand.getVoiceChannel();
        this.channelDisconnection.startBotDisconnectTimer(voiceChannel);
    }

    /**
     * Method that runs when the linked audio player is resumed
     */
    private void onPlayerResume() {
        this.channelDisconnection.stopBotDisconnectTimer();
    }

    /**
     * Method that runs when an audio track starts to play
     * @param track the audio track that is starting to play
     */
    private void onTrackStart(AudioTrack track) {
        AudioTrackQueuer trackQueuer = this.appContext.getBean(AudioTrackQueuer.class);
        if (trackQueuer.getQueueSize() == 0){
            this.channelDisconnection.stopBotDisconnectTimer();
        }
    }

    /**
     * Method that runs when the audio track ends
     * @param track the audio track that is ending
     * @param endReason the reason for the audio track to end
     */
    private void onTrackEnd(AudioTrack track, AudioTrackEndReason endReason) {
        AudioTrackQueuer trackQueuer = this.appContext.getBean(AudioTrackQueuer.class);
        trackQueuer.setLastPlayedTrack(track);

        if (endReason.mayStartNext){
            this.audioPlayer.startTrack(trackQueuer.removeNextTrackInQueue(), true);
        }

        if (trackQueuer.getQueueSize() == 0){
            PlayCommand playCommand = this.appContext.getBean(PlayCommand.class);
            ServerVoiceChannel voiceChannel = playCommand.getVoiceChannel();
            this.channelDisconnection.startBotDisconnectTimer(voiceChannel);
        }
    }

    /**
     * Method that runs when an exception is encountered during playback
     * @param track the associated audio track
     * @param exception the exception that was encountered
     */
    private void onTrackException(AudioTrack track, FriendlyException exception) {
        this.logger.error(String.format("Error during playback of the following track '%s'", track.getIdentifier()));
        this.logger.error(exception.getMessage());
    }

    /**
     * Method that runs when a track gets stuck in the audio player
     * @param track the associated audio track
     * @param thresholdMs the threshold time in milliseconds
     * @param stackTrace the associated stack trace
     */
    private void onTrackStuck(AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace) {
        this.logger.error(String.format("Error during playback of the following track '%s' at threshold '%d'",
                track.getIdentifier(),
                thresholdMs));
        StringBuilder errorMessage = new StringBuilder();
        for (StackTraceElement message: stackTrace){
            errorMessage.append(message.toString());
        }
        this.logger.error(errorMessage.toString());
    }

    /**
     * Method that runs when an {@link AudioEvent} occurs
     * @param event The event
     */
    @Override
    public void onEvent(AudioEvent event) {
        this.logger.info(String.format("Event: %s", event.toString()));
        if (event instanceof PlayerPauseEvent) {
            onPlayerPause();
        } else if (event instanceof PlayerResumeEvent) {
            onPlayerResume();
        } else if (event instanceof TrackStartEvent startEvent) {
            onTrackStart(startEvent.track);
        } else if (event instanceof TrackEndEvent endEvent) {
            onTrackEnd(endEvent.track, endEvent.endReason);
        } else if (event instanceof TrackExceptionEvent exceptionEvent) {
            onTrackException(exceptionEvent.track, exceptionEvent.exception);
        } else if (event instanceof TrackStuckEvent stuckEvent) {
            onTrackStuck(stuckEvent.track, stuckEvent.thresholdMs, stuckEvent.stackTrace);
        }
    }
}
