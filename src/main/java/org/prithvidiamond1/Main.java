package org.prithvidiamond1;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

public class Main {

    private static final Color botAccentColor = new Color(60,220,255);

    public static void main(String[] args) {

        // BOT_TOKEN = ODg4NDI0Mjk1NTczODg0OTc4.YUSfmg.1vJqhqQmDhG84k2EFX_OTUjY6uc
        String botToken = System.getenv().get("BOT_TOKEN");

        DiscordApi api = new DiscordApiBuilder().setToken(botToken).login().join();

        System.out.println("Bot has started!");

        // Commands
        helloUserCommand(api);
        gayrateCommand(api);
        simprateCommand(api);
    }

    /*
        Prithvi: eventually I will migrate these methods to a GuildCommands class
    */

    private static void helloUserCommand(DiscordApi api){
        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().equals("!ping"))
            {
                new MessageBuilder().setEmbed(new EmbedBuilder()
                            .setTitle("Hello "+event.getMessageAuthor().getDisplayName()+"!")
                            .setThumbnail(event.getMessageAuthor().getAvatar())
                            .setColor(botAccentColor))
                        .send(event.getChannel());
            }
        });
    }

    private static void gayrateCommand(DiscordApi api){
        api.addMessageCreateListener(event -> {
            if(event.getMessageContent().equals("!gayrate"))
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
                                .setColor(botAccentColor))
                        .send(event.getChannel());
            }
        });
    }

    private static void simprateCommand(DiscordApi api){
        api.addMessageCreateListener(event -> {
            if(event.getMessageContent().equals("!simprate"))
            {
                int rate=(int)(Math.random()*100+1);
                new MessageBuilder().setEmbed(new EmbedBuilder()
                                .setAuthor(event.getMessageAuthor()).setTitle("Simp Calculator")
                                .setDescription(event.getMessageAuthor().getDisplayName()+" is "+rate+"% simp")
                                .setColor(botAccentColor))
                        .send(event.getChannel());
            }
        });
    }
}
