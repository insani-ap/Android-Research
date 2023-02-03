package com.research.videoplayer.V4;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.research.videoplayer.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnhancedVideoPlayerActivity extends AppCompatActivity implements EnhancedView {
    private final EnhancedView enhancedView = this;
    private final Context context = this;
    private CustomMediaPlayerService customMediaPlayerService;
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private TextureView textureView;
    private Surface mSurface;
    private List<Uri> videoList;
    private int counter;
    private int surfaceWidth, surfaceHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enhanced_player);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemBars();
            findViewById(R.id.RLMaster).setVisibility(View.GONE);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            findViewById(R.id.RLMaster).setVisibility(View.VISIBLE);
        }

        buildListVideo();

        try {
            initializeVideoPlayer();
        } catch (IOException e) {
            Log.d("Err", "" + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (mSurface != null)
            mSurface.release();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemBars();
            findViewById(R.id.RLMaster).setVisibility(View.GONE);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            setViewSize(displayMetrics.widthPixels, displayMetrics.heightPixels);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            findViewById(R.id.RLMaster).setVisibility(View.VISIBLE);
            setViewSize(surfaceWidth, surfaceHeight);
        }
    }

    @Override
    public void initializeVideoPlayer() throws IOException {
        customMediaPlayerService = CustomMediaPlayerService.getInstance(enhancedView, context);
        customMediaPlayerService.playSomething(videoList.get(counter));
    }

    @Override
    public void showContentLoading() {
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideContentLoading() {
        animFadeOut(findViewById(R.id.loadingPanel));
    }

    @Override
    public void cantPlayVideo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Error.");
        alertDialog.setMessage("Can't play Video");
        alertDialog.setCancelable(false);
        if (counter == 0) {
            alertDialog.setPositiveButton("Next", (dialog, which) -> {
                        try {
                            nextPlay();
                        } catch (IOException e) {
                            Log.d("Err", e.getMessage());
                        }
                    });
        } else if (counter == videoList.size()-1) {
            alertDialog.setNegativeButton("Prev", (dialog, which) -> {
                try {
                    prevPlay();
                } catch (IOException e) {
                    Log.d("Err", e.getMessage());
                }
            });
        } else {
            alertDialog.setPositiveButton("Next", (dialog, which) -> {
                try {
                    nextPlay();
                } catch (IOException e) {
                    Log.d("Err", e.getMessage());
                }
            });
            alertDialog.setNegativeButton("Prev", (dialog, which) -> {
                try {
                    prevPlay();
                } catch (IOException e) {
                    Log.d("Err", e.getMessage());
                }
            });
        }

        alertDialog.show();
    }

    @Override
    public void setMainMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        setUpTextureView();
    }

    @Override
    public void setMainMediaController(MediaController mediaController) {
        this.mediaController = mediaController;
    }

    @Override
    public void setViewSize(int surfaceWidth, int surfaceHeight) {
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;

        float screenProportion = (float) surfaceWidth / (float) surfaceHeight;

        if (videoProportion > screenProportion) {
            textureView.setLayoutParams(new FrameLayout.LayoutParams(surfaceWidth, (int) ((float) surfaceWidth / videoProportion)));
        } else {
            textureView.setLayoutParams(new FrameLayout.LayoutParams((int) (videoProportion * (float) surfaceHeight), surfaceHeight));
        }
    }

    @Override
    public void videoPrepared() {
        mediaPlayer.seekTo(1);
        setViewSize(surfaceWidth, surfaceHeight);
    }

    @Override
    public void playVideo() {
        mediaPlayer.start();
    }

    @Override
    public void pauseVideo() {
        mediaPlayer.pause();
    }

    @Override
    public int getDurationVideo() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPositionVideo() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public boolean statusPlayingVideo() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void seekVideo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public void setUpTextureView() {
        if (mSurface != null) {
            mediaPlayer.setSurface(mSurface);
        } else {
            textureView = findViewById(R.id.tvAdsExample);
            textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                    mSurface = new Surface(surface);
                    mediaPlayer.setSurface(mSurface);
                    textureView.setOnTouchListener((v, event) -> {
                        if (mediaController != null)
                            mediaController.show();
                        return false;
                    });
                    surfaceWidth = width;
                    surfaceHeight = height;
                }

                @Override
                public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {}

                @Override
                public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {}
            });
        }
    }

    @Override
    public void animFadeOut(View view) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        view.startAnimation(fadeOut);
    }

    @Override
    public void nextPlay() throws IOException {
        counter++;
        if (counter >= videoList.size()) {
            Toast.makeText(context, "Tidak ada Video Lagi", Toast.LENGTH_SHORT).show();
            counter--;
        } else {
            mediaPlayer.release();
            mediaPlayer = null;
            customMediaPlayerService.playSomething(videoList.get(counter));
        }
    }

    @Override
    public void prevPlay() throws IOException {
        counter--;
        if (counter <= -1) {
            Toast.makeText(context, "Tidak ada Video Lagi", Toast.LENGTH_SHORT).show();
            counter++;
        } else {
            mediaPlayer.release();
            mediaPlayer = null;
            customMediaPlayerService.playSomething(videoList.get(counter));
        }
    }

    @Override
    public void buildListVideo() {
        videoList = new ArrayList<>();

        //AVI - Ada error Native 1. Di exo juga gak bisa, gak ada decodernya
        videoList.add(Uri.parse("https://www.engr.colostate.edu/me/facil/dynamics/files/bird.avi"));
        videoList.add(Uri.parse("https://file-examples-com.github.io/uploads/2018/04/file_example_AVI_1280_1_5MG.avi"));

        //MKV - Ada error Native 1. Fix pakai clearHttp
        videoList.add(Uri.parse("http://mirrors.standaloneinstaller.com/video-sample/page18-movie-4.mkv"));

        //3gp - Error Native 1. Bisa jalan di Exo, mungkin masalah decoding-encoding
        videoList.add(Uri.parse("https://filesamples.com/samples/video/3gp/sample_960x400_ocean_with_audio.3gp"));
        //Ini jalan
        videoList.add(Uri.parse("http://mirrors.standaloneinstaller.com/video-sample/page18-movie-4.3gp"));

        //MP4 - work
        videoList.add(Uri.parse("https://ia804608.us.archive.org/1/items/anime-flash-mieruko-chan-07/%5BAnimeFlash%5D%20Mieruko-chan%20-%2007.ia.mp4?cnt=0"));
        videoList.add(Uri.parse("https://ia801509.us.archive.org/23/items/takaghisanAHD/takaghisanAHD.mp4"));
        videoList.add(Uri.parse("https://abhiandroid.com/ui/wp-content/uploads/2016/04/videoviewtestingvideo.mp4"));
    }

    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars());
    }
}
