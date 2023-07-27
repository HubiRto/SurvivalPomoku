package pl.pomoku.survivalpomoku.myCommandLib;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
    String description();
}
