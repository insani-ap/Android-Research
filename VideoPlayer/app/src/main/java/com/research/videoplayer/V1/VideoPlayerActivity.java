package com.research.videoplayer.V1;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.research.videoplayer.R;

public class VideoPlayerActivity extends AppCompatActivity {
    private final Context context = this;
    private VideoView videoView;
    private ImageView thumbnails;
    private ImageView play;
    private ImageView pause;
    private MediaPlayer mediaPlayer;
    private Uri uri;
    private int lastPlayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thumbnails = findViewById(R.id.adsImage);
        play = findViewById(R.id.adsImagePlay);
        pause = findViewById(R.id.adsImagePause);
        videoView = findViewById(R.id.adsVideo);
        findViewById(R.id.loadingPanel).setVisibility(GONE);

        uri = Uri.parse("https://abhiandroid.com/ui/wp-content/uploads/2016/04/videoviewtestingvideo.mp4");
        Glide.with(context).asBitmap().load(uri).into(thumbnails);

        pause.setVisibility(GONE);
        play.setOnClickListener(v -> playVideo(uri));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
            lastPlayed = mediaPlayer.getCurrentPosition();
            Glide.with(context).asBitmap().load(uri).apply(new RequestOptions().frame(lastPlayed * 1000L)).into(thumbnails);

            pause.setVisibility(GONE);
            play.setVisibility(VISIBLE);
        }
    }

    private void playVideo(Uri uri) {
        play.setVisibility(GONE);
        if (mediaPlayer == null) {
            findViewById(R.id.loadingPanel).setVisibility(VISIBLE);

            videoView.setVideoURI(uri);
            videoView.setOnPreparedListener(mp -> {
                findViewById(R.id.loadingPanel).setVisibility(GONE);
                mediaPlayer = mp;
                runVideo(mp);
            });
            videoView.setOnCompletionListener(mp -> {
                thumbnails.setVisibility(VISIBLE);
                pause.setVisibility(GONE);
                play.setVisibility(VISIBLE);
                lastPlayed = 0;
            });
            videoView.setOnErrorListener((mp, what, extra) -> false);
        } else {
            runVideo(mediaPlayer);
        }
    }

    private void runVideo(MediaPlayer mediaPlayer) {
        thumbnails.setVisibility(GONE);
        if (lastPlayed > 0) {
            mediaPlayer.seekTo(lastPlayed);
            pause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.play_button, null));
        } else {
            mediaPlayer.start();
        }

        pause.setVisibility(VISIBLE);
        pause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                pause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.play_button, null));
                mediaPlayer.pause();
            } else {
                pause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pause_button, null));
                mediaPlayer.start();
            }
        });
    }
}