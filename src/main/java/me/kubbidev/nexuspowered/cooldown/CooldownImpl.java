package me.kubbidev.nexuspowered.cooldown;

import com.google.gson.JsonElement;
import me.kubbidev.nexuspowered.gson.JsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

@NotNullByDefault
class CooldownImpl implements Cooldown {

    // when the last test occurred.
    private long lastTested;

    // the cooldown duration in millis
    private final long timeout;

    CooldownImpl(long amount, TimeUnit unit) {
        this.timeout = unit.toMillis(amount);
        this.lastTested = 0;
    }

    @Override
    public @NotNull OptionalLong getLastTested() {
        return this.lastTested == 0 ? OptionalLong.empty() : OptionalLong.of(this.lastTested);
    }

    @Override
    public void setLastTested(long time) {
        if (time <= 0) {
            this.lastTested = 0;
        } else {
            this.lastTested = time;
        }
    }

    @Override
    public long getTimeout() {
        return this.timeout;
    }

    @Override
    public @NotNull CooldownImpl copy() {
        return new CooldownImpl(this.timeout, TimeUnit.MILLISECONDS);
    }

    @NotNull
    @Override
    public JsonElement serialize() {
        return JsonBuilder.object()
                .add("lastTested", lastTested)
                .add("timeout", timeout)
                .build();
    }
}