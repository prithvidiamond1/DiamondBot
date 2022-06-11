package org.prithvidiamond1.AudioPlayer;

import com.google.api.services.youtube.model.VideoSnippet;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;
import org.prithvidiamond1.AudioPlayer.Youtube.YoutubeSearchEngine;
import org.prithvidiamond1.Main;

import java.util.ArrayList;
import java.util.Iterator;

import static org.prithvidiamond1.CommandFunctions.getYoutubeVideoUrl;

/**
 * This class contains methods and fields for handling the audio player controls from the user's end
 */
public class PlayerControlsHandler implements MessageComponentCreateListener {
    private final AudioSourceHandler audioSourceHandler;

    /**
     * The {@link ActionRow} that contains the audio player control buttons
     */
    public final static ActionRow playerActionRow = ActionRow.of(
            Button.secondary("SkipToPreviousTrack", "Skip to previous track ⏮"),
            Button.danger("PlayPause", "Play/Pause ⏯"),
            Button.secondary("SkipToNextTrack", "Skip to next track ⏭"),
            Button.primary("ViewFullTrackQueue", "View Full Track Queue"),
            Button.danger("ClearTrackQueue", "Clear Track Queue")
    );

    /**
     * Simple constructor for initializing the PlayerControlsHandler
     * @param audioSourceHandler the AudioSourceHandler of the active bot's AudioPlayer instance
     */
    public PlayerControlsHandler(AudioSourceHandler audioSourceHandler) {
        this.audioSourceHandler = audioSourceHandler;
    }

    /**
     * Method that handles the play/pause button function of the bot's player
     * @param componentInteraction the interaction from the button ({@link MessageComponentInteraction})
     */
    private void playPause(MessageComponentInteraction componentInteraction){
        componentInteraction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
            YoutubeSearchEngine youtube = new YoutubeSearchEngine();
            VideoSnippet video = youtube.getVideoSnippetById(this.audioSourceHandler.playerAudioSource.audioPlayer.getPlayingTrack().getIdentifier());
            String thumbnailUrl = getYoutubeVideoUrl(video);

            boolean playerPaused = this.audioSourceHandler.playerAudioSource.audioPlayer.isPaused();
            if (playerPaused){
                this.audioSourceHandler.playerAudioSource.audioPlayer.setPaused(false);
                componentInteraction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                                .setTitle("Resumed Playback")
                                .setDescription(this.audioSourceHandler.playerAudioSource.audioPlayer.getPlayingTrack().getInfo().title)
                                .setColor(Main.botAccentColor)
                                .setThumbnail(thumbnailUrl))
                        .addComponents(playerActionRow).send()
                        .exceptionally(exception -> {
                            Main.logger.error("Unable to respond to this interaction!");
                            Main.logger.error(exception.getMessage());
                            return null;
                        });
            } else{
                this.audioSourceHandler.playerAudioSource.audioPlayer.setPaused(true);
                componentInteraction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                                .setTitle("Playback Paused")
                                .setDescription(this.audioSourceHandler.playerAudioSource.audioPlayer.getPlayingTrack().getInfo().title)
                                .setColor(Main.botAccentColor)
                                .setThumbnail(thumbnailUrl))
                        .addComponents(playerActionRow).send()
                        .exceptionally(exception -> {
                            Main.logger.error("Unable to respond to this interaction!");
                            Main.logger.error(exception.getMessage());
                            return null;
                        });
            }
        }).exceptionally(exception -> {
            Main.logger.error("Unable to register play/pause action!");
            Main.logger.error(exception.getMessage());
            return null;
        });
    }

    /**
     * Method that handles the skip forward button function of the bot's player
     * @param componentInteraction the interaction from the button ({@link MessageComponentInteraction})
     */
    private void skipToNextTrack(MessageComponentInteraction componentInteraction){
        componentInteraction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
            AudioTrack nextTrack = this.audioSourceHandler.playerAudioSource.trackScheduler.getNextTrackInQueue();
            if (this.audioSourceHandler.playerAudioSource.trackScheduler.getQueueSize() > 0) {
                componentInteraction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                        .setTitle("Skipping forward to next track")
                        .setDescription(String.format("Skipping forward to %s", nextTrack.getInfo().title))
                        .setColor(Main.botAccentColor)
                        .setThumbnail(Main.botIconURL)).send()
                        .exceptionally(exception -> {
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
                        .setThumbnail(Main.botIconURL))
                        .addComponents(playerActionRow)
                        .send()
                        .exceptionally(exception -> {
                            Main.logger.error("Unable to respond to this interaction!");
                            Main.logger.error(exception.getMessage());
                            return null;
                        });
            }
        }).exceptionally(exception -> {
            Main.logger.info("Unable to register skip forward action!");
            Main.logger.info(exception.getMessage());
            return null;
        });
    }

    /**
     * Method that handles the skip previous button function of the bot's player
     * @param componentInteraction the interaction from the button ({@link MessageComponentInteraction})
     */
    private void skipToPreviousTrack(MessageComponentInteraction componentInteraction){
        componentInteraction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
            AudioTrack nextTrack = this.audioSourceHandler.playerAudioSource.trackScheduler.getLastPlayedTrack();
            if (nextTrack != null) {
                componentInteraction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                        .setTitle("Skipping to previously played track")
                        .setDescription(String.format("Playing %s", nextTrack.getInfo().title))
                        .setColor(Main.botAccentColor)
                        .setThumbnail(Main.botIconURL)).send()
                        .exceptionally(exception -> {
                            Main.logger.error("Unable to respond to this interaction!");
                            Main.logger.error(exception.getMessage());
                            return null;
                        });
                this.audioSourceHandler.playerAudioSource.trackScheduler.jump(nextTrack.makeClone());
                this.audioSourceHandler.playerAudioSource.trackScheduler.nextTrack();
            } else {
                componentInteraction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                        .setTitle("No track previously played")
                        .setDescription("Finish playing some tracks and then try skipping back to them")
                        .setColor(Main.botAccentColor)
                        .setThumbnail(Main.botIconURL))
                        .addComponents(playerActionRow)
                        .send()
                        .exceptionally(exception -> {
                            Main.logger.error("Unable to respond to this interaction!");
                            Main.logger.error(exception.getMessage());
                            return null;
                        });
            }
        }).exceptionally(exception -> {
            Main.logger.info("Unable to register skip to previous action!");
            Main.logger.info(exception.getMessage());
            return null;
        });
    }

    /**
     * Method that handles the view full track queue button function of the bot's player
     * @param componentInteraction the interaction from the button ({@link MessageComponentInteraction})
     */
    public void viewFullTrackQueue(MessageComponentInteraction componentInteraction){
        Iterator<AudioTrack> iter = this.audioSourceHandler.playerAudioSource.trackScheduler.getTracksInQueue();
        ArrayList<String> embedDescriptions = new ArrayList<>();
        StringBuilder descriptionBuilder = new StringBuilder();
        if (iter.hasNext()) {
            while (iter.hasNext()) {
                AudioTrack audioTrack = iter.next();

                String trackTitle = audioTrack.getInfo().title;

                if (descriptionBuilder.length() + trackTitle.length() >= 4096) {
                    Main.logger.info(String.format("Description: %s", descriptionBuilder));
                    embedDescriptions.add(descriptionBuilder.toString());
                    descriptionBuilder.delete(0, descriptionBuilder.length());
                }

                descriptionBuilder.append(trackTitle).append("\n");
            }
            embedDescriptions.add(descriptionBuilder.toString());
        }

        componentInteraction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
            if (embedDescriptions.size() == 0){
                componentInteraction .createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                        .setTitle("No tracks currently in queue")
                        .setDescription("Try adding some tracks to the queue using the play command before checking to see the queue")
                        .setColor(Main.botAccentColor)
                        .setThumbnail(Main.botIconURL))
                        .addComponents(playerActionRow).send()
                        .exceptionally(exception -> {
                            Main.logger.error("Unable to respond to this interaction!");
                            Main.logger.error(exception.getMessage());
                            return null;
                        });
            } else {
                EmbedBuilder startingEmbed = new EmbedBuilder()
                        .setTitle(String.format("Track Queue - %d/%d", 1, embedDescriptions.size()))
                        .setDescription(embedDescriptions.get(0))
                        .setColor(Main.botAccentColor)
                        .setThumbnail(Main.botIconURL);
                if (embedDescriptions.size() == 1){
                    componentInteraction.createFollowupMessageBuilder().addEmbed(startingEmbed)
                            .addComponents(playerActionRow)
                            .send()
                            .exceptionally(exception -> {
                                Main.logger.error("Unable to respond to this interaction!");
                                Main.logger.error(exception.getMessage());
                                return null;
                            });
                } else {
                    componentInteraction.createFollowupMessageBuilder().addEmbed(startingEmbed)
                            .send()
                            .exceptionally(exception -> {
                                Main.logger.error("Unable to respond to this interaction!");
                                Main.logger.error(exception.getMessage());
                                return null;
                            });
                }
            }
        }).exceptionally(exception -> {
            Main.logger.info("Unable to register view track queue action!");
            Main.logger.info(exception.getMessage());
            return null;
        });

        for (int i = 1; i < embedDescriptions.size(); i++){
            new MessageBuilder().addEmbed(new EmbedBuilder()
                    .setTitle(String.format("Track Queue - %d/%d", i+1, embedDescriptions.size()))
                    .setDescription(embedDescriptions.get(0))
                    .setColor(Main.botAccentColor)
                    .setThumbnail(Main.botIconURL))
                    .send(componentInteraction.getChannel().orElse(null))
                    .exceptionally(exception -> {
                        Main.logger.error("Unable to respond to this interaction!");
                        Main.logger.error(exception.getMessage());
                        return null;
                    });
        }
    }

    /**
     * Method that handles the clear track queue button function of the bot's player
     * @param componentInteraction the interaction from the button ({@link MessageComponentInteraction})
     */
    public void clearTrackQueue(MessageComponentInteraction componentInteraction){
        componentInteraction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
           this.audioSourceHandler.playerAudioSource.trackScheduler.clearTrackQueue();
           componentInteraction.createFollowupMessageBuilder().addEmbed(
                   new EmbedBuilder()
                           .setTitle("Cleared Track Queue")
                           .setDescription("Currently 0 tracks in queue")
                           .setColor(Main.botAccentColor)
                           .setThumbnail(Main.botIconURL))
                   .addComponents(playerActionRow)
                   .send()
                   .exceptionally(exception -> {
                       Main.logger.info("Unable to register clear track queue action!");
                       Main.logger.info(exception.getMessage());
                       return null;
                   });
        });
    }

    /**
     * Method that handles button events ({@link MessageComponentCreateEvent})
     * @param event The message component trigger event
     */
    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {
        MessageComponentInteraction componentInteraction = event.getMessageComponentInteraction();
        String customId = componentInteraction.getCustomId();

        switch (customId) {
            case "PlayPause" -> playPause(componentInteraction);
            case "SkipToNextTrack" -> skipToNextTrack(componentInteraction);
            case "SkipToPreviousTrack" -> skipToPreviousTrack(componentInteraction);
            case "ViewFullTrackQueue" -> viewFullTrackQueue(componentInteraction);
            case "ClearTrackQueue" -> clearTrackQueue(componentInteraction);
        }
    }
}
