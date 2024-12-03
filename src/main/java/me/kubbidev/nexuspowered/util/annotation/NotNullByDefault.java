package me.kubbidev.nexuspowered.util.annotation;

import java.lang.annotation.*;

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