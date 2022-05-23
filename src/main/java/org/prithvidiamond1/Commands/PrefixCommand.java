package org.prithvidiamond1.Commands;

import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.prithvidiamond1.DB.Models.DiscordServer;
import org.prithvidiamond1.Main;

import static org.prithvidiamond1.ServerHelperFunctions.resolveServerModelById;

public class PrefixCommand implements Command{

    private EmbedBuilder commandFunction(User user, Server server, String validatedPrefix){
        EmbedBuilder response;
        if (!user.isBot() && server.isAdmin(user)) {
            DiscordServer serverModel = resolveServerModelById(server);
            serverModel.setGuildPrefix(validatedPrefix);
            Main.discordServerRepository.save(serverModel);
            response = new EmbedBuilder()
                    .setTitle("Guild Command Prefix Changed!")
                    .setDescription(String.format("Guild prefix has been set to **%s**", validatedPrefix))
                    .setColor(Main.botAccentColor);
        } else {
            response = new EmbedBuilder()
                    .setTitle("You cannot change guild command prefixes for this server!")
                    .setDescription("You do not have the required permissions for this action! Contact a server admin and request for a change")
                    .setColor(Main.botAccentColor);
        }

        return response;
    }

    @Override
    public void runCommand(MessageCreateEvent event) {
        User author;
        Server server;
        MessageAuthor messageAuthor = event.getMessageAuthor();
        String message = event.getMessageContent();
        if (messageAuthor.asUser().isPresent()) {
            author = messageAuthor.asUser().get();
        } else {
            // throw an error and log it.
            throw new NullPointerException("Message author was null");
        }
        if (event.getServer().isPresent()){
            server = event.getServer().get();
        } else {
            // throw an error and log it.
            throw new NullPointerException("Server was null");
        }

        DiscordServer serverModel = resolveServerModelById(server);
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
                    .send(event.getChannel())
                    .exceptionally(exception -> {   // Error message for failing to respond to the guild command
                        Main.logger.error("Unable to respond to the guild command!");
                        Main.logger.error(exception.getMessage());
//                        exception.printStackTrace();
                        return null;
                    });
        } else{
            String newPrefix = unvalidatedNewPrefix.replaceAll("\"", "");
            if (newPrefix.isBlank()) {
                new MessageBuilder().setEmbed(
                                new EmbedBuilder()
                                        .setTitle("Blank text cannot be set as a prefix!")
                                        .setDescription("Make sure to set a prefix that is not blank by following the correct syntax")
                                        .setColor(Main.botAccentColor))
                        .send(event.getChannel())
                        .exceptionally(exception -> {   // Error message for failing to respond to the guild command
                            Main.logger.error("Unable to respond to the guild command!");
                            Main.logger.error(exception.getMessage());
//                            exception.printStackTrace();
                            return null;
                        });
            } else {
                EmbedBuilder response = commandFunction(author, server, newPrefix);
                new MessageBuilder().setEmbed(response)
                        .send(event.getChannel())
                        .exceptionally(exception -> { // Error message for failing to respond to the guild command
                            Main.logger.error("Unable to respond to the guild command!");
                            Main.logger.error(exception.getMessage());
//                                exception.printStackTrace();
                            return null;
                        });
            }
        }
    }

    @Override
    public void runCommand(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        User author = slashCommandInteraction.getUser();
        slashCommandInteraction.getServer()
                .ifPresent(server -> slashCommandInteraction.getOptionByName("prefix-string").flatMap(SlashCommandInteractionOption::getStringValue)
                        .ifPresent(newPrefix -> {
                            EmbedBuilder response = commandFunction(author, server, newPrefix);
                            slashCommandInteraction.createImmediateResponder().addEmbed(response)
                                    .respond()
                                    .exceptionally(exception -> {   // Error message for failing to respond to the slash command interaction
                                        Main.logger.error("Unable to respond to the slash command interaction");
                                        Main.logger.error(exception.getMessage());
//                                      exception.printStackTrace();
                                        return null;
                                    });
        }));
    }
}
