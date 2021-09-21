package org.prithvidiamond1.EventListeners.EventImplement;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.prithvidiamond1.EventListeners.GayrateEvent;
import org.prithvidiamond1.Main;
import org.springframework.stereotype.Component;

@Component
public class GayrateEventImplement implements GayrateEvent {

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if(event.getMessageContent().equalsIgnoreCase("!gayrate")) {
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
                            .setColor(Main.botAccentColor))
                    .send(event.getChannel());
        }
    }
}
