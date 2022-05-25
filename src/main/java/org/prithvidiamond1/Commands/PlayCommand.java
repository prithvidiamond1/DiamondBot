package org.prithvidiamond1.Commands;

import com.google.api.services.youtube.model.SearchResult;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
//import org.prithvidiamond1.AudioPlayer.ParsedEvent;
import org.prithvidiamond1.AudioPlayer.AudioSourceHandler;
import org.prithvidiamond1.AudioPlayer.PlayerAudioSource;
import org.prithvidiamond1.AudioPlayer.PlayerControlsHandler;
import org.prithvidiamond1.AudioPlayer.Youtube.YoutubeSearchEngine;
import org.prithvidiamond1.Main;
import org.testng.internal.collections.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PlayCommand implements Command {

    private ServerVoiceChannel serverVoiceChannel;
    private TextChannel textChannel;
    private AudioSourceHandler audioSourceHandler;
    private YoutubeAudioSourceManager youtubeSourceManager;

    private Pair<Main.VoiceConnectionStatus, EmbedBuilder> connectToSource(DiscordApi api, User user, Server server, AudioSource source) {
        Pair<Main.VoiceConnectionStatus, EmbedBuilder> result;
        EmbedBuilder response = null;
        Main.VoiceConnectionStatus voiceConnectionStatusState = Main.VoiceConnectionStatus.Unsuccessful;
        long senderId = user.getId();
        if (server.getConnectedVoiceChannel(senderId).isPresent()) {
            Main.logger.info(String.format("Bot connection status: Is connected = %b",
                    server.getConnectedVoiceChannel(senderId).get().isConnected(api.getYourself())));
            if (server.getConnectedVoiceChannel(senderId).get().isConnected(api.getYourself())) {
                voiceConnectionStatusState = Main.VoiceConnectionStatus.AlreadyConnected;
            } else {
                this.serverVoiceChannel = server.getConnectedVoiceChannel(senderId).get();
                serverVoiceChannel.connect()
                        .thenAccept(audioConnection -> audioConnection.setAudioSource(source))
                        .exceptionally(exception -> {
                            Main.logger.error("Unexpected error trying to play the requested audio!");
                            Main.logger.error(exception.getMessage());
                            return null;
                        });
                voiceConnectionStatusState = Main.VoiceConnectionStatus.Successful;
            }
        } else {
            List<ServerVoiceChannel> voiceChannels = server.getVoiceChannels();
            if (!voiceChannels.isEmpty()) {
                boolean emptyVoiceChannelPresent = false;
                for (ServerVoiceChannel voiceChannel : voiceChannels) {
                    if (voiceChannel.getConnectedUsers().isEmpty()) {
                        emptyVoiceChannelPresent = true;
                        this.serverVoiceChannel = voiceChannel;
                        this.serverVoiceChannel.connect().thenAccept(audioConnection -> audioConnection.setAudioSource(source))
                                .exceptionally(exception -> {
                                    Main.logger.error("Unexpected error trying to play the requested audio!");
                                    Main.logger.error(exception.getMessage());
                                    return null;
                                });
                        voiceConnectionStatusState = Main.VoiceConnectionStatus.Successful;
                    } else {
                        Main.logger.info(String.format("Bot connection status: Is connected = %b",
                                voiceChannel.isConnected(api.getYourself())));
                        if (voiceChannel.isConnected(api.getYourself())) {
                            voiceConnectionStatusState = Main.VoiceConnectionStatus.AlreadyConnected;
                        }
                    }
                }
                if (!emptyVoiceChannelPresent) {
                    response = new EmbedBuilder()
                            .setTitle("No free voice channels present in the server!")
                            .setDescription("If you think one of the voice channels in the server can be disturbed, join the channel and then try playing something")
                            .setThumbnail(Main.botIconURL)
                            .setColor(Main.botAccentColor);
                }
            } else {
                response = new EmbedBuilder()
                        .setTitle("No accessible voice channels found in the server!")
                        .setDescription("Add a voice channel to this server and then try playing something")
                        .setThumbnail(Main.botIconURL)
                        .setColor(Main.botAccentColor);
            }
        }
//        }

        result = new Pair<>(voiceConnectionStatusState, response);

        return result;
    }

    private String parseCommandArgs(String[] commandArgs){
        StringBuilder searchString = new StringBuilder();

        String[] searchArgs = Arrays.copyOfRange(commandArgs, 2, commandArgs.length);
        for (String arg: searchArgs){
            searchString.append(arg);
            searchString.append(" ");
        }

        return searchString.toString().strip().replaceAll("\"", "");
    }

    private String fetchYoutubeSourceById(String searchString){
        YoutubeSearchEngine youtubeSearch = new YoutubeSearchEngine();
        SearchResult topResult = youtubeSearch.getBestSearchResult(searchString);
        return topResult.getId().getVideoId();

    }

    private EmbedBuilder commandFunction(DiscordApi api, User user, Server server, String source, String searchString){
        EmbedBuilder functionResponse = null;

        AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
        String audioSourceLink = null;

        if (source.equalsIgnoreCase("youtube")){
            audioSourceLink = String.format("https://www.youtube.com/watch?v=%s", fetchYoutubeSourceById(searchString));

            this.youtubeSourceManager = new YoutubeAudioSourceManager();
            audioPlayerManager.registerSourceManager(this.youtubeSourceManager);
        }

        PlayerAudioSource playerAudioSource = new PlayerAudioSource(api, this.textChannel, this.serverVoiceChannel, audioPlayerManager);
        Pair<Main.VoiceConnectionStatus, EmbedBuilder> connectionResponse = connectToSource(api, user, server, playerAudioSource);
        Main.VoiceConnectionStatus voiceConnectionStatus = connectionResponse.first();
        Main.logger.info(String.format("VoiceConnectionStatus = %s", voiceConnectionStatus.toString()));

        if (voiceConnectionStatus.equals(Main.VoiceConnectionStatus.Successful)){
            this.audioSourceHandler = new AudioSourceHandler(playerAudioSource);
            PlayerControlsHandler playerControlsHandler = new PlayerControlsHandler(audioSourceHandler);
            api.addMessageComponentCreateListener(playerControlsHandler);

            audioPlayerManager.loadItem(audioSourceLink, audioSourceHandler);

        } else if (voiceConnectionStatus.equals(Main.VoiceConnectionStatus.AlreadyConnected)) {
            if (this.audioSourceHandler.playerAudioSource.audioPlayer.getPlayingTrack() == null) {
                // Load the new track
                audioPlayerManager.loadItem(audioSourceLink, this.audioSourceHandler);
            } else {
                // Queue the new track
                AudioItem newTrackItem = youtubeSourceManager.loadTrackWithVideoId(fetchYoutubeSourceById(searchString), true);
                if (newTrackItem.equals(AudioReference.NO_TRACK)) {
                    functionResponse = new EmbedBuilder()
                                    .setTitle("Requested video/song not found!")
                                    .setDescription("Make sure the video being searched is public")
                                    .setColor(Main.botAccentColor)
                                    .setThumbnail(Main.botIconURL);
                } else {
                    if (newTrackItem instanceof AudioTrack newTrack) {
                        this.audioSourceHandler.playerAudioSource.trackScheduler.queue(newTrack);

                        String embedDescription;
                        int queueSize = this.audioSourceHandler.playerAudioSource.trackScheduler.getQueueSize();
                        if (queueSize == 1) {
                            embedDescription = String.format("Currently %d track in queue\n Click the button below to see the full queue", queueSize);
                        } else {
                            embedDescription = String.format("Currently %d tracks in queue\n Click the button below to see the full queue", queueSize);
                        }
                         functionResponse = new EmbedBuilder()
                                        .setTitle(String.format("Added to Queue - %s", newTrack.getInfo().title))
                                        .setDescription(embedDescription)
                                        .setColor(Main.botAccentColor)
                                        .setThumbnail(Main.botIconURL);
                    }
                }
            }
        }

        if (functionResponse == null){
            functionResponse = connectionResponse.second();
        }

        return functionResponse;
    }

    @Override
    public void runCommand(MessageCreateEvent event) {
        this.textChannel = event.getChannel();
        String[] commandArgs = event.getMessage().getContent().split(" ");
        Main.logger.info(String.format("No of elements in commandArgs: %d", commandArgs.length));
        Main.logger.info(String.format("commandArgs: %s", Arrays.toString(commandArgs)));
        if (commandArgs.length > 2) {
            Main.logger.info("phase 1 - works");
            if (Arrays.stream(Main.audioSources).anyMatch(source -> source.equals(commandArgs[1].toLowerCase()))) {
                Main.logger.info("phase 2 - works");
                String source = commandArgs[1].toLowerCase();
                String searchString = parseCommandArgs(commandArgs);
                EmbedBuilder response = commandFunction(event.getApi(),
                        event.getMessageAuthor().asUser().orElse(null),
                        event.getServer().orElse(null), source, searchString);
                new MessageBuilder().addEmbed(response)
                        .send(this.textChannel)
                        .exceptionally(exception -> {
                            Main.logger.error("Unable to respond to the guild command!");
                            Main.logger.error(exception.getMessage());
                            return null;
                        });
            }
        }
    }

    @Override
    public void runCommand(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        this.textChannel = slashCommandInteraction.getChannel().orElse(null);
        List<SlashCommandInteractionOption> commandArgs = slashCommandInteraction.getOptions();
        Optional<String> requestedAudioSource = commandArgs.get(0).getStringValue();
        if (requestedAudioSource.isPresent()) {
            if (Arrays.stream(Main.audioSources).anyMatch(source -> source.equals(requestedAudioSource.get().toLowerCase()))) {
                Main.logger.info("phase 1 - works");
                Optional<String> searchStringOptional = commandArgs.get(1).getStringValue();
                if (searchStringOptional.isPresent()){
                    String source = requestedAudioSource.get().toLowerCase();
                    String searchString = searchStringOptional.get();
                    EmbedBuilder response = commandFunction(event.getApi(),
                            slashCommandInteraction.getUser(),
                            slashCommandInteraction.getServer().orElse(null),
                            source, searchString);
                    slashCommandInteraction.createImmediateResponder()
                            .addEmbed(response)
                            .respond()
                            .exceptionally(exception -> {
                                Main.logger.error("Unable to respond to the slash command!");
                                Main.logger.error(exception.getMessage());
                                return null;
                            });
                }
            }
        }
    }
}
