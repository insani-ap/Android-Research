package com.research.cameraid;

import android.util.Log;
import android.util.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CameraSizeUtil {
    private CameraSizeUtil() {}

    public static Size chooseOptimalSize(Size[] choices, int textureViewWidth, int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {
        List<Size> bigEnough = new ArrayList<>();
        List<Size> notBigEnough = new ArrayList<>();

        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();

        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight && option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth && option.getHeight() >= textureViewHeight)
                    bigEnough.add(option);
                else
                    notBigEnough.add(option);
            }
        }

        if (!bigEnough.isEmpty())
            return Collections.min(bigEnough, new CameraSizeCompare());
        else if (!notBigEnough.isEmpty())
            return Collections.max(notBigEnough, new CameraSizeCompare());
        else {
            Log.e("Camera2", "Couldn't find any suitable preview size");
            return choices[0];
        }
    }
}
