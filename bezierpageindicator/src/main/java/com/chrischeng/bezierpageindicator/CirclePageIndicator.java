package com.chrischeng.bezierpageindicator;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class CirclePageIndicator extends View implements IPageIndicator {

    private static final String KEY_INSTANCE = "instance";
    private static final String KEY_POSITION = "position";

    private int mOrientation;
    private float mRadius;
    private float mSpacing;
    private int mCount;
    private boolean mSlideable;
    private boolean mSingleShow;

    private Paint mNormalPaint;
    private Paint mSelectedPaint;
    private Paint mStrokePaint;

    private int mCurrentPosition;
    private float mPositionOffset;

    public CirclePageIndicator(Context context) {
        this(context, null);
    }

    public CirclePageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    public void setCurrentItem(int item) {
        mCurrentPosition = item;
        postInvalidate();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mSlideable) {
            mCurrentPosition = position;
            mPositionOffset = positionOffset;
            postInvalidate();
        }
    }

    @Override
    public void onPageSelected(int position) {
        mPositionOffset = 0;
        mCurrentPosition = position;
        postInvalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        postInvalidate();
    }

    public void setOrientation(int orientation) {
        if (orientation == LinearLayout.HORIZONTAL || orientation == LinearLayout.VERTICAL) {
            mOrientation = orientation;
            requestLayout();
        }
    }

    public void setCount(int count) {
        mCount = count;
        requestLayout();
        invalidate();
    }

    public void setRadius(float radius) {
        mRadius = radius;
        invalidate();
    }

    public void setSpacing(float spacing) {
        mSpacing = spacing;
        invalidate();
    }

    public void setNormalColor(int color) {
        mNormalPaint.setColor(color);
        invalidate();
    }

    public void setSelectedColor(int color) {
        mSelectedPaint.setColor(color);
        invalidate();
    }

    public void setStrokeColor(int color) {
        mStrokePaint.setColor(color);
        invalidate();
    }

    public void setStrokeWidth(float strokeWidth) {
        mStrokePaint.setStrokeWidth(strokeWidth);
        invalidate();
    }

    public void setStrokeWidth(int strokeWidthPx) {
        mStrokePaint.setStrokeWidth(strokeWidthPx);
        invalidate();
    }

    public void setSlideable(boolean slideable) {
        mSlideable = slideable;
    }

    public void setmSingleShow(boolean singleShow) {
        mSingleShow = singleShow;
        if (mCount == 1)
            invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mOrientation == LinearLayout.HORIZONTAL)
            setMeasuredDimension(measureLong(widthMeasureSpec), measureShort(heightMeasureSpec));
        else
            setMeasuredDimension(measureShort(widthMeasureSpec), measureLong(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mCount <= 0 || (mCount == 1 && !mSingleShow))
            return;

        float longOffset = mRadius;
        float shortOffset = mRadius;

        if (mOrientation == LinearLayout.HORIZONTAL) {
            longOffset += getPaddingLeft();
            shortOffset += getPaddingTop();
        } else {
            longOffset += getPaddingTop();
            shortOffset += getPaddingLeft();
        }

        float cx;
        float cy;

        float normalRadius = mRadius;
        if (mStrokePaint.getStrokeWidth() > 0)
            normalRadius -= mStrokePaint.getStrokeWidth() / 2.0f;

        for (int i = 0; i < mCount; i++) {
            float longStart = longOffset + (mRadius * 2 + mSpacing) * i;

            if (mOrientation == LinearLayout.HORIZONTAL) {
                cx = longStart;
                cy = shortOffset;
            } else {
                cx = shortOffset;
                cy = longStart;
            }

            if (mNormalPaint.getAlpha() > 0)
                canvas.drawCircle(cx, cy, normalRadius, mNormalPaint);

            if (normalRadius != mRadius)
                canvas.drawCircle(cx, cy, mRadius, mStrokePaint);
        }

        if (mOrientation == LinearLayout.HORIZONTAL) {
            cx = longOffset + (2 * mRadius + mSpacing) * (mCurrentPosition + mPositionOffset);
            cy = shortOffset;
        } else {
            cx = shortOffset;
            cy = longOffset + (2 * mRadius + mSpacing) * (mCurrentPosition + mPositionOffset);
        }

        if (mSelectedPaint.getAlpha() > 0)
            canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INSTANCE, super.onSaveInstanceState());
        bundle.putInt(KEY_POSITION, mCurrentPosition);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mCurrentPosition = bundle.getInt(KEY_POSITION);
            super.onRestoreInstanceState(bundle.getParcelable(KEY_INSTANCE));
        } else
            super.onRestoreInstanceState(state);
    }

    private void init(Context context, AttributeSet attrs) {
        initPaint();
        initAttrs(context, attrs);
    }

    private void initPaint() {
        mNormalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNormalPaint.setStyle(Paint.Style.FILL);

        mSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectedPaint.setStyle(Paint.Style.FILL);

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        Resources res = context.getResources();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BezierCirclePageIndicator);
        mOrientation = a.getInt(R.styleable.BezierCirclePageIndicator_android_orientation,
                res.getInteger(R.integer.default_circle_orientation));
        mRadius = a.getDimension(R.styleable.BezierCirclePageIndicator_bpi_circle_radius,
                res.getDimension(R.dimen.default_circle_radius));
        mSpacing = a.getDimension(R.styleable.BezierCirclePageIndicator_bpi_circle_spacing,
                res.getDimension(R.dimen.default_circle_spacing));
        mNormalPaint.setColor(a.getColor(R.styleable.BezierCirclePageIndicator_bpi_circle_normalColor,
                res.getColor(R.color.default_circle_normal_color)));
        mSelectedPaint.setColor(a.getColor(R.styleable.BezierCirclePageIndicator_bpi_circle_selectedColor,
                res.getColor(R.color.default_circle_selected_color)));
        mStrokePaint.setColor(a.getColor(R.styleable.BezierCirclePageIndicator_bpi_circle_stroke_color,
                res.getColor(R.color.default_circle_stroke_color)));
        mStrokePaint.setStrokeWidth(a.getDimension(R.styleable.BezierCirclePageIndicator_bpi_circle_stroke_width,
                res.getDimension(R.dimen.default_circle_stroke_width)));
        Drawable background = a.getDrawable(R.styleable.BezierCirclePageIndicator_android_background);
        if (background != null)
            setBackgroundDrawable(background);
        mCount = a.getInteger(R.styleable.BezierCirclePageIndicator_bpi_circle_count,
                res.getInteger(R.integer.default_circle_count));
        mSlideable = a.getBoolean(R.styleable.BezierCirclePageIndicator_bpi_circle_slideable,
                res.getBoolean(R.bool.default_circle_slideable));
        mSingleShow = a.getBoolean(R.styleable.BezierCirclePageIndicator_bpi_circle_single_show,
                res.getBoolean(R.bool.default_circle_single_show));

        a.recycle();
    }

    private int measureLong(int measureSpec) {
        int result;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY)
            result = specSize;
        else {
            result = (int) (getPaddingLeft() + mRadius * 2 * mCount + mSpacing * (mCount - 1) + getPaddingRight() + 1);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    private int measureShort(int measureSpec) {
        int result;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY)
            result = specSize;
        else {
            result = (int) (getPaddingTop() + mRadius * 2 + getPaddingBottom() + 1);
            if (specMode == MeasureSpec.AT_MOST)
                result = Math.min(result, specSize);
        }

        return result;
    }
}
