package me.kubbidev.nexuspowered.messaging.codec;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Message {

    /**
     * Gets the codec used by this message.
     *
     * @return the codec
     */
    Class<? extends Codec<?>> codec();

}