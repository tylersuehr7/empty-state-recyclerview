package com.tylersuehr.esr.extras;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import com.tylersuehr.esr.EmptyStateRecyclerView;

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
    private static final int DEFAULT_ANIM_DURATION = 900;

    /* Properties for content loading drawing */
    private final Paint contentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean animateContentItems = true;
    private int numberOfContentItems = 3;
    private ValueAnimator anim;


    AbstractContentLoadingState(Context c) {
        onSetupContentPaint(c, contentPaint);
    }

    @Override
    public final void onDrawState(final EmptyStateRecyclerView rv, Canvas canvas) {
        final int width = rv.getMeasuredWidth();
        final int height = rv.getMeasuredHeight();

        // Draw all of our content items
        renderContent(numberOfContentItems, width, height, canvas, contentPaint);

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
     */
    protected abstract void renderContent(int numberOfContentItems,
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
     * Sets if the content items will animate or not.
     *
     * @param animateContentItems True to animate content items
     */
    public void setAnimateContentItems(boolean animateContentItems) {
        this.animateContentItems = animateContentItems;
    }

    /**
     * Sets the number of content items to draw.
     *
     * @param numberOfContentItems Number of content items displayed
     */
    public void setNumberOfContentItems(int numberOfContentItems) {
        this.numberOfContentItems = numberOfContentItems;
    }
}