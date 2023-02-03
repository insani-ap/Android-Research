package com.research.videoplayer.V3;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.research.videoplayer.R;

import java.io.IOException;

public class VideoPlayerTextureActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private TextView textView;
    private ImageView play, pause, thumbnails, fullscreen;
    private TextureView textureView;
    private MediaPlayer mediaPlayer;
    private Surface surface;
    private final MediaPlayer.OnPreparedListener mediaPlayerPreparedCallback = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            textView.setText("Video is Ready to Play...");
            play.setVisibility(View.VISIBLE);
            play.setOnClickListener(v -> {
                play.setVisibility(View.GONE);
                fadeOutAndHideImage(thumbnails);
                pause.setVisibility(View.VISIBLE);
                fullscreen.setVisibility(View.VISIBLE);
                textView.setText("Playing Video...");

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    fullscreen.setOnClickListener(vw -> setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));
                } else {
                    fullscreen.setOnClickListener(vw -> setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
                }

                mp.start();
            });

            pause.setOnClickListener(v -> {
                if (mp.isPlaying()) {
                    textView.setText("Pausing Video...");
                    pause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.play_button, null));
                    mp.pause();
                } else {
                    textView.setText("Resuming Video...");
                    pause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pause_button, null));
                    mp.start();
                }
            });
        }
    };
    private final MediaPlayer.OnInfoListener mediaPlayerInfoCallback = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_UNKNOWN:
                    Log.d("VPSA", "Something Error");
                    break;
                case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                    Log.d("VPSA", "Anjay patah patah");
                    break;
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    Log.d("VPSA", "Mau mulai nih videonya");
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Log.d("VPSA", "Buffering anjay");
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    Log.d("VPSA", "Lanjut bos");
                    break;
                case 703:
                    Log.d("VPSA", "Awokwkwk kuota abis");
                    break;
                case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                    Log.d("VPSA", "Hmm...");
                    break;
                case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                    Log.d("VPSA", "Gak iso maju mundur");
                    break;
            }
            return false;
        }
    };
    private final MediaPlayer.OnErrorListener mediaPlayerErrorCallback = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    Log.d("VPSA", "Hmm...");
                    break;
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    Log.d("VPSA", "Server down");
                    break;
            }
            return false;
        }
    };
    private final MediaPlayer.OnCompletionListener mediaPlayerCompleteCallback = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            textView.setText("Video Ended");
            pause.setVisibility(View.GONE);
            fullscreen.setVisibility(View.GONE);
            play.setVisibility(View.VISIBLE);
            Glide.with(VideoPlayerTextureActivity.this).asBitmap().load(Uri.parse(videoExample)).into(thumbnails);
            thumbnails.setVisibility(View.VISIBLE);
        }
    };

    private final String videoExample = "https://abhiandroid.com/ui/wp-content/uploads/2016/04/videoviewtestingvideo.mp4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_texture);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        textView = findViewById(R.id.tvVideo);
        textView.setText("Preparing Video...");

        play = findViewById(R.id.adsImagePlay);
        pause = findViewById(R.id.adsImagePause);
        fullscreen = findViewById(R.id.adsImageFullScreen);

        play.setVisibility(View.GONE);
        pause.setVisibility(View.GONE);

        thumbnails = findViewById(R.id.adsImage);
        Glide.with(this).asBitmap().load(Uri.parse(videoExample)).into(thumbnails);

        textureView = findViewById(R.id.tvAdsExample);
        textureView.setSurfaceTextureListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
            textView.setText("Pausing Video...");
            pause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.play_button, null));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (surface != null)
            surface.release();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            findViewById(R.id.RLMaster).setVisibility(View.GONE);
            fullscreen.setOnClickListener(v -> setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            findViewById(R.id.RLMaster).setVisibility(View.VISIBLE);
            fullscreen.setOnClickListener(v -> setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));
        }
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        if (mediaPlayer == null) {
            this.surface = new Surface(surface);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setSurface(this.surface);

            try {
                mediaPlayer.setDataSource(videoExample);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(mediaPlayerPreparedCallback);
                mediaPlayer.setOnInfoListener(mediaPlayerInfoCallback);
                mediaPlayer.setOnErrorListener(mediaPlayerErrorCallback);
                mediaPlayer.setOnCompletionListener(mediaPlayerCompleteCallback);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            } catch (IOException e) {
                Log.d("VPSA", "Failed to Fetch Data, Not a Video");
            } catch (IllegalArgumentException e) {
                Log.d("VPSA", "Failed to Set, already setted");
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        //DO nothing
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        //Do nothing
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        //Do nothing
    }

    private void fadeOutAndHideImage(final ImageView img) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(500);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                img.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        img.startAnimation(fadeOut);
    }
}
