package org.prithvidiamond1.Commands;

import org.slf4j.Logger;

public abstract class BaseCommand implements Command{
    private final Logger logger;

    protected BaseCommand(Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger(){
        return this.logger;
    }
}
