package org.prithvidiamond1.Commands;

import com.google.api.services.youtube.model.SearchResult;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.prithvidiamond1.AudioPlayer.ParsedEvent;
import org.prithvidiamond1.AudioPlayer.PlayerAudioSource;
import org.prithvidiamond1.AudioPlayer.Youtube.YoutubeSearchEngine;
import org.prithvidiamond1.Main;
import org.testng.internal.collections.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PlayCommand implements Command {

    private Pair<Main.VoiceConnection, EmbedBuilder> connectToSource(DiscordApi api, User user, Server server, AudioSource source) {
        Pair<Main.VoiceConnection, EmbedBuilder> result = null;
        EmbedBuilder response = null;
        Main.VoiceConnection voiceConnectionState = Main.VoiceConnection.Unsuccessful;
//        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
//        long senderId = slashCommandInteraction.getUser().getId();
        long senderId = user.getId();
//        Optional<Server> serverOptional = slashCommandInteraction.getServer();
//        if (serverOptional.isPresent()) {
//            Server server = serverOptional.get();
        if (server.getConnectedVoiceChannel(senderId).isPresent()) {
            Main.logger.info(String.format("Bot connection status: Is connected = %b",
                    server.getConnectedVoiceChannel(senderId).get().isConnected(api.getYourself())));
            if (server.getConnectedVoiceChannel(senderId).get().isConnected(api.getYourself())) {
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
                        Main.logger.info(String.format("Bot connection status: Is connected = %b",
                                voiceChannel.isConnected(api.getYourself())));
                        if (voiceChannel.isConnected(api.getYourself())) {
                            voiceConnectionState = Main.VoiceConnection.AlreadyConnected;
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

        result = new Pair<>(voiceConnectionState, response);

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

    private void commandFunction(DiscordApi api, String source, String searchString){
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

        if (source.equalsIgnoreCase("youtube")){
            YoutubeSearchEngine youtubeSearch = new YoutubeSearchEngine();
            SearchResult topResult = youtubeSearch.getBestSearchResult(searchString);
            String videoId = topResult.getId().getVideoId();
            String videoLink = String.format("https://www.youtube.com/watch?v=%s", videoId);

            YoutubeAudioSourceManager youtubeSourceManager = new YoutubeAudioSourceManager();
            playerManager.registerSourceManager(youtubeSourceManager);
        }

        PlayerAudioSource playerAudioSource = new PlayerAudioSource(api, playerManager, new ParsedEvent(event));
        Main.VoiceConnection voiceConnectionState = connectToSource(event, playerAudioSource);
        Main.logger.info(String.format("VoiceConnectionStatus = %s", voiceConnectionState.toString()));


    }

    @Override
    public void runCommand(MessageCreateEvent event) {
        String[] commandArgs = event.getMessage().getContent().split(" ");
        Main.logger.info(String.format("No of elements in commandArgs: %d", commandArgs.length));
        Main.logger.info(String.format("commandArgs: %s", Arrays.toString(commandArgs)));
        if (commandArgs.length > 2) {
            Main.logger.info("phase 1 - works");
            if (Arrays.stream(Main.audioSources).anyMatch(source -> source.equals(commandArgs[1].toLowerCase()))) {
                Main.logger.info("phase 2 - works");
//                AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
                String source = commandArgs[1].toLowerCase();
                String searchString = parseCommandArgs(commandArgs);
            }
        }
    }

    @Override
    public void runCommand(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        List<SlashCommandInteractionOption> commandArgs = slashCommandInteraction.getOptions();
        Optional<String> requestedAudioSource = commandArgs.get(0).getStringValue();
        if (requestedAudioSource.isPresent()) {
            if (Arrays.stream(Main.audioSources).anyMatch(source -> source.equals(requestedAudioSource.get().toLowerCase()))) {
                Main.logger.info("phase 1 - works");
//                AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
                Optional<String> searchStringOptional = commandArgs.get(1).getStringValue();
                if (searchStringOptional.isPresent()){
                    String source = requestedAudioSource.get().toLowerCase();
                    String searchString = searchStringOptional.get();
                }
            }
        }
    }
}
