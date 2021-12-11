package org.prithvidiamond1.GuildCommands.RegisteredGuildCommands;

import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.prithvidiamond1.DB.Models.DiscordServer;
import org.prithvidiamond1.GuildCommands.GuildCommandInterface;
import org.prithvidiamond1.Main;

import java.util.Objects;

import static org.prithvidiamond1.ServerHelperFunctions.resolveServerModelById;

/**
 * This class contains the actions of the prefix guild command
 */
public class GuildPrefixCommand implements GuildCommandInterface {
    /**
     * Method that contains and runs the actions of the prefix guild command
     * @param event the event that called the command
     */
    @Override
    public void runCommand(MessageCreateEvent event) {
        DiscordServer serverModel = resolveServerModelById(Objects.requireNonNull(event.getServer().orElse(null)));

        MessageAuthor author = event.getMessageAuthor();
        String message = event.getMessageContent();
        String currentPrefix = serverModel.getGuildPrefix();

        String unvalidatedNewPrefix = message.substring((currentPrefix.length())+("prefix".length())).strip();

        boolean prefixIsValid = false;
        if (unvalidatedNewPrefix.length() != 0){
            prefixIsValid = ((unvalidatedNewPrefix.charAt(0)=='\"') && (unvalidatedNewPrefix.charAt(unvalidatedNewPrefix.length()-1)=='\"'));
        }

        if (!prefixIsValid) {
            new MessageBuilder().setEmbed(
                            new EmbedBuilder()
                                    .setTitle("Incorrect syntax for changing prefix!")
                                    .setDescription(String.format("Make sure to use the following syntax: %sprefix \"NEW_PREFIX\"", currentPrefix))
                                    .setColor(Main.botAccentColor))
                    .send(event.getChannel());
        }
        else{
            String newPrefix = unvalidatedNewPrefix.replaceAll("\"", "");
            if (newPrefix.isBlank()) {
                new MessageBuilder().setEmbed(
                                new EmbedBuilder()
                                        .setTitle("Blank text cannot be set as a prefix!")
                                        .setDescription("Make sure to set a prefix that is not blank by following the correct syntax")
                                        .setColor(Main.botAccentColor))
                        .send(event.getChannel());
            } else {
                if (author.isRegularUser() && author.isServerAdmin()) {
                    serverModel.setGuildPrefix(newPrefix);
                    Main.discordServerRepository.save(serverModel);
                    new MessageBuilder().setEmbed(
                                    new EmbedBuilder()
                                            .setTitle("Guild Command Prefix Changed!")
                                            .setDescription(String.format("Guild prefix has been set to **%s**", newPrefix))
                                            .setColor(Main.botAccentColor))
                            .send(event.getChannel());
                } else {
                    new MessageBuilder().setEmbed(
                                    new EmbedBuilder()
                                            .setTitle("You cannot change guild command prefixes for this server!")
                                            .setDescription("You do not have the required permissions for this action! Contact a server admin and request for a change")
                                            .setColor(Main.botAccentColor))
                            .send(event.getChannel());
                }
            }
        }
    }
}
