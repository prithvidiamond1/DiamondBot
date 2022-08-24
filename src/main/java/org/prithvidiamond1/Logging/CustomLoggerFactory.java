package org.prithvidiamond1.Logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CustomLoggerFactory {
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Logger generateLogger(InjectionPoint injectionPoint){
        return LoggerFactory.getLogger(
                Objects.requireNonNull(injectionPoint.getMethodParameter())
                        .getContainingClass()
        );
    }
}
