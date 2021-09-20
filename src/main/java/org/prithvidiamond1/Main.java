package org.prithvidiamond1;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.awt.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        String botToken = "ODg4NDI0Mjk1NTczODg0OTc4.YUSfmg.1vJqhqQmDhG84k2EFX_OTUjY6uc";

        DiscordApi api = new DiscordApiBuilder().setToken(botToken).login().join();

       // SlashCommand pingCommand = SlashCommand.with("ping", "prints \"Henlo there!\"").createGlobal(api).join();
        //List<SlashCommand> commands = api.getGlobalSlashCommands().join();
        api.addMessageCreateListener(event ->
        {
            if (event.getMessageContent().equals("!ping"))
            {
                event.getChannel().sendMessage("Hello "+event.getMessageAuthor().getDisplayName()+"!");
            }
            else if(event.getMessageContent().equals("!gayrate"))
            {
                int rate=(int)(Math.random()*100+1);
                String gayness;
                if(rate<20)
                    gayness="https://img.buzzfeed.com/buzzfeed-static/static/2015-11/20/10/campaign_images/webdr12/worlds-leading-anxiety-expert-found-curing-people-2-8748-1448032226-1_dblbig.jpg";
                else if(20<rate && rate<50)
                    gayness="https://i.ytimg.com/vi/qO_Dk_Z2zRM/maxresdefault.jpg";
                else if(50<rate && rate<80)
                    gayness="https://i0.wp.com/www.culturesonar.com/wp-content/uploads/2021/06/drew-pisarra-book.jpg";
                else
                    gayness="https://i1.sndcdn.com/artworks-000655332292-x1ui3u-t500x500.jpg";
                new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setAuthor(event.getMessageAuthor()).setTitle("Gay Calculator")
                        .setDescription(event.getMessageAuthor().getDisplayName()+" is "+rate+"% gay")
                        .setThumbnail(gayness)
                        .setColor(new Color(60,220,255)))
                                .send(event.getChannel());

                //event.getChannel().sendMessage(event.getMessageAuthor().getDisplayName()+" is "+(int)(Math.random()*100+1)+"% gay");
            }
            else if(event.getMessageContent().equals("!simprate"))
            {
                int rate1=(int)(Math.random()*100+1);
                String simp;
                if(rate1<20)
                    simp="https://img.buzzfeed.com/buzzfeed-static/static/2015-11/20/10/campaign_images/webdr12/worlds-leading-anxiety-expert-found-curing-people-2-8748-1448032226-1_dblbig.jpg";
                else if(20<rate1 && rate1<50)
                    simp="https://i.ytimg.com/vi/qO_Dk_Z2zRM/maxresdefault.jpg";
                else if(50<rate1 && rate1<80)
                    simp="https://i0.wp.com/www.culturesonar.com/wp-content/uploads/2021/06/drew-pisarra-book.jpg";
                else
                    simp="https://i1.sndcdn.com/artworks-000655332292-x1ui3u-t500x500.jpg";
                new MessageBuilder().setEmbed(new EmbedBuilder()
                        .setAuthor(event.getMessageAuthor()).setTitle("Simp Calculator")
                        .setDescription(event.getMessageAuthor().getDisplayName()+" is "+rate1+"% simp")
                        .setThumbnail(gayness)
                        .setColor(new Color(60,220,255)))
                                .send(event.getChannel());

                //event.getChannel().sendMessage(event.getMessageAuthor().getDisplayName()+" is "+(int)(Math.random()*100+1)+"% gay");
            }
        });
        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
            if (slashCommandInteraction.getCommandName().equals("ping")){
                slashCommandInteraction.createImmediateResponder().setContent("Henlo there!").respond();
            }
        });


    }
}
