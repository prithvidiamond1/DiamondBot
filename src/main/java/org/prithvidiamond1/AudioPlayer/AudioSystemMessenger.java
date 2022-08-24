package org.prithvidiamond1.AudioPlayer;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.prithvidiamond1.BotConstants;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class AudioSystemMessenger implements MessageCreateListener {
    private final Logger logger;

    private Message lastMessageWithComponents;

    public AudioSystemMessenger(Logger logger) {
        this.logger = logger;
    }

    public Message getLastMessageWithComponents() {
        return this.lastMessageWithComponents;
    }

    private void setLastMessageWithComponents(Message messageWithComponents){
        this.lastMessageWithComponents = messageWithComponents;
    }

    public EmbedBuilder joiningVoiceChannelMessage(VoiceConnectionConstants.VoiceChannelConnectionStatus channelConnectionStatus,
                                                   ServerVoiceChannel voiceChannel){
        EmbedBuilder embed = null;

        switch (channelConnectionStatus){
            case JOIN_VOICE_CHANNEL -> {
                embed = new EmbedBuilder()
                        .setTitle(String.format("Joining Voice Channel - %s", voiceChannel.getName()))
                        .setDescription("If you need me to join a different voice channel, join the voice channel before using the play command")
                        .setThumbnail(BotConstants.botIconURL)
                        .setColor(BotConstants.botAccentColor);
            } case ALREADY_JOINED_VOICE_CHANNEL -> {
                embed = new EmbedBuilder()
                        .setTitle(String.format("Already connected to Channel - %s", voiceChannel.getName()))
                        .setDescription("If you need me to join a different voice channel, join the voice channel before using the play command")
                        .setThumbnail(BotConstants.botIconURL)
                        .setColor(BotConstants.botAccentColor);
            } case NO_FREE_VOICE_CHANNELS -> {
                embed = new EmbedBuilder()
                        .setTitle("No free voice channels present in the server!")
                        .setDescription("If you think one of the voice channels in the server can be disturbed, join the channel and then try playing something")
                        .setThumbnail(BotConstants.botIconURL)
                        .setColor(BotConstants.botAccentColor);
            } case NO_ACCESSIBLE_VOICE_CHANNELS -> {
                embed = new EmbedBuilder()
                        .setTitle("No accessible voice channels found in the server!")
                        .setDescription("Add a voice channel to this server and then try playing something")
                        .setThumbnail(BotConstants.botIconURL)
                        .setColor(BotConstants.botAccentColor);
            } case ERROR_DURING_CONNECTION -> {
                embed = new EmbedBuilder()
                        .setTitle("Uh oh! That wasn't supposed to happen!")
                        .setDescription("If you are seeing this then something went wrong while trying to play your requested audio track. Try playing it again and if you see this message again, contact one of the developers on Github by creating an issue at [Diamond Bot Issues](https://github.com/prithvidiamond1/DiamondBot/issues)")
                        .setColor(BotConstants.botAccentColor)
                        .setThumbnail(BotConstants.botIconURL);
            }
        }

        return embed;
    }

    public void addingTrackToQueueMessage(TextChannel textChannel, AudioTrack newTrack){
        new MessageBuilder().addEmbed(
                new EmbedBuilder().setTitle("Adding New Track to Queue")
                .setDescription(String.format("Adding Track to Queue - %s", newTrack.getInfo().title))
                .setColor(BotConstants.botAccentColor)
                .setThumbnail(BotConstants.botIconURL))
                .send(textChannel).exceptionally(exception -> {
                    this.logger.error("Unable to respond to play command!");
                    this.logger.error(exception.getMessage());
                    return null;
                });
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (getLastMessageWithComponents() != null){
            //remove components from the message.
            getLastMessageWithComponents().createUpdater().removeAllComponents()
                    .applyChanges().exceptionally(exception -> {
                        this.logger.error("Unable to remove player controls from the last sent embed!");
                        this.logger.error(exception.getMessage());
                        return null;
                    });
        } else {
            throw new NullPointerException("getLastMessageWithComponents returned null!");
        }

        if (event.getMessageAuthor().isYourself()){
            if (!event.getMessage().getComponents().isEmpty()) {
                setLastMessageWithComponents(event.getMessage());
            }
        }
    }
}
