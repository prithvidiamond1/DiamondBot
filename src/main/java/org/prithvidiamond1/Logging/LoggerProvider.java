package org.prithvidiamond1.Logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LoggerProvider {
    @Bean
    public static Logger generateLogger(){
        var callingClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(stream -> {
                    var list = stream.toList();
                    return list.get(list.size() - 1);
                });
        return LoggerFactory.getLogger(callingClass.getClass());
    }
}
