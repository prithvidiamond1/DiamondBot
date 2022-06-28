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
import org.prithvidiamond1.AudioPlayer.AudioSourceHandler;
import org.prithvidiamond1.AudioPlayer.PlayerAudioSource;
import org.prithvidiamond1.AudioPlayer.PlayerControlsHandler;
import org.prithvidiamond1.AudioPlayer.Youtube.YoutubeSearchEngine;
import org.prithvidiamond1.Main;
import org.testng.internal.collections.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.prithvidiamond1.AudioPlayer.PlayerControlsHandler.playerActionRow;
import static org.prithvidiamond1.ServerHelperFunctions.resolveServerModelById;

/**
 * This class contains the actions of the play command
 * <br>
 * Plays audio from a requested audio source
 */
public class PlayCommand implements Command {

    private ServerVoiceChannel serverVoiceChannel;
    private TextChannel textChannel;
    private AudioSourceHandler audioSourceHandler;
    private YoutubeAudioSourceManager youtubeSourceManager;

    /**
     * Method that finds a suitable server voice channel to join to
     * @param api the {@link DiscordApi} instance
     * @param user the user calling the play command
     * @param server the server in which the play command got invoked
     * @return a {@link Pair} object containing a {@link org.prithvidiamond1.Main.VoiceConnectionStatus} and an {@link org.javacord.api.entity.message.embed.EmbedBuilder}
     */
    private Pair<Main.VoiceConnectionStatus, EmbedBuilder> findVoiceChannel(DiscordApi api, User user, Server server){
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

        if (voiceConnectionStatusState.equals(Main.VoiceConnectionStatus.Successful)){
            response = new EmbedBuilder()
                    .setTitle(String.format("Joining Voice Channel - %s", this.serverVoiceChannel.getName()))
                    .setDescription("If you need me to join a different voice channel, join the voice channel before using the play command")
                    .setThumbnail(Main.botIconURL)
                    .setColor(Main.botAccentColor);
        } else if (voiceConnectionStatusState.equals(Main.VoiceConnectionStatus.AlreadyConnected)) {
            response = new EmbedBuilder()
                    .setTitle(String.format("Already connected to Channel - %s", this.serverVoiceChannel.getName()))
                    .setDescription("If you need me to join a different voice channel, join the voice channel before using the play command")
                    .setThumbnail(Main.botIconURL)
                    .setColor(Main.botAccentColor);
        }



        result = new Pair<>(voiceConnectionStatusState, response);

        return result;
    }

    /**
     * Method that connects the bot to voice channel using an audio source
     * @param voiceChannel the voice channel to connect to
     * @param voiceConnectionStatus the status of the connection
     * @param source the audio source
     */
    private void connectToSource(ServerVoiceChannel voiceChannel, Main.VoiceConnectionStatus voiceConnectionStatus, AudioSource source) {
        if (voiceConnectionStatus.equals(Main.VoiceConnectionStatus.Successful)) {
            voiceChannel.connect()
                    .thenAccept(audioConnection -> audioConnection.setAudioSource(source))
                    .exceptionally(exception -> {
                        Main.logger.error("Unexpected error trying to play the requested audio!");
                        Main.logger.error(exception.getMessage());
                        return null;
                    });
        }
    }

    /**
     * Method to parse guild command arguments
     * @param commandArgs the command arguments
     * @return a list with all the parsed arguments
     */
    private String parseCommandArgs(String[] commandArgs){
        StringBuilder searchString = new StringBuilder();

        String[] searchArgs = Arrays.copyOfRange(commandArgs, 2, commandArgs.length);
        for (String arg: searchArgs){
            searchString.append(arg);
            searchString.append(" ");
        }

        return searchString.toString().strip().replaceAll("\"", "");
    }

    /**
     * Method to fetch a YouTube video's id by using search string
     * @param searchString the search string
     * @return the video's id as a string
     */
    private String fetchYoutubeSourceById(String searchString){
        YoutubeSearchEngine youtubeSearch = new YoutubeSearchEngine();
        SearchResult topResult = youtubeSearch.getBestSearchResult(searchString);
        return topResult.getId().getVideoId();

    }

    /**
     * Method that does the core function of the play command
     * @param api the {@link DiscordApi} instance
     * @param user the user that called the play command
     * @param server the server in which the play command was invoked
     * @param source the audio source to be played
     * @param searchString the search string associated with the audio source
     * @return an {@link EmbedBuilder} which is the response from this action
     */
    private EmbedBuilder commandFunction(DiscordApi api, User user, Server server, String source, String searchString){
        AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
        String audioSourceLink = null;

        if (source.equalsIgnoreCase("youtube")){
            audioSourceLink = String.format("https://www.youtube.com/watch?v=%s", fetchYoutubeSourceById(searchString));

            this.youtubeSourceManager = new YoutubeAudioSourceManager();
            audioPlayerManager.registerSourceManager(this.youtubeSourceManager);
        }


        Pair<Main.VoiceConnectionStatus, EmbedBuilder> connectionResponse = findVoiceChannel(api, user, server);
        Main.VoiceConnectionStatus voiceConnectionStatus = connectionResponse.first();
        EmbedBuilder functionResponse = connectionResponse.second();
        PlayerAudioSource playerAudioSource = new PlayerAudioSource(api, this.textChannel, this.serverVoiceChannel, audioPlayerManager);
        connectToSource(this.serverVoiceChannel, voiceConnectionStatus, playerAudioSource);
        Main.logger.info(String.format("VoiceConnectionStatus = %s", voiceConnectionStatus));

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
                AudioItem newTrackItem = null;
                if (source.equalsIgnoreCase("youtube")) {
                    newTrackItem = this.youtubeSourceManager.loadTrackWithVideoId(fetchYoutubeSourceById(searchString), true);
                } // check for other sources

                if (newTrackItem == null || newTrackItem.equals(AudioReference.NO_TRACK)) {
                    functionResponse = new EmbedBuilder()
                                    .setTitle("Requested video/song not found!")
                                    .setDescription("Make sure the video being searched is public")
                                    .setColor(Main.botAccentColor)
                                    .setThumbnail(Main.botIconURL);
                } else if (newTrackItem instanceof AudioTrack newTrack) {
                    functionResponse = new EmbedBuilder()
                            .setTitle("Adding New Track to Queue")
                            .setDescription(String.format("Adding Track to Queue - %s", newTrack.getInfo().title))
                            .setColor(Main.botAccentColor)
                            .setThumbnail(Main.botIconURL);

                    this.audioSourceHandler.playerAudioSource.trackScheduler.queue(newTrack);
                }
            }
        } else {
            // Contact me
            functionResponse = new EmbedBuilder()
                    .setTitle("Uh oh! That wasn't supposed to happen!")
                    .setDescription("If you are seeing this then something went wrong while trying to play your requested audio track. Try playing it again and if you see this message again, contact **Prithvi** if you know him personally and if not, send him an email at prithviraj645@gmail.com")
                    .setColor(Main.botAccentColor)
                    .setThumbnail(Main.botIconURL);
        }

        return functionResponse;
    }

    /**
     * the guild version of the play command
     * @param event the guild command trigger event
     */
    @Override
    public void runCommand(MessageCreateEvent event) {
        this.textChannel = event.getChannel();
        String[] commandArgs = event.getMessage().getContent().split(" ");
        Main.logger.info(String.format("No of elements in commandArgs: %d", commandArgs.length));
        Main.logger.info(String.format("commandArgs: %s", Arrays.toString(commandArgs)));
        if (commandArgs.length > 2 && Arrays.asList(Main.audioSources).contains(commandArgs[1].toLowerCase())) {
            Main.logger.info("Guild play command has received the correct number of args including a valid source");
            if (commandArgs[2].equals("\"\"")) {
                Main.logger.info("Guild play command has received an empty search string as one of its arguments");
                new MessageBuilder().addEmbed(new EmbedBuilder()
                                .setTitle("Empty search string!")
                                .setDescription("Please provide a non-empty search string to find a playable source")
                                .setColor(Main.botAccentColor)
                                .setThumbnail(Main.botIconURL))
                        .send(this.textChannel)
                        .exceptionally(exception -> {
                            Main.logger.error("Unable to respond to the guild command!");
                            Main.logger.error(exception.getMessage());
                            return null;
                        });
            } else {
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
        } else {
            if (commandArgs.length == 1){
                String prefix = resolveServerModelById(Objects.requireNonNull(event.getServer().orElse(null))).getGuildPrefix();
                Main.logger.info("Showing command help message");
                new MessageBuilder().addEmbed(new EmbedBuilder()
                                .setTitle("Play Command")
                                .setDescription(String.format("To use the play command, type **%splay <source> \"<search string>\"**\nSupported sources currently include: **%s**", prefix, Arrays.toString(Main.audioSources).replaceAll("[\\[\\]]", "")))
                                .setColor(Main.botAccentColor)
                                .setThumbnail(Main.botIconURL))
                        .send(this.textChannel)
                        .exceptionally(exception -> {
                            Main.logger.error("Unable to respond to the guild command!");
                            Main.logger.error(exception.getMessage());
                            return null;
                        });
            } else {
                if (!Arrays.asList(Main.audioSources).contains(commandArgs[1].toLowerCase())) {
                    Main.logger.info("Guild play command has not received a matching source as one of its arguments!");
                    new MessageBuilder().addEmbed(new EmbedBuilder()
                                    .setTitle("No matching sources!")
                                    .setDescription(String.format("Currently the only sources supported are: **%s**", Arrays.toString(Main.audioSources).replaceAll("[\\[\\]]", "")))
                                    .setColor(Main.botAccentColor)
                                    .setThumbnail(Main.botIconURL))
                            .send(this.textChannel)
                            .exceptionally(exception -> {
                                Main.logger.error("Unable to respond to the guild command!");
                                Main.logger.error(exception.getMessage());
                                return null;
                            });
                } else {
                    Main.logger.info("Guild play command has not received a search string as one of its arguments!");
                    new MessageBuilder().addEmbed(new EmbedBuilder()
                                    .setTitle("Missing search string argument!")
                                    .setDescription("Along with the play command and it's other arguments, pass in a search string surrounded in double quotes as shown: **\"<search string>\"**")
                                    .setColor(Main.botAccentColor)
                                    .setThumbnail(Main.botIconURL))
                            .send(this.textChannel)
                            .exceptionally(exception -> {
                                Main.logger.error("Unable to respond to the guild command!");
                                Main.logger.error(exception.getMessage());
                                return null;
                            });
                }
            }
        }
    }

    /**
     * the slash version of the play command
     * @param event the slash command trigger event
     */
    @Override
    public void runCommand(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        this.textChannel = slashCommandInteraction.getChannel().orElse(null);
        List<SlashCommandInteractionOption> commandArgs = slashCommandInteraction.getOptions();
        Optional<String> requestedAudioSource = commandArgs.get(0).getStringValue();
        if (requestedAudioSource.isPresent() && Arrays.asList(Main.audioSources).contains(requestedAudioSource.get().toLowerCase())) {
            Main.logger.info("Slash play command has received a matching source as its arguments");
            Optional<String> searchStringOptional = commandArgs.get(1).getStringValue();
            if (searchStringOptional.isPresent()) {
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
