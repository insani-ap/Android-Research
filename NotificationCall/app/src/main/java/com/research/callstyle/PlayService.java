package com.research.callstyle;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.IOException;

public class PlayService extends Service {
    private MediaPlayer mMediaPlayer = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mMediaPlayer = MediaPlayer.create(this, R.raw.anime_ringtone);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                .setLegacyStreamType(AudioManager.STREAM_RING)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build());

        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int streamVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
        mMediaPlayer.setVolume(streamVolume, streamVolume);
        mMediaPlayer.start();

        return START_STICKY;
    }
}
