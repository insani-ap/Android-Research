package com.research.otpactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private OtpEditText otpEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.next);
        button.setEnabled(false);
        button.setOnClickListener(view -> showCustomDIalog(1));

        otpEditText = findViewById(R.id.OTPEditText);
        otpEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //DO nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 6) {
                    button.setEnabled(true);
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    showCustomDIalog(1);
                } else {
                    button.setEnabled(false);
                }
            }
        });

        TextView notReceiveOtp = findViewById(R.id.resendCode);
        notReceiveOtp.setOnClickListener(view -> {
            showCustomDIalog(0);
            new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long l) {
                    notReceiveOtp.setText("Not receive an OTP? (00:" + l/1000 + ")");
                    notReceiveOtp.setEnabled(false);
                }

                @Override
                public void onFinish() {
                    notReceiveOtp.setText("Not receive an OTP?");
                    notReceiveOtp.setEnabled(true);
                }
            }.start();
        });

    }

    private void showCustomDIalog(int cases) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {
                //Do Nothing
            }

            @Override
            public void onFinish() {
                progressDialog.hide();
                if (cases == 1) {
                    if ("092381".equals(Objects.requireNonNull(otpEditText.getText()).toString()))
                        showDialog2("OTP Success", "Your OTP is Correct");
                    else
                        showDialog2("OTP Failed", "Your OTP is Incorrect");
                } else {
                    showDialog2("OTP Request", "This is your OTP 092381");
                }
            }
        }.start();
    }

    private void showDialog2(String ttl, String msg) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(ttl)
                .setMessage(msg)
                .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }
}