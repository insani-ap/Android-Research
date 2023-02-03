package com.research.mediacodec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;

public class MainActivity extends AppCompatActivity {
    private TextureView textureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initializeView();
    }

    private void initializeView() {
        textureView = findViewById(R.id.tvMaster);

        setupSurface();
    }

    private void setupSurface() {
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                Thread thread = new Thread(() -> {
                    try {
                        decodeVideo(surface, width, height);
                    } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
                        Log.d("MainActivity", "FATAL ERROR: " + e.getMessage());
                    }
                });
                thread.start();
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                //Do nothing
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
                //Do nothing
            }
        });
    }

    private void decodeVideo(SurfaceTexture surface, int width, int height) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        String testVideo = "http://10.10.1.226:8080/test.320x240.mp4";
        H264Decoder h264Decoder = new H264Decoder(getVideo(new URL(testVideo)));

        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
        format.setByteBuffer("csd-0", ByteBuffer.wrap(h264Decoder.getSPS()));
        format.setByteBuffer("csd-1", ByteBuffer.wrap(h264Decoder.getPPS()));
        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, width * height);

        try {
            MediaCodec mediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            mediaCodec.configure(format, new Surface(surface), null, 0);
            mediaCodec.start();

            H264DecoderAsync h264DecoderAsync = new H264DecoderAsync(h264Decoder, mediaCodec);
            h264DecoderAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e){
            Log.d("MainActivity", "FATAL ERROR: " + e.getMessage());
        }
    }

    private InputStream getVideo(URL url) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new X509TrustManager[]{new X509TrustManager(){
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {}
            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {}
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(60);
        httpURLConnection.connect();

        return httpURLConnection.getInputStream();
    }
}