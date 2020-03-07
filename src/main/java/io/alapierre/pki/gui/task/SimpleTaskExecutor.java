package io.alapierre.pki.gui.task;

/**
 * @author Adrian Lapierre {@literal <alapierre@soft-project.pl>}
 * Copyright by Adrian Lapierre 2018
 */
@FunctionalInterface
public interface SimpleTaskExecutor {

    /**
     * Gets a result.
     *
     * @return a result
     */
    void execute() throws Exception;
}
