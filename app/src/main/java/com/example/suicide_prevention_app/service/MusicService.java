package com.example.suicide_prevention_app.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.suicide_prevention_app.R;

public class MusicService extends Service {
    private MediaPlayer musicPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        musicPlayer = MediaPlayer.create(this, R.raw.heavenly);
        musicPlayer.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        musicPlayer.start();
        return START_STICKY; // 시스템에 의해 종료 되면 서비스 다시 실행
    }

    @Override
    public void onDestroy() {
        musicPlayer.stop();
        musicPlayer.release();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}