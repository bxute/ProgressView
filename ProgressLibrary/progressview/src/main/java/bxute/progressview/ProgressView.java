package bxute.progressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

/**
 * Created by Ankit on 8/23/2017.
 */

public class ProgressView extends View {

    //Params
    private int mProgress;
    private int mShaderColor;
    private int mProgressColor;
    private float mShaderWidth;
    private float mProgressWidth;
    private int mTextColor;
    private int mProgressTextSize;

    // Paints
    Paint mShaderPaint;
    Paint mProgressPaint;
    Paint mProgressTextPaint;
    private RectF shaderRect;
    private RectF progressRect;
    private final float START_ANGLE = 270;
    private ProgressAnimation progressAnimation;
    private long PROGRESS_DURATION = 5;
    private float DECELERATE_FACTOR = 5F;
    private int REPEAT_COUNT = 200;
    private float intermediateProgress = 0;
    private Typeface typeface;
    //locals
    private boolean specsCalculated = false;

    public ProgressView(Context context) {
        super(context);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ProgressView,
                0, 0);

        try {

            mProgress = typedArray.getInteger(R.styleable.ProgressView_progress, 0);
            REPEAT_COUNT = mProgress;
            mShaderColor = typedArray.getColor(R.styleable.ProgressView_shaderColor, Color.GRAY);
            mTextColor = mProgressColor = typedArray.getColor(R.styleable.ProgressView_progressColor, Color.GREEN);
            mShaderWidth = typedArray.getDimension(R.styleable.ProgressView_shaderWidth, 24);
            mProgressWidth = typedArray.getDimension(R.styleable.ProgressView_progressWidth, 24);
            mProgressTextSize = typedArray.getInt(R.styleable.ProgressView_progressTextSize, 24);

        } finally {
            {
                typedArray.recycle();
            }
        }

        init();

    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int mProgress) {
        this.mProgress = mProgress;
        REPEAT_COUNT = mProgress;
        invalidate();
        requestLayout();
    }

    public int getShaderColor() {
        return mShaderColor;
    }

    public void setShaderColor(int mShaderColor) {
        this.mShaderColor = mShaderColor;
        invalidate();
        requestLayout();
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int mProgressColor) {
        this.mProgressColor = mProgressColor;
        invalidate();
        requestLayout();
    }

    public float getShaderWidth() {
        return mShaderWidth;
    }

    public void setShaderWidth(float mShaderWidth) {
        this.mShaderWidth = mShaderWidth;
        invalidate();
        requestLayout();
    }

    public float getProgressWidth() {
        return mProgressWidth;
    }

    public void setProgressWidth(float mProgressWidth) {
        this.mProgressWidth = mProgressWidth;
        invalidate();
        requestLayout();
    }

    private void init() {

        mShaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShaderPaint.setColor(mShaderColor);
        mShaderPaint.setStrokeWidth(mShaderWidth);
        mShaderPaint.setStyle(Paint.Style.STROKE);
        mShaderPaint.setStrokeCap(Paint.Cap.ROUND);
        mShaderPaint.setAntiAlias(true);
        mShaderPaint.setStrokeJoin(Paint.Join.ROUND);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setStrokeWidth(mProgressWidth);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStrokeJoin(Paint.Join.ROUND);

        mProgressTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressTextPaint.setColor(mTextColor);
        if (typeface != null)
            mProgressTextPaint.setTypeface(typeface);
        mProgressTextPaint.setTextAlign(Paint.Align.CENTER);
        mProgressTextPaint.setTextSize(mProgressTextSize * getResources().getDisplayMetrics().density);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!specsCalculated) {
            int _height = getHeight();
            int _width = getWidth();

            int pl = getPaddingLeft();
            int pt = getPaddingTop();
            int pr = getPaddingRight();
            int pb = getPaddingBottom();

            // Shader
            int shaderLeft = (int) (pl + mShaderWidth);
            int shaderTop = (int) (pt + mShaderWidth);
            int shaderRight = (int) (_width - mShaderWidth - pr);
            int shaderBottom = (int) (_height - mShaderWidth - pb);
            shaderRect = new RectF(shaderLeft, shaderTop, shaderRight, shaderBottom);

            // Progress
            int progressLeft = (int) (pl + mProgressWidth);
            int progressTop = (int) (pt + mProgressWidth);
            int progressRight = (int) (_width - mProgressWidth - pr);
            int progressBottom = (int) (_height - mProgressWidth - pb);
            progressRect = new RectF(progressLeft, progressTop, progressRight, progressBottom);
            specsCalculated = true;
        }

        float sweepAngle = (float) (3.6 * intermediateProgress);
        canvas.drawArc(shaderRect, 0, 360, false, mShaderPaint);   // full round shader
        canvas.drawArc(progressRect, START_ANGLE, sweepAngle, false, mProgressPaint);
        String text = intermediateProgress + "%";
        canvas.drawText(
                text,
                canvas.getWidth() / 2,
                ((canvas.getHeight() / 2) - ((mProgressTextPaint.descent() + mProgressTextPaint.ascent()) / 2)),
                mProgressTextPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int minWidth = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int width = resolveSizeAndState(minWidth, widthMeasureSpec, 1);
        int minHeight = getPaddingTop() + getPaddingBottom();
        int height = resolveSizeAndState(minHeight, heightMeasureSpec, 1);
        setMeasuredDimension(width, height);
    }

    public void startProgress() {

        clearAnimation();
        invalidate();

        progressAnimation = new ProgressAnimation();
        progressAnimation.setDuration(PROGRESS_DURATION);
        progressAnimation.setRepeatCount(REPEAT_COUNT);
        progressAnimation.setInterpolator(new DecelerateInterpolator(DECELERATE_FACTOR));
        progressAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                intermediateProgress++;
            }
        });

        startAnimation(progressAnimation);
    }

    private class ProgressAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            invalidate();
        }
    }


}
