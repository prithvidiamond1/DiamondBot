package org.prithvidiamond1.AudioPlayer;

import com.google.api.services.youtube.model.VideoSnippet;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;
import org.prithvidiamond1.AudioPlayer.Youtube.YoutubeSearchEngine;
import org.prithvidiamond1.Main;

public class PlayerControlsHandler implements MessageComponentCreateListener {
    private final AudioSourceHandler audioSourceHandler;

    public final static ActionRow playerActionRow = ActionRow.of(
            Button.danger("PlayPause", "Play/Pause ⏯"),
            Button.secondary("SkipToNextTrack", "Skip to next track ⏭")
    );

    public PlayerControlsHandler(AudioSourceHandler audioSourceHandler) {
        this.audioSourceHandler = audioSourceHandler;
    }

    private void playPause(MessageComponentInteraction componentInteraction){
        componentInteraction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
            YoutubeSearchEngine youtube = new YoutubeSearchEngine();
            VideoSnippet video = youtube.getVideoSnippetById(this.audioSourceHandler.playerAudioSource.audioPlayer.getPlayingTrack().getIdentifier());
            String thumbnailUrl = video.getThumbnails().getStandard().getUrl();

            boolean playerPaused = this.audioSourceHandler.playerAudioSource.audioPlayer.isPaused();
            if (playerPaused){
//                        this.audioSourceHandler.audioSource.scheduler.onPlayerResume();
                this.audioSourceHandler.playerAudioSource.audioPlayer.setPaused(false);
                componentInteraction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                                .setTitle("Resumed Playback")
                                .setDescription(this.audioSourceHandler.playerAudioSource.audioPlayer.getPlayingTrack().getInfo().title)
                                .setColor(Main.botAccentColor)
                                .setThumbnail(thumbnailUrl))
                        .addComponents(playerActionRow).send();
            } else{
//                        this.audioSourceHandler.audioSource.scheduler.onPlayerPause();
                this.audioSourceHandler.playerAudioSource.audioPlayer.setPaused(true);
                componentInteraction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                                .setTitle("Playback Paused")
                                .setDescription(this.audioSourceHandler.playerAudioSource.audioPlayer.getPlayingTrack().getInfo().title)
                                .setColor(Main.botAccentColor)
                                .setThumbnail(thumbnailUrl))
                        .addComponents(playerActionRow).send();
            }
        }).exceptionally(exception -> {
            Main.logger.error("Unable to register play/pause action!");
            Main.logger.error(exception.getMessage());
            return null;
        });
    }

    private void skipToNextTrack(MessageComponentInteraction componentInteraction){
        componentInteraction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
            AudioTrack nextTrack = this.audioSourceHandler.playerAudioSource.trackScheduler.getNextTrackInQueue();
            if (this.audioSourceHandler.playerAudioSource.trackScheduler.getQueueSize() > 0) {
                componentInteraction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                        .setTitle("Skipping forward to next track")
                        .setDescription(String.format("Skipping forward to %s", nextTrack.getInfo().title))
                        .setColor(Main.botAccentColor)
                        .setThumbnail(Main.botIconURL)
                ).send().exceptionally(exception -> {
                    Main.logger.error("Unable to respond to this interaction!");
                    Main.logger.error(exception.getMessage());
                    return null;
                });
                this.audioSourceHandler.playerAudioSource.trackScheduler.nextTrack();
            } else {
                componentInteraction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                        .setTitle("No tracks currently in queue to skip forward to")
                        .setDescription("Add some tracks to the queue and then try skipping forward to them")
                        .setColor(Main.botAccentColor)
                        .setThumbnail(Main.botIconURL)
                ).send();
            }
        }).exceptionally(exception -> {
            Main.logger.info("Unable to register skip forward action!");
            Main.logger.info(exception.getMessage());
            return null;
        });
    }

    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {
        MessageComponentInteraction componentInteraction = event.getMessageComponentInteraction();
        String customId = componentInteraction.getCustomId();

        switch (customId){
            case "PlayPause":
                playPause(componentInteraction);
                break;
            case "SkipToNextTrack":
                skipToNextTrack(componentInteraction);
                break;
        }
    }
}
