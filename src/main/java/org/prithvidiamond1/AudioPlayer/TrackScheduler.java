package org.prithvidiamond1.AudioPlayer;

import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.VideoSnippet;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.prithvidiamond1.AudioPlayer.Youtube.YoutubeSearchEngine;
import org.prithvidiamond1.Main;

import java.util.Iterator;
import java.util.concurrent.*;

import static org.prithvidiamond1.CommandFunctions.getYoutubeVideoUrl;

public class TrackScheduler implements AudioEventListener {

    private AudioTrack lastPlayedTrack;
    private TextChannel textChannel;
    private ServerVoiceChannel serverVoiceChannel;
    private ScheduledExecutorService scheduledExecutorService;

    private final BlockingDeque<AudioTrack> trackQueue;

    public final AudioPlayer audioPlayer;

    public TrackScheduler(TextChannel textChannel, ServerVoiceChannel serverVoiceChannel, AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.trackQueue = new LinkedBlockingDeque<>(100);
        this.textChannel = textChannel;
        this.serverVoiceChannel = serverVoiceChannel;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    public int getQueueSize(){
        return this.trackQueue.size();
    }

    public AudioTrack getNextTrackInQueue(){
        return this.trackQueue.peek();
    }

    public void nextTrack(){
        this.audioPlayer.startTrack(this.trackQueue.poll(), false);
    }

    public AudioTrack getLastPlayedTrack(){
        return this.lastPlayedTrack;
    }

    /**
     * Method that adds a track to the back of the deque (normal queuing)
     * @param track the AudioTrack to be added
     */
    public void queue(AudioTrack track){
        boolean addedToQueue = false;
        if(!this.audioPlayer.startTrack(track, true)){
            addedToQueue = this.trackQueue.offer(track);
        }
        Main.logger.info(String.format("Track successfully added to queue: %b", addedToQueue));
        Main.logger.info(String.format("Queue size: %d", this.trackQueue.size()));
    }

    /**
     * Method that adds a track to the front of the deque (jumps the queue)
     * @param track the AudioTrack to be added
     */
    public void jump(AudioTrack track){
        boolean addedToQueue = false;
        if(!this.audioPlayer.startTrack(track, true)){
            addedToQueue = this.trackQueue.offerFirst(track);
        }
        Main.logger.info(String.format("Track successfully jumped the queue: %b", addedToQueue));
        Main.logger.info(String.format("Queue size: %d", this.trackQueue.size()));
    }

    /**
     * Method that gets all the tracks in the queue in the form an iterator
     * @return an iterator to iter through the tracks in the queue.=
     */
    public Iterator<AudioTrack> getTracksInQueue(){
        return this.trackQueue.iterator();
    }

    private Runnable botDisconnect(){
        return () -> this.serverVoiceChannel.disconnect()
                        .exceptionally(exception -> {
                            Main.logger.error("An error occurred when trying to disconnect from a voice channel");
                            Main.logger.error(exception.getMessage());
                            return null;
                        });
    }

    private void onPlayerPause() {
        Runnable task = botDisconnect();
        if (this.scheduledExecutorService.isShutdown()){
            this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        }
        this.scheduledExecutorService.schedule(task, 1, TimeUnit.MINUTES);
    }

    private void onPlayerResume() {
        this.scheduledExecutorService.shutdownNow();
        if (this.scheduledExecutorService.isShutdown()){
            Main.logger.info("The bot disconnect scheduler has been shutdown intermittently and can be reset now");
        } else {
            Main.logger.info("The bot disconnect scheduler has not been shutdown intermittently");
        }
    }

    private void onTrackStart(AudioTrack track) {
        YoutubeSearchEngine youtube = new YoutubeSearchEngine();
        VideoSnippet video = youtube.getVideoSnippetById(track.getIdentifier());
        String thumbnailUrl = getYoutubeVideoUrl(video);

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Playing")
                .setDescription(track.getInfo().title)
                .setColor(Main.botAccentColor)
                .setThumbnail(thumbnailUrl);

        new MessageBuilder().addEmbed(embed)
                .addComponents(PlayerControlsHandler.playerActionRow)
                .send(this.textChannel)
                .exceptionally(exception -> {
                    Main.logger.error("Error trying to send embed!");
                    Main.logger.error(exception.getMessage());
                    return null;
                });
    }

    private void onTrackEnd(AudioTrack track, AudioTrackEndReason endReason) {
        this.lastPlayedTrack = track;

        if(endReason.mayStartNext){
            nextTrack();
        }

        boolean isPlayingTrackNull = this.audioPlayer.getPlayingTrack() == null;
        Main.logger.info(String.format("Is Playing Track Null: %b", isPlayingTrackNull));
        if (getQueueSize() == 0 && isPlayingTrackNull){
            this.serverVoiceChannel.disconnect()
                    .exceptionally(exception -> {
                        Main.logger.error("An error occurred when trying to disconnect from a voice channel");
                        Main.logger.error(exception.getMessage());
                        return null;
                    });
        }
    }

    private void onTrackException(AudioTrack track, FriendlyException exception) {
        Main.logger.error(String.format("Error during playback of the following track '%s'", track.getIdentifier()));
        Main.logger.error(exception.getMessage());
    }

    private void onTrackStuck(AudioTrack track, long thresholdMs, StackTraceElement[] stackTrace) {
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
