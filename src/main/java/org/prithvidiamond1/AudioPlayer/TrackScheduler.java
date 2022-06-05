package org.prithvidiamond1.AudioPlayer;

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

/**
 * This class holds all the functions related to scheduling player tracks such as the track queue and player control functions
 */
public class TrackScheduler implements AudioEventListener {

    private AudioTrack lastPlayedTrack;
    private final TextChannel textChannel;
    private final ServerVoiceChannel serverVoiceChannel;
    private ScheduledExecutorService scheduledExecutorService;

    private final BlockingDeque<AudioTrack> trackQueue;

    public final AudioPlayer audioPlayer;

    /**
     * A simple constructor for the track scheduler
     * @param textChannel the text channel in which the play command was invoked
     * @param serverVoiceChannel the server in which the play command was invoked
     * @param audioPlayer the audio player to be linked to this track scheduler
     */
    public TrackScheduler(TextChannel textChannel, ServerVoiceChannel serverVoiceChannel, AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.trackQueue = new LinkedBlockingDeque<>(100);
        this.textChannel = textChannel;
        this.serverVoiceChannel = serverVoiceChannel;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    /**
     * Method that gets the current size of the queue
     * @return the size of the queue
     */
    public int getQueueSize(){
        return this.trackQueue.size();
    }

    /**
     * Method that gets the next track in the queue;
     * @return the next track in the queue
     */
    public AudioTrack getNextTrackInQueue(){
        return this.trackQueue.peek();
    }

    /**
     * Method that plays the next track in the queue
     */
    public void nextTrack(){
        this.audioPlayer.startTrack(this.trackQueue.poll(), false);
    }

    /**
     * Method that gets the last played track
     * @return the last played track
     */
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

        String embedDescription;
        int queueSize = this.getQueueSize();
        if (queueSize == 1) {
            embedDescription = String.format("Currently %d track in queue\n To view the full queue, click the **View Full Track Queue** button", queueSize);
        } else {
            embedDescription = String.format("Currently %d tracks in queue\n To view the full queue, click the **View Full Track Queue** button", queueSize);
        }
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(String.format("Added to Queue - %s", track.getInfo().title))
                .setDescription(embedDescription)
                .setColor(Main.botAccentColor)
                .setThumbnail(Main.botIconURL);

        this.sendMessageEmbed(embed);

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

    /**
     * Method that performs the disconnect function for the bot's {@link ScheduledExecutorService}
     * @return a {@link Runnable} version of this function
     */
    private Runnable botDisconnect(){
        return () -> this.serverVoiceChannel.disconnect()
                        .exceptionally(exception -> {
                            Main.logger.error("An error occurred when trying to disconnect from a voice channel");
                            Main.logger.error(exception.getMessage());
                            return null;
                        });
    }

    /**
     * Method that sends an embed as a message
     * @param embed the embed ({@link EmbedBuilder})
     */
    private void sendMessageEmbed (EmbedBuilder embed){
        new MessageBuilder().addEmbed(embed)
                .addComponents(PlayerControlsHandler.playerActionRow)
                .send(this.textChannel)
                .exceptionally(exception -> {
                    Main.logger.error("Error trying to send embed!");
                    Main.logger.error(exception.getMessage());
                    return null;
                });
    }

    /**
     * Method that starts the bot's disconnect sequence
     */
    private void botDisconnectTimerStartSequence(){
        Runnable task = botDisconnect();
        if (this.scheduledExecutorService.isShutdown()){
            this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        }
        this.scheduledExecutorService.schedule(task, 1, TimeUnit.MINUTES);
    }

    /**
     * Method that pauses the bot's disconnect sequence
     */
    private void botDisconnectTimerPauseSequence(){
        this.scheduledExecutorService.shutdownNow();
        if (this.scheduledExecutorService.isShutdown()){
            Main.logger.info("The bot disconnect scheduler has been shutdown intermittently and can be reset now");
        } else {
            Main.logger.info("The bot disconnect scheduler has not been shutdown intermittently");
        }
    }

    /**
     * Method that runs when the linked audio player is paused
     */
    private void onPlayerPause() {
        botDisconnectTimerStartSequence();
    }

    /**
     * Method that runs when the linked audio player is resumed
     */
    private void onPlayerResume() {
        botDisconnectTimerPauseSequence();
    }

    /**
     * Method that runs when an audio track starts to play
     * @param track the audio track is starting to play
     */
    private void onTrackStart(AudioTrack track) {
        botDisconnectTimerPauseSequence();

        YoutubeSearchEngine youtube = new YoutubeSearchEngine();
        VideoSnippet video = youtube.getVideoSnippetById(track.getIdentifier());
        String thumbnailUrl = getYoutubeVideoUrl(video);

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Playing")
                .setDescription(track.getInfo().title)
                .setColor(Main.botAccentColor)
                .setThumbnail(thumbnailUrl);

        this.sendMessageEmbed(embed);
    }

    /**
     * Method that runs when the audio track ends
     * @param track the audio track is ending
     * @param endReason the reason for the audio track to end
     */
    private void onTrackEnd(AudioTrack track, AudioTrackEndReason endReason) {
        this.lastPlayedTrack = track;

        if(endReason.mayStartNext){
            nextTrack();
        }

        boolean isPlayingTrackNull = this.audioPlayer.getPlayingTrack() == null;
        Main.logger.info(String.format("Is Playing Track Null: %b", isPlayingTrackNull));
        if (getQueueSize() == 0 && isPlayingTrackNull){
            botDisconnectTimerStartSequence();
        }
    }

    /**
     * Method that runs when an exception is encountered during playback
     * @param track the associated audio track
     * @param exception the exception that was encountered
     */
    private void onTrackException(AudioTrack track, FriendlyException exception) {
        Main.logger.error(String.format("Error during playback of the following track '%s'", track.getIdentifier()));
        Main.logger.error(exception.getMessage());
    }

    /**
     * Method that runs when a track gets stuck in the audio player
     * @param track the associated audio track
     * @param thresholdMs the threshold time in milliseconds
     * @param stackTrace the associated stack trace
     */
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

    /**
     * Method that runs when an {@link AudioEvent} occurs
     * @param event The event
     */
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
