package com.research.otpactivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.ActionMode;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

public class OtpEditText extends AppCompatEditText {
    private float mSpace = 12;
    private float mNumChars = 6;
    private float mLineSpacing = 8;
    private int mMaxLength = 6;
    private float mLineStroke = 2;
    private Paint mLinesPaint;
    private OnClickListener mClickListener;

    public OtpEditText(Context context) {
        super(context);
    }

    public OtpEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OtpEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        float multi = context.getResources().getDisplayMetrics().density;
        mLineStroke = multi * mLineStroke;
        mLinesPaint = new Paint(getPaint());
        mLinesPaint.setStrokeWidth(mLineStroke);
        mLinesPaint.setColor(ContextCompat.getColor(context, R.color.black));
        setBackgroundResource(0);
        mSpace = multi * mSpace;
        mLineSpacing = multi * mLineSpacing;
        mNumChars = mMaxLength;

        super.setOnClickListener(v -> {
            setSelection(getText().length());
            if (mClickListener != null) {
                mClickListener.onClick(v);
            }
        });
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mClickListener = l;
    }

    @Override
    public void setCustomSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
        throw new RuntimeException("setCustomSelectionActionModeCallback() not supported.");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int availableWidth = getWidth() - getPaddingRight() - getPaddingLeft();
        float mCharSize;
        if (mSpace < 0)
            mCharSize = (availableWidth / (mNumChars * 2 - 1));
        else
            mCharSize = (availableWidth - (mSpace * (mNumChars - 1))) / mNumChars;

        int startX = getPaddingLeft();
        int bottom = getHeight() - getPaddingBottom();

        Editable text = getText();
        int textLength = text.length();
        float[] textWidths = new float[textLength];
        getPaint().getTextWidths(getText(), 0, textLength, textWidths);

        for (int i = 0; i < mNumChars; i++) {
            canvas.drawLine(startX, bottom, startX + mCharSize, bottom, mLinesPaint);
            if (getText().length() > i) {
                float middle = startX + mCharSize / 2;
                canvas.drawText(text, i, i + 1, middle - textWidths[0] / 2, bottom - mLineSpacing, getPaint());
            }
            if (mSpace < 0) {
                startX += mCharSize * 2;
            } else {
                startX += mCharSize + mSpace;
            }
        }
    }
}