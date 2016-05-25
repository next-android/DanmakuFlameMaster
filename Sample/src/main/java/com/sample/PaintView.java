package com.sample;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

/**
 * Created by 5410 on 4/23/2016.
 */
public class PaintView extends View {

    private static final String LOG_TAG = "PaintView" ;
    private float mStrokeWidth;
    private int mStrokeColor;
    private Paint mPaint;

    /**
     * We use a Sparse Array to store pointers by mapping their IDs (integer)
     * with their coordinates (PointF)
     * We should use Sparse Array instead of HashMap because it is optimized by Android
     */
    private SparseArray<PointF> mActivePointer;

    /**
     * We use this array to store the colors (total of 6) of the touch points
     */
    private int[] colors = {
            Color.BLUE, Color.GREEN, Color.MAGENTA,
            Color.BLACK, Color.CYAN, Color.GRAY
    };

    private Random random = new Random();

    public PaintView(Context context) {
        super(context);
        init();
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.PaintView, 0, 0);
        try {
            mStrokeWidth = a.getFloat(R.styleable.PaintView_strokeWidth, 1.0f);
            mStrokeColor = a.getColor(R.styleable.PaintView_strokeColor, 0xffffff);
        } finally {
            a.recycle();
        }
        init();
    }

    /**
     * We should have a function to initialize the resource
     */
    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(mStrokeColor);
        mActivePointer = new SparseArray<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // With each active pointer we draw the corresponding circle at its location
        int size = mActivePointer.size();
        for (int i = 0; i < size; i++) {
            PointF pointF = mActivePointer.valueAt(i);
            mPaint.setARGB(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
            canvas.drawCircle(pointF.x, pointF.y, mStrokeWidth, mPaint);
            mPaint.setColor(colors[random.nextInt(colors.length)]);
            mPaint.setARGB(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
            canvas.drawCircle(pointF.x, pointF.y, mStrokeWidth + 3.0f, mPaint);
            mPaint.setColor(colors[random.nextInt(colors.length)]);
            mPaint.setARGB(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
            canvas.drawCircle(pointF.x, pointF.y, mStrokeWidth + 5.0f, mPaint);
        }
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        Log.d(LOG_TAG, "Pointer index: " + pointerIndex);
        final int pointerId = event.getPointerId(pointerIndex);
        Log.d(LOG_TAG, "Pointer id: " + pointerId);

        int maskedAction = event.getActionMasked();
        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_POINTER_DOWN:
                PointF pointF = new PointF();
                pointF.x = event.getX();
                pointF.y = event.getY();
                mActivePointer.put(pointerId, pointF);
                if (listener != null) listener.onTouch();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mActivePointer.remove(pointerId);
                    }
                }, random.nextInt(500));
                break;
            }
        }
        invalidate();
        return true;
    }

    private TouchListener listener;

    public void setTouchListener(TouchListener listener) {
        this.listener = listener;
    }

    protected interface TouchListener {
        void onTouch();
    }
}
