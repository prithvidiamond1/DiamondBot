package org.prithvidiamond1;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        String botToken = "ODg4NDI0Mjk1NTczODg0OTc4.YUSfmg.VY0mMvjB_Xoa_7wEyuoQUgip4QA";

        DiscordApi api = new DiscordApiBuilder().setToken(botToken).login().join();

        SlashCommand pingCommand = SlashCommand.with("ping", "prints \"Henlo there!\"").createGlobal(api).join();

        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
            if (slashCommandInteraction.getCommandName().equals("ping")){
                slashCommandInteraction.createImmediateResponder().setContent("Henlo there!").respond();
            }
        });

    }
}
