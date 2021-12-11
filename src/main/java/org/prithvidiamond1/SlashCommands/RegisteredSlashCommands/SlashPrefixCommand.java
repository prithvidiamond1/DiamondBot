package org.prithvidiamond1.SlashCommands.RegisteredSlashCommands;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.prithvidiamond1.DB.Models.DiscordServer;
import org.prithvidiamond1.Main;
import org.prithvidiamond1.SlashCommands.SlashCommandInterface;

import static org.prithvidiamond1.ServerHelperFunctions.resolveServerModelById;

/**
 * This class contains the actions of the prefix slash command
 */
public class SlashPrefixCommand implements SlashCommandInterface {
    /**
     * Method that contains and runs the actions of the prefix slash command
     * @param event the event that called the command
     */
    @Override
    public void runCommand(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        User commandUser = slashCommandInteraction.getUser();
        slashCommandInteraction.getServer().ifPresent(server -> {
            if (!commandUser.isBot() && server.isAdmin(commandUser)) {
                DiscordServer serverModel = resolveServerModelById(server);
                slashCommandInteraction.getOptionByName("prefix-string").flatMap(SlashCommandInteractionOption::getStringValue).ifPresent(newPrefix -> {
                    serverModel.setGuildPrefix(newPrefix);
                    Main.discordServerRepository.save(serverModel);
                    slashCommandInteraction.createImmediateResponder().addEmbed(
                                    new EmbedBuilder()
                                            .setTitle("Guild Command Prefix Changed!")
                                            .setDescription(String.format("Guild prefix has been set to **%s**", newPrefix))
                                            .setColor(Main.botAccentColor)).respond()
                            .exceptionally(exception -> {
                                exception.printStackTrace();
                                return null;
                            });
                });
            } else {
                slashCommandInteraction.createImmediateResponder().addEmbed(
                        new EmbedBuilder()
                                .setTitle("You cannot change guild command prefixes for this server!")
                                .setDescription("You do not have the required permissions for this action! Contact a server admin and request for a change")
                                .setColor(Main.botAccentColor)).respond()
                        .exceptionally(exception -> {
                                    exception.printStackTrace();
                                    return null;
                                });
            }
        });
    }
}
