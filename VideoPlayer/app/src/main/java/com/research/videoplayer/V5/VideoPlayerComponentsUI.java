package com.research.videoplayer.V5;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.research.videoplayer.R;

public class VideoPlayerComponentsUI extends AppCompatActivity {
    private LinearLayout skipAds, rootLayout;
    private ExoPlayer player;
    private RelativeLayout rlView;
    private final Uri videoUri = Uri.parse("https://ia600107.us.archive.org/13/items/KJnT12_ENDR-ID-ShiyamaSei/%5B%20ShiyamaSei%20%5D%20Karakai%20Jouzu%20No%20Takagi-san%20-%2012%20%5B%20x264%20360p-ID%20AAC-JP%20%5D.mp4?cnt=0");
    private final Uri videoSecondUri = Uri.parse("https://ia804608.us.archive.org/1/items/anime-flash-mieruko-chan-07/%5BAnimeFlash%5D%20Mieruko-chan%20-%2007.ia.mp4?cnt=0");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exo_player);

        rootLayout = findViewById(R.id.rootLayout);
        rlView = findViewById(R.id.rlView);
        skipAds = findViewById(R.id.exo_skip_ads);
        skipAds.setVisibility(View.GONE);
        checkOrientation();

        ImageButton fullScreen = findViewById(R.id.exo_fullscreen_button);
        fullScreen.setOnClickListener(view -> setRequestedOrientation(checkOrientation()));

        MediaItem firstMedia = MediaItem.fromUri(videoUri);
        MediaItem secondMedia = MediaItem.fromUri(videoSecondUri);
        player = new ExoPlayer.Builder(this).build();

        PlayerView playerView = findViewById(R.id.playerView);
        playerView.setPlayer(player);
        playerView.setControllerShowTimeoutMs(3000);

        player.addMediaItem(firstMedia);
        player.addMediaItem(secondMedia);
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY && !player.isPlaying())
                    playerView.showController();
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying)
                    skipAds.setVisibility(View.VISIBLE);
                else
                    skipAds.setVisibility(View.GONE);
            }
        });
        skipAds.setOnClickListener(v -> {
            if (player.hasNextMediaItem()) {
                playerView.hideController();
                player.seekToNextMediaItem();
            } else {
                player.stop();
                Snackbar.make(rootLayout, "Tidak ada Video Lainnya", BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });


        player.prepare();
        playerView.hideController();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null)
            player.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null)
            player.release();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideShowSystemBars("hide");
            rlView.setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            hideShowSystemBars("show");
            rlView.setVisibility(View.VISIBLE);
        }
    }

    private void hideShowSystemBars(String status) {
        WindowInsetsControllerCompat windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null || status == null) {
            return;
        }

        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        if (status.equals("hide"))
            windowInsetsController.hide(WindowInsetsCompat.Type.statusBars());
        else if (status.equals("show"))
            windowInsetsController.show(WindowInsetsCompat.Type.statusBars());
    }

    private int checkOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideShowSystemBars("hide");
            rlView.setVisibility(View.GONE);
            return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        } else {
            hideShowSystemBars("show");
            rlView.setVisibility(View.VISIBLE);
            return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }
    }
}