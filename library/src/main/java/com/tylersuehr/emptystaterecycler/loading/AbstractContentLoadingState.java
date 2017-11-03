package com.tylersuehr.emptystaterecycler.loading;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.TextPaint;

import com.tylersuehr.emptystaterecycler.EmptyStateRecyclerView;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * Semi-concrete implementation of {@link EmptyStateRecyclerView.StateDisplay} that
 * provides an API to create 'content' loading state displays.
 *
 * A 'content' loading state display is a display that shows mockup-like shapes that
 * animate to resemble the shimmering effect used by Facebook and other companies.
 *
 * This affords the concrete ability for loading text to be drawn at an offset after
 * calculating and drawing the content, and also to animate the drawn content items.
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public abstract class AbstractContentLoadingState implements EmptyStateRecyclerView.StateDisplay {
    static final int DEFAULT_ANIM_DURATION = 900;
    static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    static final float DEFAULT_TEXT_SIZE = 16f;

    /* Properties for loading text drawing */
    private boolean drawLoadingText = true;
    private final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private Typeface loadingTextTypeface;
    private float loadingTextSize;
    private int loadingTextColor;
    private String loadingText;

    /* Properties for content loading drawing */
    private final Paint contentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean animateContentItems = true;
    private int numberOfContentItems = 3;
    private int[] animationColors = new int[] { Color.LTGRAY, Color.GRAY };
    private ValueAnimator anim;


    AbstractContentLoadingState(Context c) {
        // Setup loading text defaults
        this.loadingTextTypeface = Typeface.DEFAULT;
        this.loadingTextSize = DEFAULT_TEXT_SIZE * c.getResources().getDisplayMetrics().scaledDensity;
        this.loadingTextColor = DEFAULT_TEXT_COLOR;
        this.loadingText = "Loading";

        // Setup loading text paint
        this.textPaint.setColor(loadingTextColor);
        this.textPaint.setTextSize(loadingTextSize);
        this.textPaint.setTypeface(loadingTextTypeface);
        this.textPaint.setTextAlign(Paint.Align.CENTER);

        // Setup content paint
        onSetupContentPaint(c, contentPaint);
    }

    @Override
    public final void onDrawState(final EmptyStateRecyclerView rv, Canvas canvas) {
        final int width = rv.getMeasuredWidth();
        final int height = rv.getMeasuredHeight();

        // Draw all of our content items
        float dy = renderContent(numberOfContentItems, width, height, canvas, contentPaint);

        // Draw our text under content items in vertical-center, if possible
        if (drawLoadingText) {
            canvas.drawText(loadingText,
                    (width >> 1),
                    dy += sizeOfContentItem(),
                    textPaint);
        }

        // Setup and start animation, if possible
        if (animateContentItems) {
            if (anim == null) {
                this.anim = ObjectAnimator.ofObject(contentPaint, "color", new ArgbEvaluator(),
                        Color.parseColor("#E0E0E0"), Color.parseColor("#BDBDBD"), Color.parseColor("#9E9E9E"));
                onInterceptAnimatorCreation(anim);
                this.anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        rv.invalidate();
                    }
                });
                this.anim.start();
            }
        }
    }

    /**
     * Intercepts the animator's creation. Override this if you want to set
     * custom properties on the animator.
     *
     * @param anim {@link ValueAnimator}
     */
    protected void onInterceptAnimatorCreation(@NonNull ValueAnimator anim) {
        anim.setDuration(DEFAULT_ANIM_DURATION);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setRepeatCount(ValueAnimator.INFINITE);
    }

    /**
     * Sets up the content paint used to draw content items.
     *
     * @param c {@link Context}
     * @param contentPaint {@link Paint} to setup
     */
    protected abstract void onSetupContentPaint(@NonNull Context c,
                                                @NonNull Paint contentPaint);

    /**
     * Render the content at a given index in the content item sequence.
     *
     * @param numberOfContentItems Number of content items to draw
     * @param availableWidth Width canvas real-estate
     * @param availableHeight Height canvas real-estate
     * @param canvas {@link Canvas} to draw on
     * @param contentPaint {@link Paint} to draw with
     * @return y-coordinate of last content item position
     */
    protected abstract float renderContent(int numberOfContentItems,
                                           int availableWidth,
                                           int availableHeight,
                                           Canvas canvas,
                                           Paint contentPaint);

    /**
     * Get the height of a single content item.
     *
     * @return Height in pixels
     */
    protected abstract int sizeOfContentItem();

    /**
     * Measures the width of the current set loading text.
     * @return Width of loading text
     */
    protected float getTextWidth() {
        return textPaint.measureText(loadingText);
    }

    /**
     * Measures the font height metrics of the current text paint.
     * @return Font height metrics
     */
    protected float getFontHeight() {
        return (textPaint.descent() + textPaint.ascent());
    }

    // TODO: add documentation
    public boolean isDrawLoadingText() {
        return drawLoadingText;
    }

    public void setDrawLoadingText(boolean drawLoadingText) {
        this.drawLoadingText = drawLoadingText;
    }

    public Typeface getLoadingTextTypeface() {
        return loadingTextTypeface;
    }

    public void setLoadingTextTypeface(Typeface loadingTextTypeface) {
        this.loadingTextTypeface = loadingTextTypeface;
        this.textPaint.setTypeface(loadingTextTypeface);
    }

    public float getLoadingTextSize() {
        return loadingTextSize;
    }

    public void setLoadingTextSize(float loadingTextSize) {
        this.loadingTextSize = loadingTextSize;
        this.textPaint.setTextSize(loadingTextSize);
    }

    public int getLoadingTextColor() {
        return loadingTextColor;
    }

    public void setLoadingTextColor(int loadingTextColor) {
        this.loadingTextColor = loadingTextColor;
        this.textPaint.setColor(loadingTextColor);
    }

    public String getLoadingText() {
        return loadingText;
    }

    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
    }

    public boolean isAnimateContentItems() {
        return animateContentItems;
    }

    public void setAnimateContentItems(boolean animateContentItems) {
        this.animateContentItems = animateContentItems;
    }

    public int getNumberOfContentItems() {
        return numberOfContentItems;
    }

    public void setNumberOfContentItems(int numberOfContentItems) {
        this.numberOfContentItems = numberOfContentItems;
    }
}