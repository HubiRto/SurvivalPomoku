package pl.pomoku.survivalpomoku.utils.base64;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class Base64ConvertException extends Throwable {
    private final Exception exception;
    public Base64ConvertException(@NotNull Exception exception) {
        super(exception.getClass().getSimpleName() + ": " + exception.getMessage());
        this.exception = exception;
    }
}
