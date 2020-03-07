package io.alapierre.pki.gui.task;

import javafx.concurrent.Task;

/**
 * @author Adrian Lapierre {@literal <alapierre@soft-project.pl>}
 */
public abstract class BaseTask<T> extends Task<T> {

    private String errorMessage = "";

    @Override
    protected void failed() {
        super.failed();
        Throwable ex = getException();
        ErrorDialog.showErrorDialog(ex, errorMessage);
        ex.printStackTrace();
    }

    public void execute() {
        Thread th = new Thread(this);
        th.start();
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

