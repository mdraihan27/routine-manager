package io.github.mdraihan27.routinemanager.feature.home.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dagger.hilt.android.AndroidEntryPoint;
import io.github.mdraihan27.routinemanager.core.base.BaseFragment;
import io.github.mdraihan27.routinemanager.databinding.FragmentHomeBinding;

@AndroidEntryPoint
public class HomeFragment extends BaseFragment<FragmentHomeBinding> {

    @NonNull
    @Override
    protected FragmentHomeBinding inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentHomeBinding.inflate(inflater, container, false);
    }
}
