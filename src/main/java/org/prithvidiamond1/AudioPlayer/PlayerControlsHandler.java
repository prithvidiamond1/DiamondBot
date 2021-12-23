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
    private final AudioSourceLoadResultHandler audioSourceHandler;

    public PlayerControlsHandler(AudioSourceLoadResultHandler audioSourceHandler){
        this.audioSourceHandler = audioSourceHandler;
    }

    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {
        MessageComponentInteraction interaction = event.getMessageComponentInteraction();
        String customId = interaction.getCustomId();

        switch (customId){
            case "PlayPause":
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    YoutubeSearchEngine youtube = new YoutubeSearchEngine();
                    VideoSnippet video = youtube.getVideoSnippetById(this.audioSourceHandler.audioSource.scheduler.player.getPlayingTrack().getIdentifier());
                    String thumbnailUrl = video.getThumbnails().getStandard().getUrl();

                    boolean playerPaused = this.audioSourceHandler.audioSource.scheduler.player.isPaused();
                    if (playerPaused){
//                        this.audioSourceHandler.audioSource.scheduler.onPlayerResume();
                        this.audioSourceHandler.audioSource.scheduler.player.setPaused(false);
                        interaction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                                        .setTitle("Resumed Playback")
                                        .setDescription(this.audioSourceHandler.audioSource.scheduler.player.getPlayingTrack().getInfo().title)
                                        .setColor(Main.botAccentColor)
                                        .setThumbnail(thumbnailUrl))
                                .addComponents(
                                        ActionRow.of(
                                                Button.danger("PlayPause", "Play/Pause ⏯"),
                                                Button.secondary("SkipNextTrack", "Skip to next track ⏭")
                                        )
                                ).send();
                    } else{
//                        this.audioSourceHandler.audioSource.scheduler.onPlayerPause();
                        this.audioSourceHandler.audioSource.scheduler.player.setPaused(true);
                        interaction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                                        .setTitle("Playback Paused")
                                        .setDescription(this.audioSourceHandler.audioSource.scheduler.player.getPlayingTrack().getInfo().title)
                                        .setColor(Main.botAccentColor)
                                .setThumbnail(thumbnailUrl))
                                .addComponents(
                                        ActionRow.of(
                                                Button.danger("PlayPause", "Play/Pause ⏯"),
                                                Button.secondary("SkipNextTrack", "Skip to next track ⏭")
                                        )
                                ).send();
                    }
                }).exceptionally(exception -> {
                    Main.logger.error("Unable to register play/pause action!");
                    Main.logger.error(exception.getMessage());
                    return null;
                });

                break;
            case "SkipNextTrack":
                interaction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
                    AudioTrack nextTrack = this.audioSourceHandler.audioSource.scheduler.getNextTrackInQueue();
                    if (this.audioSourceHandler.audioSource.scheduler.getQueueSize() > 0) {
                        interaction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                                .setTitle("Skipping forward to next track")
                                .setDescription(String.format("Skipping forward to %s", nextTrack.getInfo().title))
                                .setColor(Main.botAccentColor)
                                .setThumbnail(Main.botIconURL)
                        ).send().exceptionally(exception -> {
                            Main.logger.error("Unable to respond to this interaction!");
                            Main.logger.error(exception.getMessage());
                            return null;
                        });
                        this.audioSourceHandler.audioSource.scheduler.nextTrack();
                    } else {
                        interaction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
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

                break;

        }
    }
}
