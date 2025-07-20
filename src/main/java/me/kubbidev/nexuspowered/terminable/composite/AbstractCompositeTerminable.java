package me.kubbidev.nexuspowered.terminable.composite;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import me.kubbidev.nexuspowered.terminable.Terminable;

public class AbstractCompositeTerminable implements CompositeTerminable {

    private final Deque<AutoCloseable> closeables = new ConcurrentLinkedDeque<>();
    private       boolean              closed     = false;

    protected AbstractCompositeTerminable() {

    }

    @Override
    public CompositeTerminable with(AutoCloseable autoCloseable) {
        Objects.requireNonNull(autoCloseable, "autoCloseable");
        this.closeables.push(autoCloseable);
        return this;
    }

    @Override
    public void close() throws CompositeClosingException {
        List<Exception> caught = new ArrayList<>();
        for (AutoCloseable ac; (ac = this.closeables.poll()) != null; ) {
            try {
                ac.close();
            } catch (Exception e) {
                caught.add(e);
            }
        }
        this.closed = true;

        if (!caught.isEmpty()) {
            throw new CompositeClosingException(caught);
        }
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void cleanup() {
        this.closeables.removeIf(ac -> {
            if (!(ac instanceof Terminable)) {
                return false;
            }
            if (ac instanceof CompositeTerminable) {
                ((CompositeTerminable) ac).cleanup();
            }
            return ((Terminable) ac).isClosed();
        });
    }
}