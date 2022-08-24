package org.prithvidiamond1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * The main class of the Discord bot
 */
@EnableMongoRepositories
@SpringBootApplication
public class Main {
    private static ApplicationContext appContext;

    public static void main(String[] args) {
        appContext = SpringApplication.run(Main.class, args);
    }
    
    @Bean
    public ApplicationContext getAppContext(){
        return appContext;
    }
}
