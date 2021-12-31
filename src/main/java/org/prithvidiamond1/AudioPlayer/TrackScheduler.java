package org.prithvidiamond1.AudioPlayer;

import com.google.api.services.youtube.model.VideoSnippet;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.prithvidiamond1.AudioPlayer.Youtube.YoutubeSearchEngine;
import org.prithvidiamond1.Main;

import java.util.concurrent.*;

public class TrackScheduler implements AudioEventListener {
    private ScheduledExecutorService scheduledExecutorService;

    private final ParsedEvent event;
    private final BlockingQueue<AudioTrack> trackQueue;
    private AudioTrack lastPlayedTrack;

    public final AudioPlayer player;
    public final ActionRow actionRow;

    public TrackScheduler(AudioPlayer player, ParsedEvent event){
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        this.event = event;
        this.player = player;
        this.trackQueue = new LinkedBlockingQueue<>(100);
        this.actionRow = ActionRow.of(
                Button.danger("PlayPause", "Play/Pause ⏯"),
                Button.secondary("SkipNextTrack", "Skip to next track ⏭")
        );
    }

    private Runnable botDisconnect(){
        return () -> this.event.getServer()
                .getConnectedVoiceChannel(this.event.getApi().getYourself())
                .ifPresent(voiceChannel -> voiceChannel.disconnect()
                        .exceptionally(exception -> {
                            Main.logger.error("An error occurred when trying to disconnect from a voice channel");
                            Main.logger.error(exception.getMessage());
                            return null;
                        }));
    }

    public int getQueueSize(){
        return this.trackQueue.size();
    }

    public AudioTrack getNextTrackInQueue(){
        return this.trackQueue.peek();
    }

    public void queue(AudioTrack track){
        boolean addedToQueue = false;
        if(!this.player.startTrack(track, true)){
            addedToQueue = this.trackQueue.offer(track);
        }
        Main.logger.info(String.format("Track successfully added to queue: %b", addedToQueue));
        Main.logger.info(String.format("Queue size: %d", this.trackQueue.size()));
    }

    public void nextTrack(){
        this.player.startTrack(this.trackQueue.poll(), false);
    }

    public void onPlayerPause() {
        Runnable task = botDisconnect();
        if (this.scheduledExecutorService.isShutdown()){
            this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        }
        this.scheduledExecutorService.schedule(task, 1, TimeUnit.MINUTES);
    }

    public void onPlayerResume() {
        this.scheduledExecutorService.shutdownNow();
        if (this.scheduledExecutorService.isShutdown()){
            Main.logger.info("The bot disconnect scheduler has been shutdown intermittently and can be reset now");
        } else {
            Main.logger.info("The bot disconnect scheduler has not been shutdown intermittently");
        }
    }

    public void onTrackStart(AudioTrack track){
        this.lastPlayedTrack = track;

        YoutubeSearchEngine youtube = new YoutubeSearchEngine();
        VideoSnippet video = youtube.getVideoSnippetById(track.getIdentifier());
        String thumbnailUrl = video.getThumbnails().getStandard().getUrl();

        EmbedBuilder embedMessage = new EmbedBuilder()
                .setTitle("Playing")
                .setDescription(track.getInfo().title)
                .setColor(Main.botAccentColor)
                .setThumbnail(thumbnailUrl);
        this.event.sendEmbed(embedMessage, this.actionRow);
    }

    public void onTrackEnd(AudioTrack track, AudioTrackEndReason endReason){
        if(endReason.mayStartNext){
            nextTrack();
        }

        boolean isPlayingTrackNull = this.player.getPlayingTrack() == null;
        Main.logger.info(String.format("Is Playing Track Null: %b", isPlayingTrackNull));
        if (getQueueSize() == 0){
            this.event.getServer()
                    .getConnectedVoiceChannel(this.event.getApi().getYourself())
                    .ifPresent(voiceChannel -> voiceChannel.disconnect()
                    .exceptionally(exception -> {
                        Main.logger.error("An error occurred when trying to disconnect from a voice channel");
                        Main.logger.error(exception.getMessage());
                        return null;
                    }));
        }
    }

    // Temporary implementation
    public void onTrackException(AudioTrack track, FriendlyException exception){
        Main.logger.error(String.format("Error during playback of the following track '%s'", track.getIdentifier()));
        Main.logger.error(exception.getMessage());
    }

    // Temporary implementation
    public void onTrackStuck(AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace){
        Main.logger.error(String.format("Error during playback of the following track '%s' at threshold '%d'",
                track.getIdentifier(),
                thresholdMs));
        StringBuilder errorMessage = new StringBuilder();
        for (StackTraceElement message: stackTrace){
            errorMessage.append(message.toString());
        }
        Main.logger.error(errorMessage.toString());
    }

    @Override
    public void onEvent(AudioEvent event) {
        Main.logger.info(String.format("Event: %s", event.toString()));
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
