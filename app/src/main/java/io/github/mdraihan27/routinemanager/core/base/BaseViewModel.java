package io.github.mdraihan27.routinemanager.core.base;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class BaseViewModel<S extends UiState, E extends UiEvent> extends ViewModel {

    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<S> stateLiveData;

    public BaseViewModel(@NonNull S initialState) {
        this.stateLiveData = new MutableLiveData<>(initialState);
    }

    @NonNull
    public LiveData<S> getState() {
        return stateLiveData;
    }

    @NonNull
    protected S getCurrentState() {
        S current = stateLiveData.getValue();
        if (current == null) {
            throw new IllegalStateException();
        }
        return current;
    }

    protected void setState(@NonNull S newState) {
        if (android.os.Looper.getMainLooper() == android.os.Looper.myLooper()) {
            stateLiveData.setValue(newState);
        } else {
            stateLiveData.postValue(newState);
        }
    }

    protected void addDisposable(@NonNull Disposable disposable) {
        disposables.add(disposable);
    }

    public abstract void onEvent(@NonNull E event);

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}
