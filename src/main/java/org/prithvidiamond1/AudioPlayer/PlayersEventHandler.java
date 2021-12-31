package org.prithvidiamond1.AudioPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.*;

abstract public class PlayersEventHandler implements AudioEventListener {
    final private AudioPlayer player;

    public PlayersEventHandler(AudioPlayer player){
        this.player = player;
        this.player.addListener(this);
    }

    public AudioPlayer getPlayer() {
        return this.player;
    }

    @Override
    public void onEvent(AudioEvent event){
        if (event instanceof PlayerPauseEvent){

        } else if (event instanceof PlayerResumeEvent){

        } else if (event instanceof TrackStartEvent){

        } else if (event instanceof TrackEndEvent){

        }
    }
}
