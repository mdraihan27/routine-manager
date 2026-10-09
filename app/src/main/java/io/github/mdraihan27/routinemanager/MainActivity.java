package io.github.mdraihan27.routinemanager;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.github.mdraihan27.routinemanager.core.background.BackgroundKeepAliveManager;
import io.github.mdraihan27.routinemanager.databinding.ActivityMainBinding;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject
    BackgroundKeepAliveManager keepAliveManager;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        keepAliveManager.startKeepAlive();
    }
}
