package me.kubbidev.nexuspowered.util.annotation;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

@NotNull
@Documented
@Target({
        ElementType.TYPE,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNullByDefault {

}