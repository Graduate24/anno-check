package edu.tsinghua.demo.config;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
public class ValidatingComponent {
    public int validateNotNull(@NotNull String data) {
        return data.length();
    }

    public @NotNull
    String getNotNullableValue(@Nullable String input) {
        if (input != null) {
            return input.toUpperCase();
        }
        return null;
    }
}
