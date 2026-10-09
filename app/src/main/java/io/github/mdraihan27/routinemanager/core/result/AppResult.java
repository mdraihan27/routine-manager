package io.github.mdraihan27.routinemanager.core.result;

import androidx.annotation.NonNull;

public abstract class AppResult<T> {

    AppResult() {
    }

    public abstract boolean isSuccess();

    public static final class Success<T> extends AppResult<T> {
        private final T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    public static final class Failure<T> extends AppResult<T> {
        private final AppError error;

        public Failure(@NonNull AppError error) {
            this.error = error;
        }

        @NonNull
        public AppError getError() {
            return error;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }
    }
}
