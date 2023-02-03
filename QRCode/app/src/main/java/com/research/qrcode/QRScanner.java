package com.research.qrcode;

import android.app.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class QRScanner {
    public static void instantiate(Activity activity, BarcodeResultCallback barcodeResultCallback) {
        ScanOptions options = new ScanOptions();
        options.setCaptureActivity(CaptureActivity.class);
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
        options.setPrompt("Scan Barcode");
        options.setOrientationLocked(false);
        options.setBeepEnabled(false);

        ActivityResultLauncher<ScanOptions> barcodeLauncher = ((AppCompatActivity) activity).registerForActivityResult(new ScanContract(), result -> barcodeResultCallback.barcodeResult(result.getContents()));
        barcodeLauncher.launch(options);
    }
}