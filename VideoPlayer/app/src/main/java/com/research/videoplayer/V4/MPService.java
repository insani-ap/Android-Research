package com.research.videoplayer.V4;

import android.net.Uri;

import java.io.IOException;

public interface MPService {
    void playSomething(Uri uri) throws IOException;
}
