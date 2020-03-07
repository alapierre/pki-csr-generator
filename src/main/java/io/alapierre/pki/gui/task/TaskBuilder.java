package io.alapierre.pki.gui.task;

import javafx.concurrent.Service;

/**
 * @author Adrian Lapierre {@literal <alapierre@soft-project.pl>}
 * Copyright by Adrian Lapierre 2018
 */
public class TaskBuilder {

    /**
     * Uruchamia zadanie w tle na podstawie BaseTask. Błędy są obsługiwane przez BaseTask
     *
     * @param function funkcja wykonywana w tle
     * @see BaseTask
     */
    public static void backgroundJob(SimpleTaskExecutor function) {

        Service<Void> service = new Service<Void>() {
            @Override
            protected BaseTask<Void> createTask() {
                return new BaseTask<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        function.execute();
                        return null;
                    }
                };
            }
        };
        service.start();
    }

    /**
     * Uruchamia zadanie w tle na podstawie BaseTask z możliwością wykonania czynności po zakończeniu z powodzeniem.
     * Błędy są obsługiwane przez BaseTask.
     *
     * @param function funkcja wykonywana w tle
     * @param successor funkcja wykonywana po zakończeniu zadania
     */
    public static void backgroundJob(SimpleTaskExecutor function, Successor successor) {
        Service<Void> service = getVoidService(function, successor);
        service.start();
    }

    /**
     * Uruchamia zadanie w tle na podstawie BaseTask z możliwością wykonania czynności po zakończeniu z powodzeniem
     * oraz wykonania czynności po zakończeniu z błędem. Błędy są obsługiwane przez BaseTask
     *
     * @param function funkcja wykonywana w tle
     * @param successor funkcja wykonywana po zakończeniu zadania
     * @param fault funkcja wykonywana po błędzie, po obsłużeniu przez BaseTask.
     * @see BaseTask#failed()
     */
    public static void backgroundJob(SimpleTaskExecutor function, Successor successor, FaultCallback fault) {
        Service<Void> service = getVoidService(function, successor, fault);
        service.start();
    }

    protected static Service<Void> getVoidService(SimpleTaskExecutor function, Successor successor) {
        return new Service<Void>() {
            @Override
            protected BaseTask<Void> createTask() {
                return new BaseTask<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        function.execute();
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        successor.success();
                    }
                };
            }
        };
    }

    protected static Service<Void> getVoidService(SimpleTaskExecutor function, Successor successor, FaultCallback fault) {
        return new Service<Void>() {
            @Override
            protected BaseTask<Void> createTask() {
                return new BaseTask<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        function.execute();
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        successor.success();
                    }

                    @Override
                    protected void failed() {
                        super.failed();
                        fault.fault(getException());
                    }
                };
            }
        };
    }

}
