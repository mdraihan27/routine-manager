package io.github.mdraihan27.routinemanager;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.github.mdraihan27.routinemanager.core.background.BackgroundKeepAliveManager;
import io.github.mdraihan27.routinemanager.core.datastore.PreferencesDataSource;
import io.github.mdraihan27.routinemanager.databinding.ActivityMainBinding;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    public static final String ACTION_RESCHEDULE_FROM_NOTIF = "ACTION_RESCHEDULE_FROM_NOTIF";
    public static final String EXTRA_CLASS_ID = "EXTRA_CLASS_ID";
    public static final String ACTION_SHOW_RESCHEDULE_DIALOG = "io.github.mdraihan27.routinemanager.SHOW_RESCHEDULE";

    @Inject
    BackgroundKeepAliveManager keepAliveManager;

    @Inject
    PreferencesDataSource preferencesDataSource;

    private ActivityMainBinding binding;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        disposables.add(
            preferencesDataSource.observePersistentNotificationEnabled()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(enabled -> {
                    if (enabled) {
                        keepAliveManager.startKeepAlive();
                    } else {
                        keepAliveManager.stopKeepAlive();
                    }
                }, throwable -> {})
        );

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && ACTION_RESCHEDULE_FROM_NOTIF.equals(intent.getAction())) {
            long classId = intent.getLongExtra(EXTRA_CLASS_ID, -1);
            if (classId != -1) {
                Intent broadcast = new Intent(ACTION_SHOW_RESCHEDULE_DIALOG);
                broadcast.putExtra(EXTRA_CLASS_ID, classId);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
            }
        }
    }

    @Override
    protected void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }
}
