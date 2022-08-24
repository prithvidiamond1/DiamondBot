package org.prithvidiamond1.Commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
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
import org.javacord.api.interaction.*;
import org.prithvidiamond1.AudioPlayer.AudioSystemMessenger;
import org.prithvidiamond1.AudioPlayer.PlayerControlsListener;
import org.prithvidiamond1.AudioPlayer.Sources.Youtube.YoutubeSourceEngine;
import org.prithvidiamond1.AudioPlayer.TrackQueuer.AudioTrackQueuer;
import org.prithvidiamond1.AudioPlayer.VoiceConnectionConstants;
import org.prithvidiamond1.BotConstants;
import org.prithvidiamond1.DB.Repositories.ServerRepository.ServerRepository;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * This class contains the actions of the play command
 * <br>
 * Plays audio from a requested audio source
 */
@Component
public class PlayCommand extends BaseCommand {

    private final String name = "play";

    private final String description = "A command to play music. *_This command takes arguments_*";

    private final List<SlashCommandOption> slashCommandOptions = new ArrayList<>();

    private final ServerRepository serverRepository;

    private final ApplicationContext appContext;

    private ServerVoiceChannel voiceChannel;

    private VoiceConnectionConstants.VoiceChannelConnectionStatus channelConnectionStatus = VoiceConnectionConstants.VoiceChannelConnectionStatus.ERROR_DURING_CONNECTION;

    public PlayCommand(Logger logger, ServerRepository serverRepository, ApplicationContext appContext){
        super(logger);
        this.serverRepository = serverRepository;
        this.appContext = appContext;

        slashCommandOptions.add(SlashCommandOption.createWithChoices(
                SlashCommandOptionType.STRING,
                "audio-source",
                "Name of the audio source to be searched",
                true,
                List.of(SlashCommandOptionChoice.create("YouTube", "youtube"))
        ));
        slashCommandOptions.add(SlashCommandOption.create(
                SlashCommandOptionType.STRING,
                "search-string",
                "String to be searched using the set audio source",
                true
        ));
    }

    private void setVoiceChannel(ServerVoiceChannel voiceChannel){
        this.voiceChannel = voiceChannel;
    }

    @Bean
    public ServerVoiceChannel getVoiceChannel(){
        if (this.voiceChannel != null){
            return this.voiceChannel;
        } else {
            throw new NullPointerException("voiceChannel is currently null");
        }
    }

    /**
     * Method that finds a suitable server voice channel to join to
     * @param api the {@link DiscordApi} instance
     * @param user the user calling the play command
     * @param server the server in which the play command got invoked
     * @return a {@link VoiceConnectionConstants.VoiceChannelConnectionStatus}
     */
    private ServerVoiceChannel findVoiceChannel(DiscordApi api, User user, Server server){
        ServerVoiceChannel freeVoiceChannel = null;
        long userId = user.getId();
        if (server.getConnectedVoiceChannel(userId).isPresent()) {
            getLogger().info(String.format("Bot connection status: Is connected = %b",
                    server.getConnectedVoiceChannel(userId).get().isConnected(api.getYourself())));
            if (server.getConnectedVoiceChannel(userId).get().isConnected(api.getYourself())) {
                this.channelConnectionStatus = VoiceConnectionConstants.VoiceChannelConnectionStatus.ALREADY_JOINED_VOICE_CHANNEL;
            } else {
                freeVoiceChannel = server.getConnectedVoiceChannel(userId).get();
                this.channelConnectionStatus = VoiceConnectionConstants.VoiceChannelConnectionStatus.JOIN_VOICE_CHANNEL;
            }
        } else {
            List<ServerVoiceChannel> voiceChannels = server.getVoiceChannels();
            if (!voiceChannels.isEmpty()) {
                boolean emptyVoiceChannelPresent = false;
                for (ServerVoiceChannel voiceChannel : voiceChannels) {
                    if (voiceChannel.getConnectedUsers().isEmpty()) {
                        emptyVoiceChannelPresent = true;
                        freeVoiceChannel = voiceChannel;
                        this.channelConnectionStatus = VoiceConnectionConstants.VoiceChannelConnectionStatus.JOIN_VOICE_CHANNEL;
                    } else {
                        getLogger().info(String.format("Bot connection status: Is connected = %b",
                                voiceChannel.isConnected(api.getYourself())));
                        if (voiceChannel.isConnected(api.getYourself())) {
                            this.channelConnectionStatus = VoiceConnectionConstants.VoiceChannelConnectionStatus.ALREADY_JOINED_VOICE_CHANNEL;
                        }
                    }
                }
                if (!emptyVoiceChannelPresent) {
                    this.channelConnectionStatus = VoiceConnectionConstants.VoiceChannelConnectionStatus.NO_FREE_VOICE_CHANNELS;
                }
            } else {
                this.channelConnectionStatus = VoiceConnectionConstants.VoiceChannelConnectionStatus.NO_ACCESSIBLE_VOICE_CHANNELS;
            }
        }
        return freeVoiceChannel;
    }

    /**
     * Method that connects the bot to voice channel using an audio source
     * @param voiceChannel the voice channel to connect to
     * @param audioSource the audio source
     */
    private void connectToSource(ServerVoiceChannel voiceChannel, AudioSource audioSource) {
        voiceChannel.connect()
                .thenAccept(audioConnection -> audioConnection.setAudioSource(audioSource))
                .exceptionally(exception -> {
                    getLogger().error("Unexpected error trying to play the requested audio!");
                    getLogger().error(exception.getMessage());
                    return null;
                });
    }

    private EmbedBuilder commandFunction(User user, TextChannel textChannel, Server server, String sourceName, String searchString){
        DiscordApi api = appContext.getBean(DiscordApi.class);
        ServerVoiceChannel freeVoiceChannel = findVoiceChannel(api, user, server);
        this.setVoiceChannel(freeVoiceChannel);

        AudioSystemMessenger audioSystemMessenger = appContext.getBean(AudioSystemMessenger.class);
        AudioPlayerManager playerManager = appContext.getBean(AudioPlayerManager.class);
        AudioSource audioSource = appContext.getBean(AudioSource.class);
        AudioLoadResultHandler audioLoadResultHandler = appContext.getBean(AudioLoadResultHandler.class);
        AudioSourceManager sourceManager = null;
        String sourceLink = null;

         switch (sourceName.toLowerCase()) {
            case "youtube" -> {
                sourceManager = appContext.getBean("youtubeAudioSourceManager", AudioSourceManager.class);
                YoutubeSourceEngine youtubeSourceEngine = appContext.getBean(YoutubeSourceEngine.class);
                PlayerControlsListener controlsListener = appContext.getBean(PlayerControlsListener.class);
                controlsListener.setCurrentAudioSourceEngine(youtubeSourceEngine);
                sourceLink = youtubeSourceEngine.fetchSourceLink(searchString);
            }
        }

        playerManager.registerSourceManager(sourceManager);

        if (this.channelConnectionStatus.getConnectionStatus().equals(VoiceConnectionConstants.VoiceConnectionStatus.SUCCESSFUL)) {
            connectToSource(freeVoiceChannel, audioSource);
            playerManager.loadItem(sourceLink, audioLoadResultHandler);
        } else if (this.channelConnectionStatus.getConnectionStatus().equals(VoiceConnectionConstants.VoiceConnectionStatus.ALREADY_CONNECTED)){
            AudioPlayer audioPlayer = this.appContext.getBean(AudioPlayer.class);

            if (audioPlayer.getPlayingTrack() == null){
                playerManager.loadItem(sourceLink, audioLoadResultHandler);
            } else {
                AudioItem newTrackItem = null;

                if (sourceManager instanceof YoutubeAudioSourceManager) {
                    YoutubeAudioSourceManager youtubeSourceManager = (YoutubeAudioSourceManager) sourceManager;
                    YoutubeSourceEngine youtubeSourceEngine = appContext.getBean(YoutubeSourceEngine.class);
                    newTrackItem = youtubeSourceManager.loadTrackWithVideoId(youtubeSourceEngine.fetchSourceId(searchString), false);
                }

                assert newTrackItem != null;
                if (newTrackItem.equals(AudioReference.NO_TRACK)) {
                    // Handle this
                } else {
                    if (newTrackItem instanceof AudioTrack newTrack) {
                        // Notify users that the track is going to be added.
                        audioSystemMessenger.addingTrackToQueueMessage(textChannel, newTrack);

                        // Adding track to queue
                        AudioTrackQueuer trackQueuer = this.appContext.getBean(AudioTrackQueuer.class);
                        trackQueuer.addTrackToTheBack(newTrack);
                    }
                }
            }
        }

        return audioSystemMessenger.joiningVoiceChannelMessage(this.channelConnectionStatus, freeVoiceChannel);
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
     * the guild version of the play command
     * @param event the guild command trigger event
     */
    @Override
    public void runCommand(MessageCreateEvent event) {
        TextChannel textChannel = event.getChannel();
        String[] commandArgs = event.getMessage().getContent().split(" ");
        getLogger().info(String.format("No of elements in commandArgs: %d", commandArgs.length));
        getLogger().info(String.format("commandArgs: %s", Arrays.toString(commandArgs)));
        if (commandArgs.length > 2 && Arrays.asList(BotConstants.audioSources).contains(commandArgs[1].toLowerCase())) {
            getLogger().info("Guild play command has received the correct number of args including a valid source");
            if (commandArgs[2].equals("\"\"")) {
                getLogger().info("Guild play command has received an empty search string as one of its arguments");
                new MessageBuilder().addEmbed(new EmbedBuilder()
                                .setTitle("Empty search string!")
                                .setDescription("Please provide a non-empty search string to find a playable source")
                                .setColor(BotConstants.botAccentColor)
                                .setThumbnail(BotConstants.botIconURL))
                        .send(textChannel)
                        .exceptionally(exception -> {
                            getLogger().error("Unable to respond to the guild command!");
                            getLogger().error(exception.getMessage());
                            return null;
                        });
            } else {
                String source = commandArgs[1].toLowerCase();
                String searchString = parseCommandArgs(commandArgs);
                EmbedBuilder response = commandFunction(event.getMessageAuthor().asUser().orElse(null),
                        textChannel, event.getServer().orElse(null),
                        source, searchString);
                new MessageBuilder().addEmbed(response)
                        .send(textChannel)
                        .exceptionally(exception -> {
                            getLogger().error("Unable to respond to the guild command!");
                            getLogger().error(exception.getMessage());
                            return null;
                        });
            }
        } else {
            if (commandArgs.length == 1){
                String prefix = this.serverRepository.resolveServerModelById(Objects.requireNonNull(event.getServer().orElse(null))).getGuildPrefix();
                getLogger().info("Showing command help message");
                new MessageBuilder().addEmbed(new EmbedBuilder()
                                .setTitle("Play Command")
                                .setDescription(String.format("To use the play command, type **%splay <source> \"<search string>\"**\nSupported sources currently include: **%s**", prefix, Arrays.toString(BotConstants.audioSources).replaceAll("[\\[\\]]", "")))
                                .setColor(BotConstants.botAccentColor)
                                .setThumbnail(BotConstants.botIconURL))
                        .send(textChannel)
                        .exceptionally(exception -> {
                            getLogger().error("Unable to respond to the guild command!");
                            getLogger().error(exception.getMessage());
                            return null;
                        });
            } else {
                if (!Arrays.asList(BotConstants.audioSources).contains(commandArgs[1].toLowerCase())) {
                    getLogger().info("Guild play command has not received a matching source as one of its arguments!");
                    new MessageBuilder().addEmbed(new EmbedBuilder()
                                    .setTitle("No matching sources!")
                                    .setDescription(String.format("Currently the only sources supported are: **%s**", Arrays.toString(BotConstants.audioSources).replaceAll("[\\[\\]]", "")))
                                    .setColor(BotConstants.botAccentColor)
                                    .setThumbnail(BotConstants.botIconURL))
                            .send(textChannel)
                            .exceptionally(exception -> {
                                getLogger().error("Unable to respond to the guild command!");
                                getLogger().error(exception.getMessage());
                                return null;
                            });
                } else {
                    getLogger().info("Guild play command has not received a search string as one of its arguments!");
                    new MessageBuilder().addEmbed(new EmbedBuilder()
                                    .setTitle("Missing search string argument!")
                                    .setDescription("Along with the play command and it's other arguments, pass in a search string surrounded in double quotes as shown: **\"<search string>\"**")
                                    .setColor(BotConstants.botAccentColor)
                                    .setThumbnail(BotConstants.botIconURL))
                            .send(textChannel)
                            .exceptionally(exception -> {
                                getLogger().error("Unable to respond to the guild command!");
                                getLogger().error(exception.getMessage());
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
        TextChannel textChannel = slashCommandInteraction.getChannel().orElse(null);
        List<SlashCommandInteractionOption> commandArgs = slashCommandInteraction.getOptions();
        Optional<String> requestedAudioSource = commandArgs.get(0).getStringValue();
        if (requestedAudioSource.isPresent() && Arrays.asList(BotConstants.audioSources).contains(requestedAudioSource.get().toLowerCase())) {
            getLogger().info("Slash play command has received a matching source as its arguments");
            Optional<String> searchStringOptional = commandArgs.get(1).getStringValue();
            if (searchStringOptional.isPresent()) {
                String source = requestedAudioSource.get().toLowerCase();
                String searchString = searchStringOptional.get();
                EmbedBuilder response = commandFunction(slashCommandInteraction.getUser(),
                        textChannel, slashCommandInteraction.getServer().orElse(null),
                        source, searchString);
                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(response)
                        .respond()
                        .exceptionally(exception -> {
                            getLogger().error("Unable to respond to the slash command!");
                            getLogger().error(exception.getMessage());
                            return null;
                        });
            }
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public List<SlashCommandOption> getSlashCommandOptions() {
        return this.slashCommandOptions;
    }
}
