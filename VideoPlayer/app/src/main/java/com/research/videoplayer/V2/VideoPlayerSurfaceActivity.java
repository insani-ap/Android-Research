package com.research.videoplayer.V2;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.research.videoplayer.R;

import java.io.IOException;

public class VideoPlayerSurfaceActivity extends AppCompatActivity {
    private TextView textView;
    private ImageView play, pause, thumbnails;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private final SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDisplay(surfaceHolder);

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
            } else {
                mediaPlayer.setDisplay(holder);
            }
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            //Do nothing
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            //Do nothing
        }
    };

    private MediaPlayer mediaPlayer;
    private final MediaPlayer.OnPreparedListener mediaPlayerPreparedCallback = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            findViewById(R.id.loadingPanel).setVisibility(GONE);
            textView.setText("Video is Ready to Play...");
            play.setVisibility(View.VISIBLE);
            play.setOnClickListener(v -> {
                play.setVisibility(View.GONE);
                thumbnails.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                textView.setText("Playing Video...");
                mp.start();
            });

            pause.setOnClickListener(v -> {
                if (mp.isPlaying()) {
                    textView.setText("Pausing Video...");
                    pause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.play_button, null));
                    mp.pause();
                } else {
                    if (thumbnails.getVisibility() == VISIBLE)
                        thumbnails.setVisibility(GONE);
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
            play.setVisibility(View.VISIBLE);
            Glide.with(VideoPlayerSurfaceActivity.this).asBitmap().load(Uri.parse(videoExample)).into(thumbnails);
            thumbnails.setVisibility(View.VISIBLE);
        }
    };

    private final String videoExample = "https://abhiandroid.com/ui/wp-content/uploads/2016/04/videoviewtestingvideo.mp4";
    private int lastPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_surface);

        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        textView = findViewById(R.id.tvVideo);
        textView.setText("Preparing Video...");

        play = findViewById(R.id.adsImagePlay);
        pause = findViewById(R.id.adsImagePause);
        play.setVisibility(View.GONE);
        pause.setVisibility(View.GONE);

        thumbnails = findViewById(R.id.adsImage);
        Glide.with(this).asBitmap().load(Uri.parse(videoExample)).into(thumbnails);

        surfaceView = findViewById(R.id.svAdsExample);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(surfaceHolderCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
            lastPosition = mediaPlayer.getCurrentPosition();
            textView.setText("Pausing Video...");
            pause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.play_button, null));
            Glide.with(this).asBitmap().apply(new RequestOptions().frame(lastPosition * 1000L)).into(thumbnails);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
