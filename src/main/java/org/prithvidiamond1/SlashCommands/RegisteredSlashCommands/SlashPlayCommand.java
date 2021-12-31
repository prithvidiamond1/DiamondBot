package org.prithvidiamond1.SlashCommands.RegisteredSlashCommands;

import com.google.api.services.youtube.model.SearchResult;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.prithvidiamond1.AudioPlayer.AudioSourceLoadResultHandler;
import org.prithvidiamond1.AudioPlayer.ParsedEvent;
import org.prithvidiamond1.AudioPlayer.PlayerAudioSource;
import org.prithvidiamond1.AudioPlayer.PlayerControlsHandler;
import org.prithvidiamond1.AudioPlayer.Youtube.YoutubeSearchEngine;
import org.prithvidiamond1.Main;
import org.prithvidiamond1.SlashCommands.SlashCommandInterface;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SlashPlayCommand implements SlashCommandInterface {
    public AudioSourceLoadResultHandler audioSourceHandler;

    public Main.VoiceConnection connectToSource(SlashCommandCreateEvent event, AudioSource source) {
        Main.VoiceConnection voiceConnectionState = Main.VoiceConnection.Unsuccessful;
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        long senderId = slashCommandInteraction.getUser().getId();
        Optional<Server> serverOptional = slashCommandInteraction.getServer();
        if (serverOptional.isPresent()) {
            Server server = serverOptional.get();
            if (server.getConnectedVoiceChannel(senderId).isPresent()) {
                Main.logger.info(String.format("Bot connection status: Is connected = %b", server.getConnectedVoiceChannel(senderId).get().isConnected(event.getApi().getYourself())));
                if (server.getConnectedVoiceChannel(senderId).get().isConnected(event.getApi().getYourself())) {
                    voiceConnectionState = Main.VoiceConnection.AlreadyConnected;
                } else {
                    server.getConnectedVoiceChannel(senderId).get().connect()
                            .thenAccept(audioConnection -> audioConnection.setAudioSource(source))
                            .exceptionally(exception -> {
                                Main.logger.error("Unexpected error trying to play the requested audio!");
                                Main.logger.error(exception.getMessage());
                                return null;
                            });
                    voiceConnectionState = Main.VoiceConnection.Successful;
                }
            } else {
                List<ServerVoiceChannel> voiceChannels = server.getVoiceChannels();
                if (!voiceChannels.isEmpty()) {
                    boolean emptyVoiceChannelPresent = false;
                    for (ServerVoiceChannel voiceChannel : voiceChannels) {
                        if (voiceChannel.getConnectedUsers().isEmpty()) {
                            emptyVoiceChannelPresent = true;
                            voiceChannel.connect().thenAccept(audioConnection -> audioConnection.setAudioSource(source))
                                    .exceptionally(exception -> {
                                        Main.logger.error("Unexpected error trying to play the requested audio!");
                                        Main.logger.error(exception.getMessage());
                                        return null;
                                    });
                            voiceConnectionState = Main.VoiceConnection.Successful;
                        } else {
                            Main.logger.info(String.format("Bot connection status: Is connected = %b", voiceChannel.isConnected(event.getApi().getYourself())));
                            if (voiceChannel.isConnected(event.getApi().getYourself())) {
                                voiceConnectionState = Main.VoiceConnection.AlreadyConnected;
                            }
                        }
                    }
                    if (!emptyVoiceChannelPresent) {
                        slashCommandInteraction.createImmediateResponder()
                                .addEmbed(new EmbedBuilder()
                                        .setTitle("No free voice channels present in the server!")
                                        .setDescription("If you think one of the voice channels in the server can be disturbed, join the channel and then try playing something")
                                        .setThumbnail(Main.botIconURL)
                                        .setColor(Main.botAccentColor))
                                .respond().exceptionally(exception -> {
                                    Main.logger.error("Unable to respond to the slash command");
                                    Main.logger.error(exception.getMessage());
                                    return null;
                                });
                    }

                } else {
                    slashCommandInteraction.createImmediateResponder()
                            .addEmbed(new EmbedBuilder()
                                    .setTitle("No accessible voice channels found in the server!")
                                    .setDescription("Add a voice channel to this server and then try playing something")
                                    .setThumbnail(Main.botIconURL)
                                    .setColor(Main.botAccentColor))
                            .respond().exceptionally(exception -> {
                                Main.logger.error("Unable to respond to the slash command");
                                Main.logger.error(exception.getMessage());
                                return null;
                            });
                }
            }
        }
        return voiceConnectionState;
    }


    @Override
    public void runCommand(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();

        List<SlashCommandInteractionOption> commandArgs = slashCommandInteraction.getOptions();
        Optional<String> requestedAudioSource = commandArgs.get(0).getStringValue();

         if (Main.guildCommandRunner.guildPlayCommand.audioSourceHandler != null) {
            this.audioSourceHandler = Main.guildCommandRunner.guildPlayCommand.audioSourceHandler;
        }

        if (requestedAudioSource.isPresent()) {
            if (Arrays.stream(Main.audioSources).anyMatch(source -> source.equals(requestedAudioSource.get().toLowerCase()))) {
                Main.logger.info("phase 1 - works");
                AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
                if (requestedAudioSource.get().equalsIgnoreCase("youtube")) {
                    Optional<String> searchString = commandArgs.get(1).getStringValue();
                    if (searchString.isPresent()) {
                        Main.logger.info("phase 3 - works");
                        YoutubeSearchEngine youtubeSearch = new YoutubeSearchEngine();
                        SearchResult topResult = youtubeSearch.getBestSearchResult(searchString.get());
                        String videoId = topResult.getId().getVideoId();
                        String videoLink = String.format("https://www.youtube.com/watch?v=%s", videoId);

                        YoutubeAudioSourceManager youtubeSourceManager = new YoutubeAudioSourceManager();
                        playerManager.registerSourceManager(youtubeSourceManager);
                        PlayerAudioSource playerAudioSource = new PlayerAudioSource(event.getApi(), playerManager, new ParsedEvent(event));
                        Main.VoiceConnection voiceConnectionState = connectToSource(event, playerAudioSource);
                        if (voiceConnectionState.equals(Main.VoiceConnection.Successful)) {
                            if (Main.guildCommandRunner.guildPlayCommand.audioSourceHandler == null) {
                                this.audioSourceHandler = new AudioSourceLoadResultHandler(playerAudioSource);
                            }

                            PlayerControlsHandler playerControlsRegistry = new PlayerControlsHandler(audioSourceHandler);
                            event.getApi().addMessageComponentCreateListener(playerControlsRegistry);

                            playerManager.loadItem(videoLink, audioSourceHandler);

                        } else if (voiceConnectionState.equals(Main.VoiceConnection.AlreadyConnected)) {

                            if (this.audioSourceHandler.audioSource.scheduler.player.getPlayingTrack() == null) {
                                // Load the new track
                                playerManager.loadItem(videoLink, this.audioSourceHandler);
                            } else {
                                // Queue the new track
                                AudioItem newTrackItem = youtubeSourceManager.loadTrackWithVideoId(videoId, true);
                                if (newTrackItem.equals(AudioReference.NO_TRACK)) {
                                    slashCommandInteraction.createImmediateResponder().addEmbed(new EmbedBuilder()
                                                    .setTitle("Requested video/song not found!")
                                                    .setDescription("Make sure the video being searched is public")
                                                    .setColor(Main.botAccentColor)
                                                    .setThumbnail(Main.botIconURL))
                                            .respond()
                                            .exceptionally(exception -> {
                                                Main.logger.error("Unable to respond to the slash command!");
                                                Main.logger.error(exception.getMessage());
                                                return null;
                                            });
                                } else {
                                    if (newTrackItem instanceof AudioTrack newTrack) {
                                        this.audioSourceHandler.audioSource.scheduler.queue(newTrack);

                                        String embedDescription;
                                        int queueSize = this.audioSourceHandler.audioSource.scheduler.getQueueSize();
                                        if (queueSize == 1) {
                                            embedDescription = String.format("Currently %d track in queue\n Click the button below to see the full queue", queueSize);
                                        } else {
                                            embedDescription = String.format("Currently %d tracks in queue\n Click the button below to see the full queue", queueSize);
                                        }
                                        slashCommandInteraction.createImmediateResponder().addEmbed(new EmbedBuilder()
                                                        .setTitle(String.format("Added to Queue - %s", newTrack.getInfo().title))
                                                        .setDescription(embedDescription)
                                                        .setColor(Main.botAccentColor)
                                                        .setThumbnail(Main.botIconURL))
                                                .addComponents(this.audioSourceHandler.audioSource.scheduler.actionRow)
                                                .respond()
                                                .exceptionally(exception -> {
                                                    Main.logger.error("Unable to respond to the guild command!");
                                                    Main.logger.error(exception.getMessage());
                                                    return null;
                                                });
                                    }
                                }
                            }
                        }
                    }
                } else if (requestedAudioSource.get().equalsIgnoreCase("soundcloud")) {

                } else {

                }
            }
        }


    }
}
