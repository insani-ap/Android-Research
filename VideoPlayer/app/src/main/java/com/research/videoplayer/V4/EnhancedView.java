package com.research.videoplayer.V4;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.MediaController;

import java.io.IOException;

public interface EnhancedView {
    void initializeVideoPlayer() throws IOException;
    void showContentLoading();
    void hideContentLoading();
    void cantPlayVideo();

    void setMainMediaPlayer(MediaPlayer mediaPlayer);
    void setMainMediaController(MediaController mediaController);
    void setViewSize(int surfaceWidth, int surfaceHeight);

    void videoPrepared();
    void playVideo();
    void pauseVideo();
    int getDurationVideo();
    int getCurrentPositionVideo();
    boolean statusPlayingVideo();
    void seekVideo(int pos);

    void setUpTextureView();
    void animFadeOut(View view);

    void nextPlay() throws IOException;
    void prevPlay() throws IOException;

    void buildListVideo();
}
