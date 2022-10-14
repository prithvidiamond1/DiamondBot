package org.prithvidiamond1.AudioPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;
import org.prithvidiamond1.AudioPlayer.Sources.AudioSourceEngine;
import org.prithvidiamond1.AudioPlayer.TrackQueuer.AudioTrackQueuer;
import org.prithvidiamond1.BotConstants;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class contains methods and fields for handling the audio player controls from the user's end
 */
@Component
public class PlayerControlsListener implements MessageComponentCreateListener {
    private final Logger logger;
    private final AudioPlayer audioPlayer;
    private final AudioTrackQueuer trackQueuer;

    private AudioSourceEngine sourceEngine;

    /**
     * The {@link ActionRow} that contains the audio player control buttons
     */
    public final static ActionRow audioPlayerActionRow = ActionRow.of(
            Button.secondary("SkipToPreviousTrack", "Skip to previous track ⏮"),
            Button.danger("PlayPause", "Play/Pause ⏯"),
            Button.secondary("SkipToNextTrack", "Skip to next track ⏭"),
            Button.primary("ViewFullTrackQueue", "View Full Track Queue"),
            Button.danger("ClearTrackQueue", "Clear Track Queue")
    );

    public final static ActionRow audioPlayerActionRowWithoutPlayPause = ActionRow.of(
            Button.secondary("SkipToPreviousTrack", "Skip to previous track ⏮"),
            Button.secondary("SkipToNextTrack", "Skip to next track ⏭"),
            Button.primary("ViewFullTrackQueue", "View Full Track Queue"),
            Button.danger("ClearTrackQueue", "Clear Track Queue")
    );

    /**
     * Simple constructor for initializing the PlayerControlsHandler
     *
     * @param logger the logger instance
     * @param audioPlayer the audio player instance
     * @param trackQueuer the track queuer instance
     */
    public PlayerControlsListener(Logger logger, AudioPlayer audioPlayer, AudioTrackQueuer trackQueuer) {
        this.logger = logger;
        this.audioPlayer = audioPlayer;
        this.trackQueuer = trackQueuer;
    }

    /**
     * Method that sets the currentAudioSourceEngine
     * @param sourceEngine the {@link AudioSourceEngine} currently in use
     */
    public void setCurrentAudioSourceEngine(AudioSourceEngine sourceEngine){
        this.sourceEngine = sourceEngine;
    }

    /**
     * Method that handles the play/pause button function of the bot's player
     * @param componentInteraction the interaction from the button ({@link MessageComponentInteraction})
     */
    private void playPause(MessageComponentInteraction componentInteraction){
        componentInteraction.createOriginalMessageUpdater()
                .removeAllComponents().update().thenAccept(interactionOriginalResponseUpdater -> {
            String thumbnailUrl = this.sourceEngine.getThumbnailUrl(this.audioPlayer.getPlayingTrack().getIdentifier());

            boolean playerPaused = this.audioPlayer.isPaused();
            if (playerPaused){
                this.audioPlayer.setPaused(false);
                componentInteraction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                                .setTitle("Resumed Playback")
                                .setDescription(this.audioPlayer.getPlayingTrack().getInfo().title)
                                .setColor(BotConstants.botAccentColor)
                                .setThumbnail(thumbnailUrl))
                        .addComponents(audioPlayerActionRow).send()
                        .exceptionally(exception -> {
                            this.logger.error("Unable to respond to this interaction!");
                            this.logger.error(exception.getMessage());
                            return null;
                        });
            } else{
                this.audioPlayer.setPaused(true);
                componentInteraction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                                .setTitle("Playback Paused")
                                .setDescription(this.audioPlayer.getPlayingTrack().getInfo().title)
                                .setColor(BotConstants.botAccentColor)
                                .setThumbnail(thumbnailUrl))
                        .addComponents(audioPlayerActionRow).send()
                        .exceptionally(exception -> {
                            this.logger.error("Unable to respond to this interaction!");
                            this.logger.error(exception.getMessage());
                            return null;
                        });
            }
        }).exceptionally(exception -> {
            this.logger.error("Unable to register play/pause action!");
            this.logger.error(exception.getMessage());
            return null;
        });
    }

    /**
     * Method that handles the skip forward button function of the bot's player
     * @param componentInteraction the interaction from the button ({@link MessageComponentInteraction})
     */
    private void skipToNextTrack(MessageComponentInteraction componentInteraction){
        componentInteraction.createOriginalMessageUpdater()
                .removeAllComponents().update().thenAccept(interactionOriginalResponseUpdater -> {
            AudioTrack nextTrack = this.trackQueuer.getNextTrackInQueue();
            if (this.trackQueuer.getQueueSize() > 0) {
                componentInteraction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                        .setTitle("Skipping forward to next track")
                        .setDescription(String.format("Skipping forward to %s", nextTrack.getInfo().title))
                        .setColor(BotConstants.botAccentColor)
                        .setThumbnail(BotConstants.botIconURL)).send()
                        .exceptionally(exception -> {
                            this.logger.error("Unable to respond to this interaction!");
                            this.logger.error(exception.getMessage());
                            return null;
                        });
                this.audioPlayer.startTrack(this.trackQueuer.removeNextTrackInQueue(), false);
            } else {
                componentInteraction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                        .setTitle("No tracks currently in queue to skip forward to")
                        .setDescription("Add some tracks to the queue and then try skipping forward to them")
                        .setColor(BotConstants.botAccentColor)
                        .setThumbnail(BotConstants.botIconURL))
                        .addComponents(audioPlayerActionRow)
                        .send()
                        .exceptionally(exception -> {
                            this.logger.error("Unable to respond to this interaction!");
                            this.logger.error(exception.getMessage());
                            return null;
                        });
            }
        }).exceptionally(exception -> {
            this.logger.info("Unable to register skip forward action!");
            this.logger.info(exception.getMessage());
            return null;
        });
    }

    /**
     * Method that handles the skip previous button function of the bot's player
     * @param componentInteraction the interaction from the button ({@link MessageComponentInteraction})
     */
    private void skipToPreviousTrack(MessageComponentInteraction componentInteraction){
        componentInteraction.createOriginalMessageUpdater()
                .removeAllComponents().update().thenAccept(interactionOriginalResponseUpdater -> {
            AudioTrack nextTrack = this.trackQueuer.getLastPlayedTrack();
            if (nextTrack != null) {
                componentInteraction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                        .setTitle("Skipping to previously played track")
                        .setDescription(String.format("Playing %s", nextTrack.getInfo().title))
                        .setColor(BotConstants.botAccentColor)
                        .setThumbnail(BotConstants.botIconURL)).send()
                        .exceptionally(exception -> {
                            this.logger.error("Unable to respond to this interaction!");
                            this.logger.error(exception.getMessage());
                            return null;
                        });
                this.trackQueuer.addTrackToTheFront(nextTrack.makeClone());
                this.audioPlayer.startTrack(this.trackQueuer.removeNextTrackInQueue(), false);
            } else {
                componentInteraction.createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                        .setTitle("No track previously played")
                        .setDescription("Finish playing some tracks and then try skipping back to them")
                        .setColor(BotConstants.botAccentColor)
                        .setThumbnail(BotConstants.botIconURL))
                        .addComponents(audioPlayerActionRow)
                        .send()
                        .exceptionally(exception -> {
                            this.logger.error("Unable to respond to this interaction!");
                            this.logger.error(exception.getMessage());
                            return null;
                        });
            }
        }).exceptionally(exception -> {
            this.logger.info("Unable to register skip to previous action!");
            this.logger.info(exception.getMessage());
            return null;
        });
    }

    /**
     * Method that handles the view full track queue button function of the bot's player
     * @param componentInteraction the interaction from the button ({@link MessageComponentInteraction})
     */
    public void viewFullTrackQueue(MessageComponentInteraction componentInteraction){
        Iterator<AudioTrack> iter = this.trackQueuer.getTracksInQueue();
        ArrayList<String> embedDescriptions = new ArrayList<>();
        StringBuilder descriptionBuilder = new StringBuilder();
        int trackQueueSize = this.trackQueuer.getQueueSize();
        String descriptionStartLine = trackQueueSize <= 1 ? String.format("Currently **%d** track in queue\n", trackQueueSize):
                String.format("Currently **%d** tracks in queue\n", trackQueueSize);
        descriptionBuilder.append(descriptionStartLine).append("\n");
        if (iter.hasNext()) {
            while (iter.hasNext()) {
                AudioTrack audioTrack = iter.next();

                String trackTitle = audioTrack.getInfo().title;

                if (descriptionBuilder.length() + trackTitle.length() >= 4096) {
                    this.logger.info(String.format("Description: %s", descriptionBuilder));
                    embedDescriptions.add(descriptionBuilder.toString());
                    descriptionBuilder.delete(0, descriptionBuilder.length());
                }

                descriptionBuilder.append(trackTitle).append("\n");
            }
            embedDescriptions.add(descriptionBuilder.toString());
        }

        componentInteraction.createOriginalMessageUpdater()
                .removeAllComponents().update().thenAccept(interactionOriginalResponseUpdater -> {
            if (embedDescriptions.size() == 0){
                componentInteraction .createFollowupMessageBuilder().addEmbed(new EmbedBuilder()
                        .setTitle("No tracks currently in queue")
                        .setDescription("Try adding some tracks to the queue using the play command before checking to see the queue")
                        .setColor(BotConstants.botAccentColor)
                        .setThumbnail(BotConstants.botIconURL))
                        .addComponents(audioPlayerActionRow).send()
                        .exceptionally(exception -> {
                            this.logger.error("Unable to respond to this interaction!");
                            this.logger.error(exception.getMessage());
                            return null;
                        });
            } else {
                EmbedBuilder startingEmbed = new EmbedBuilder()
                        .setTitle(String.format("Track Queue - %d/%d", 1, embedDescriptions.size()))
                        .setDescription(embedDescriptions.get(0))
                        .setColor(BotConstants.botAccentColor)
                        .setThumbnail(BotConstants.botIconURL);
                if (embedDescriptions.size() == 1){
                    componentInteraction.createFollowupMessageBuilder().addEmbed(startingEmbed)
                            .addComponents(audioPlayerActionRow)
                            .send()
                            .exceptionally(exception -> {
                                this.logger.error("Unable to respond to this interaction!");
                                this.logger.error(exception.getMessage());
                                return null;
                            });
                } else {
                    componentInteraction.createFollowupMessageBuilder().addEmbed(startingEmbed)
                            .send()
                            .exceptionally(exception -> {
                                this.logger.error("Unable to respond to this interaction!");
                                this.logger.error(exception.getMessage());
                                return null;
                            });
                }
            }
        }).exceptionally(exception -> {
            this.logger.info("Unable to register view track queue action!");
            this.logger.info(exception.getMessage());
            return null;
        });

        for (int i = 1; i < embedDescriptions.size() - 1; i++){
            new MessageBuilder().addEmbed(new EmbedBuilder()
                    .setTitle(String.format("Track Queue - %d/%d", i+1, embedDescriptions.size()))
                    .setDescription(embedDescriptions.get(0))
                    .setColor(BotConstants.botAccentColor)
                    .setThumbnail(BotConstants.botIconURL))
                    .send(componentInteraction.getChannel().orElse(null))
                    .exceptionally(exception -> {
                        this.logger.error("Unable to respond to this interaction!");
                        this.logger.error(exception.getMessage());
                        return null;
                    });
        }
        if (embedDescriptions.size() > 1) {
            EmbedBuilder endingEmbed = new EmbedBuilder()
                    .setTitle(String.format("Track Queue - %d/%d", embedDescriptions.size(), embedDescriptions.size()))
                    .setDescription(embedDescriptions.get(embedDescriptions.size() - 1))
                    .setColor(BotConstants.botAccentColor)
                    .setThumbnail(BotConstants.botIconURL);

            new MessageBuilder().addEmbed(endingEmbed)
                    .addComponents(audioPlayerActionRow)
                    .send(componentInteraction.getChannel().orElse(null))
                    .exceptionally(exception -> {
                        this.logger.error("Unable to respond to this interaction!");
                        this.logger.error(exception.getMessage());
                        return null;
                    });
        }
    }

    /**
     * Method that handles the clear track queue button function of the bot's player
     * @param componentInteraction the interaction from the button ({@link MessageComponentInteraction})
     */
    public void clearTrackQueue(MessageComponentInteraction componentInteraction){
        componentInteraction.createOriginalMessageUpdater()
                .removeAllComponents().update().thenAccept(interactionOriginalResponseUpdater -> {
           this.trackQueuer.clearTrackQueue();
           componentInteraction.createFollowupMessageBuilder().addEmbed(
                   new EmbedBuilder()
                           .setTitle("Cleared Track Queue")
                           .setDescription("Currently 0 tracks in queue")
                           .setColor(BotConstants.botAccentColor)
                           .setThumbnail(BotConstants.botIconURL))
                   .addComponents(audioPlayerActionRow)
                   .send()
                   .exceptionally(exception -> {
                       this.logger.info("Unable to register clear track queue action!");
                       this.logger.info(exception.getMessage());
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
