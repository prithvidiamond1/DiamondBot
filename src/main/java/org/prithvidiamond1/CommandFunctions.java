package org.prithvidiamond1;

import org.testng.internal.collections.Pair;

public class CommandFunctions {
    public static Pair<String, Integer> gayRate(){
        String gayness;
        int rate= randomRate();
        if(rate<20)
            gayness="https://img.buzzfeed.com/buzzfeed-static/static/2015-11/20/10/campaign_images/webdr12/worlds-leading-anxiety-expert-found-curing-people-2-8748-1448032226-1_dblbig.jpg";
        else if(20<rate && rate<50)
            gayness="https://i.ytimg.com/vi/qO_Dk_Z2zRM/maxresdefault.jpg";
        else if(50<rate && rate<80)
            gayness="https://i0.wp.com/www.culturesonar.com/wp-content/uploads/2021/06/drew-pisarra-book.jpg";
        else
            gayness="https://i1.sndcdn.com/artworks-000655332292-x1ui3u-t500x500.jpg";
        return new Pair<String, Integer>(gayness, rate);
    }

    public static int randomRate(){
        return (int)(Math.random()*100+1);
    }
}
