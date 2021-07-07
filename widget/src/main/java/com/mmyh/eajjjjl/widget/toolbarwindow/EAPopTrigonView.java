package com.mmyh.eajjjjl.widget.toolbarwindow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.mmyh.eajjjjl.widget.R;


public class EAPopTrigonView extends View {

    private Path mPath = new Path();

    private Paint mPaint = new Paint();

    int mWidth;

    public EAPopTrigonView(Context context) {
        super(context);
    }

    public EAPopTrigonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint.setColor(getResources().getColor(R.color.c666666));
    }

    public EAPopTrigonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWidth == 0) {
            mWidth = getWidth() / 6;
            ViewGroup.LayoutParams vlp = getLayoutParams();
            vlp.height = mWidth;
            setLayoutParams(vlp);
        }
        mPath.reset();
        mPath.moveTo(getWidth() / 3f * 2, 0);
        mPath.lineTo(getWidth() / 3f * 2 - mWidth / 2f, mWidth);
        mPath.lineTo(getWidth() / 3f * 2 + mWidth / 2f, mWidth);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }
}
