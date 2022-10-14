package org.prithvidiamond1.AudioPlayer;

import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class VoiceChannelDisconnection {
    private final Logger logger;
    private final ApplicationContext appContext;
    private ScheduledExecutorService taskScheduler;

    public VoiceChannelDisconnection(Logger logger, ApplicationContext appContext, ScheduledExecutorService taskScheduler) {
        this.logger = logger;
        this.appContext = appContext;
        this.taskScheduler = taskScheduler;
    }

    // Use this when player is paused or track ends and when last track from the queue has finished playing
    public void startBotDisconnectTimer(ServerVoiceChannel voiceChannel){
        Runnable task = botDisconnectSequence(voiceChannel);
        ScheduledExecutorServiceFactory executorServiceFactory = this.appContext.getBean(ScheduledExecutorServiceFactory.class);
        if (this.taskScheduler.isShutdown()){
            this.taskScheduler = executorServiceFactory.generateTaskScheduler();
        }
        this.taskScheduler.schedule(task, 1, TimeUnit.MINUTES);
        this.logger.info("The bot disconnect scheduler has been reset and restarted");
    }

    //Use this when player is resumed or when a track is added to empty queue/everytime when a track starts playing
    public void stopBotDisconnectTimer(){
        this.taskScheduler.shutdownNow();
        if (this.taskScheduler.isShutdown()){
            this.logger.info("The bot disconnect scheduler has been shutdown intermittently and can be reset now");
        } else {
            this.logger.info("The bot disconnect scheduler has not been shutdown intermittently");
        }
    }

    private Runnable botDisconnectSequence(ServerVoiceChannel voiceChannel){
        return () -> {
            try {
                PlayerControlsListener playerControlsListener = this.appContext.getBean(PlayerControlsListener.class);
                voiceChannel.getApi().removeListener(playerControlsListener);
            } catch (Exception exception){
                this.logger.error("An error occurred when trying to remove the player controls handler");
                this.logger.error(exception.getMessage());
            }

            AudioSystemMessenger audioSystemMessenger = this.appContext.getBean(AudioSystemMessenger.class);
            if (audioSystemMessenger.getLastMessageWithComponents() != null){
                //remove components from the message
                audioSystemMessenger.getLastMessageWithComponents().createUpdater()
                        .removeAllComponents()
                        .applyChanges().exceptionally(exception -> {
                            this.logger.error("Unable to remove player controls from the last sent embed!");
                            this.logger.error(exception.getMessage());
                            return null;
                        });
            }

            voiceChannel.disconnect()
                    .exceptionally(exception -> {
                        this.logger.error("An error occurred when trying to disconnect from a voice channel");
                        this.logger.error(exception.getMessage());
                        return null;
                    });
        };
    }

    @Component
    public static class ScheduledExecutorServiceFactory{
        @Bean
        @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        public ScheduledExecutorService generateTaskScheduler(){
            return Executors.newScheduledThreadPool(1);
        }
    }

}
