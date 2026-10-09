package io.github.mdraihan27.routinemanager.core.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

public abstract class BaseFragment<VB extends ViewBinding> extends Fragment {

    private VB binding;

    @NonNull
    protected abstract VB inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    @NonNull
    protected final VB getBinding() {
        if (binding == null) {
            throw new IllegalStateException();
        }
        return binding;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = inflateBinding(inflater, container);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Apply system bar insets to every fragment root automatically.
        // Stores initial padding so it can be restored cleanly on re-measure.
        final int initTop    = view.getPaddingTop();
        final int initBottom = view.getPaddingBottom();
        final int initLeft   = view.getPaddingLeft();
        final int initRight  = view.getPaddingRight();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                initLeft,
                initTop  + bars.top,
                initRight,
                initBottom + bars.bottom
            );
            return windowInsets;
        });
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
