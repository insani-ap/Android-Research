package com.research.mediacodec;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class H264Decoder {
    private byte[] spsParams;
    private byte[] ppsParams;
    private byte[] iFrame;

    public H264Decoder(InputStream inputStream) {
        try (InputStream mainInputStream = inputStream) {
            int firstStartCodeIndex = 6;    //SPS
            int secondStartCodeIndex = 0;   //PPS
            int thirdStartCodeIndex = 0;    //IFrame

            //NAL Units
            int firstNaluSize = 0;
            int secondNaluSize = 0;
            int thirdNaluSize = 0;

            byte[] data = toByteArray(mainInputStream);

            for (int i = 0; i < 100; i++) {
                if (data[i] == 0x00 && data[i+1] == 0x00 && data[i+2] == 0x00 && data[i+3] == 0x01) {
                    if ((data[i + 4] & 0x1F) == 7) {
                        firstStartCodeIndex = i;
                        break;
                    }
                }
            }

            for (int i = firstStartCodeIndex + 4; i < firstStartCodeIndex + 100; i++) {
                if (data[i] == 0x00 && data[i+1] == 0x00 && data[i+2] == 0x00 && data[i+3] == 0x01) {
                    if (firstNaluSize == 0)
                        firstNaluSize = i - firstStartCodeIndex;

                    if ((data[i + 4] & 0x1F) == 8) {
                        secondStartCodeIndex = i;
                        break;
                    }
                }
            }

            for (int i = secondStartCodeIndex + 4; i < secondStartCodeIndex + 130; i++) {
                if (data[i] == 0x00 && data[i+1] == 0x00 && data[i+2] == 0x00 && data[i+3] == 0x01) {
                    if (secondNaluSize == 0)
                        secondNaluSize = i - secondStartCodeIndex;

                    if ((data[i+4] & 0x1F) == 5) {
                        thirdStartCodeIndex = i;
                        break;
                    }
                }
            }

            int counter = thirdStartCodeIndex + 4;
            while (counter++ < data.length - 1) {
                if (data[counter] == 0x00 && data[counter + 1] == 0x00 && data[counter + 2] == 0x00 && data[counter + 3] == 0x01) {
                    thirdNaluSize = counter - thirdStartCodeIndex;
                    break;
                }
            }


            byte[] firstNalu = new byte[firstNaluSize];
            byte[] secondNalu = new byte[secondNaluSize];
            byte[] thirdNalu = new byte[thirdNaluSize];

            System.arraycopy(data, thirdStartCodeIndex, thirdNalu, 0, thirdNaluSize);
            System.arraycopy(data, firstStartCodeIndex, firstNalu, 0, firstNaluSize);
            System.arraycopy(data, secondStartCodeIndex, secondNalu, 0, secondNaluSize);

            spsParams = firstNalu;
            ppsParams = secondNalu;
            iFrame = thirdNalu;

        } catch (IOException e) {
            Log.e("H264Decoder", "FATAL ERROR: " + e.getMessage());
        }
    }

    public byte[] getSPS () {
        return spsParams;
    }

    public byte[] getPPS () {
        return ppsParams;
    }

    public byte[] nextFrame () {
        return iFrame;
    }

    private byte[] toByteArray(InputStream inputStream) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            int read = 0;
            byte[] buffer = new byte[1024];
            while (read != -1) {
                read = inputStream.read(buffer);

                if (read != -1)
                    byteArrayOutputStream.write(buffer, 0, read);
            }

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            Log.e("H264Decoder", "FATAL ERROR: " + e.getMessage());
            return new byte[0];
        }
    }
}
