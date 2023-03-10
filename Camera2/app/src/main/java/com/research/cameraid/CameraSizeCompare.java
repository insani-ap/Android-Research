package com.research.cameraid;

import android.util.Size;

import java.util.Comparator;

public class CameraSizeCompare implements Comparator<Size> {
    @Override
    public int compare(Size lhs, Size rhs) {
        return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
    }
}
