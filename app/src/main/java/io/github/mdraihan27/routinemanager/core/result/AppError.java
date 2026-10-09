package io.github.mdraihan27.routinemanager.core.result;

import androidx.annotation.NonNull;

public final class AppError {

    public enum Type {
        VALIDATION,
        NOT_FOUND,
        CONFLICT,
        GENERAL
    }

    private final Type type;
    private final String message;

    public AppError(@NonNull Type type, @NonNull String message) {
        this.type = type;
        this.message = message;
    }

    @NonNull
    public Type getType() {
        return type;
    }

    @NonNull
    public String getMessage() {
        return message;
    }
}
