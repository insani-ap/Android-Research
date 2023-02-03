package com.research.mediacodec;

import android.media.MediaCodec;
import android.os.AsyncTask;
import android.util.Log;

import java.nio.ByteBuffer;

public class H264DecoderAsync extends AsyncTask<String, String, String> {
    private H264Decoder h264Decoder;
    private MediaCodec mediaCodec;

    public H264DecoderAsync(H264Decoder h264Decoder, MediaCodec mediaCodec) {
        this.h264Decoder = h264Decoder;
        this.mediaCodec = mediaCodec;
    }

    @Override
    protected String doInBackground(String... strings) {
        while (!isCancelled()) {
            byte[] frame = h264Decoder.nextFrame();

            int inputIndex = mediaCodec.dequeueInputBuffer(-1);

            if (inputIndex >= 0) {
                ByteBuffer buffer = mediaCodec.getInputBuffer(inputIndex);
                buffer.put(frame);

                mediaCodec.queueInputBuffer(inputIndex, 0, frame.length, 0, 0);
            }

            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            int outputIndex = mediaCodec.dequeueOutputBuffer(info, 0);
            if (outputIndex >= 0)
                mediaCodec.releaseOutputBuffer(outputIndex, true);

            //Wait for next Frame
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Log.d("H264DecoderAsync", "FATAL ERROR: " + e.getMessage());
            }
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            mediaCodec.stop();
            mediaCodec.release();
        } catch (Exception e) {
            Log.d("H264DecoderAsync", "FATAL ERROR: " + e.getMessage());
        }
    }
}
