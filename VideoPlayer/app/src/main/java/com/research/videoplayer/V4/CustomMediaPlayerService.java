package com.research.videoplayer.V4;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;

import com.research.videoplayer.R;

import java.io.IOException;

public class CustomMediaPlayerService implements MPService, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener, MediaController.MediaPlayerControl {
    private final EnhancedView enhancedView;
    private final Context context;
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;

    public static CustomMediaPlayerService getInstance(EnhancedView enhancedView, Context context) {
        return new CustomMediaPlayerService(enhancedView, context);
    }

    public CustomMediaPlayerService(EnhancedView enhancedView, Context context) {
        this.enhancedView = enhancedView;
        this.context = context;
    }

    @Override
    public void playSomething(Uri uri) throws IOException {
        enhancedView.showContentLoading();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(uri.toString());
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener((mp, what, extra) -> {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    ((Activity) context).findViewById(R.id.bufferPanel).setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    ((Activity) context).findViewById(R.id.bufferPanel).setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
            return false;
        });
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        enhancedView.setMainMediaPlayer(mediaPlayer);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaController = new MediaController(context);
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(((Activity) context).findViewById(R.id.tvAdsExample));
        mediaController.setPrevNextListeners(v -> {
            try {
                enhancedView.nextPlay();
            } catch (IOException e) {
                Log.d("Err", ""+e.getMessage());
            }
        }, v -> {
            try {
                enhancedView.prevPlay();
            } catch (IOException e) {
                Log.d("Err", ""+e.getMessage());
            }
        });
        enhancedView.setMainMediaController(mediaController);
        enhancedView.videoPrepared();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        enhancedView.hideContentLoading();
        enhancedView.cantPlayVideo();
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (mp.getCurrentPosition() == 1) {
            enhancedView.hideContentLoading();
        }
    }

    @Override
    public void start() {
        enhancedView.playVideo();
    }

    @Override
    public void pause() {
        enhancedView.pauseVideo();
    }

    @Override
    public int getDuration() {
        return enhancedView.getDurationVideo();
    }

    @Override
    public int getCurrentPosition() {
        return enhancedView.getCurrentPositionVideo();
    }

    @Override
    public void seekTo(int pos) {
        enhancedView.seekVideo(pos);
    }

    @Override
    public boolean isPlaying() {
        return enhancedView.statusPlayingVideo();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
