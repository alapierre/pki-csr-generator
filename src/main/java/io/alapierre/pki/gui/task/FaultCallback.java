package io.alapierre.pki.gui.task;

/**
 * @author Adrian Lapierre {@literal <al@alapierre.io>}
 * created 20.09.18
 */
@FunctionalInterface
public interface FaultCallback {
    void fault(Throwable ex);
}
