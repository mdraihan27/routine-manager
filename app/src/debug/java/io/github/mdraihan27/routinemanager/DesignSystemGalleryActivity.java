package io.github.mdraihan27.routinemanager;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.github.mdraihan27.routinemanager.core.designsystem.components.AppTopBar;
import io.github.mdraihan27.routinemanager.databinding.ActivityDesignSystemGalleryBinding;

public class DesignSystemGalleryActivity extends AppCompatActivity {

    private ActivityDesignSystemGalleryBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDesignSystemGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppTopBar topBar = binding.galleryTopBar;
        topBar.setTitle(R.string.title_gallery);
    }
}
