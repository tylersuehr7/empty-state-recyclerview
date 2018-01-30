package com.tylersuehr.esr;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.Gravity;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * Subclass of {@link AbstractState} that contains properties for two texts
 * (one above the other one), a title and a subtitle perhaps, and affords an
 * API to draw and manipulate them.
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public class TextualState extends AbstractState {
    /* Properties for the title text */
    private final TextPaint mTitlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private StaticLayout mTitleLayout;
    private String mTitle;

    /* Properties for the subtitle text */
    private final TextPaint mSubtitlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private StaticLayout mSubtitleLayout;
    private String mSubtitle;

    /* Stores the gravity for the text */
    private int mGravity = Gravity.CENTER;
    /* Space between the title and subtitle texts */
    private int mSpacing;


    public TextualState(Context c) {
        this(c, "", "");
    }

    public TextualState(Context c, @NonNull String title, @Nullable String subtitle) {
        DisplayMetrics dm = c.getResources().getDisplayMetrics();

        // Setup default sizes
        final int large = (int)(16f * dm.density);
        setPadding(large, large, large, large);
        mSpacing = (int)(4f * dm.scaledDensity);

        // Setup title defaults
        mTitlePaint.setTextAlign(Paint.Align.CENTER);
        mTitlePaint.setTextSize(18f * dm.scaledDensity);
        mTitlePaint.setColor(Color.BLACK);
        mTitle = title;

        // Default to a single line of text
        mTitleLayout = new StaticLayout(title,
                mTitlePaint,
                (int)mTitlePaint.measureText(title),
                Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0, false);

        // Setup subtitle defaults
        mSubtitlePaint.setTextSize(14f * dm.scaledDensity);
        mSubtitlePaint.setTextAlign(Paint.Align.CENTER);
        mSubtitlePaint.setColor(Color.GRAY);
        mSubtitle = subtitle == null ? "" : subtitle;

        // Default to a single line of text
        mSubtitleLayout = new StaticLayout(subtitle,
                mSubtitlePaint,
                (int)mSubtitlePaint.measureText(subtitle),
                Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0, false);
    }

    @Override
    public void onDrawState(StateRecyclerView rv, Canvas canvas) {
        super.onDrawState(rv, canvas);

        final int width = rv.getMeasuredWidth();
        final int height = rv.getMeasuredHeight();

        // Account for vertical text gravity
        final int verticalGravity = mGravity &Gravity.VERTICAL_GRAVITY_MASK;
        float dy;
        switch (verticalGravity) {
            case Gravity.CENTER_VERTICAL:
                dy = (height >> 1) - ((int)getFullTextHeight() >> 1);
                break;
            case Gravity.BOTTOM:
                dy = height - getFullTextHeight();
                break;
            default:
            case Gravity.TOP:
                dy = 0;
                break;
        }
        dy += getPaddingTop();

        final int horizontalGravity = Gravity.getAbsoluteGravity(mGravity,
                ViewCompat.getLayoutDirection(rv))&Gravity.HORIZONTAL_GRAVITY_MASK;

        // Draw the title text
        canvas.save();
        canvas.translate(
                getDx(width, horizontalGravity, mTitlePaint, mTitleLayout),
                dy);
        mTitleLayout.draw(canvas);
        canvas.restore();

        // Add spacing for under the text with the title spacing
        dy += mTitleLayout.getHeight() + mSpacing;

        // Draw the subtitle text under the title text
        canvas.save();
        canvas.translate(
                getDx(width, horizontalGravity, mSubtitlePaint, mSubtitleLayout),
                dy);
        mSubtitleLayout.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onConfigure(int availableWidth, int availableHeight) {
        final int totalNeededPadding = getPaddingLeft() + getPaddingRight();

        // Create new static layout only if needed!
        if ((mTitleLayout.getWidth() + totalNeededPadding) > availableWidth) {
            mTitleLayout = new StaticLayout(mTitle,
                    mTitlePaint,
                    availableWidth,
                    Layout.Alignment.ALIGN_NORMAL,
                    1.15f, 0, false);
        }

        // Create new static layout only if needed!
        if ((mSubtitleLayout.getWidth() + totalNeededPadding) > availableWidth) {
            mSubtitleLayout = new StaticLayout(mSubtitle,
                    mSubtitlePaint,
                    availableWidth,
                    Layout.Alignment.ALIGN_NORMAL,
                    1.15f, 0, false);
        }
    }

    /**
     * Sets the overall alignment for the text (supports all gravity variations
     * except for RIGHT and END).
     * @param gravity Text gravity
     */
    public void setTextGravity(int gravity) {
        // Let's adjust paint alignment to fit horizontal gravity (END not supported)
        final int horizontalGravity = Gravity.getAbsoluteGravity(gravity,
                ViewCompat.LAYOUT_DIRECTION_LTR)&Gravity.HORIZONTAL_GRAVITY_MASK;
        switch (horizontalGravity) {
            case GravityCompat.START:
                mTitlePaint.setTextAlign(Paint.Align.LEFT);
                mSubtitlePaint.setTextAlign(Paint.Align.LEFT);
                break;
            case Gravity.CENTER_HORIZONTAL:
                mTitlePaint.setTextAlign(Paint.Align.CENTER);
                mSubtitlePaint.setTextAlign(Paint.Align.CENTER);
                break;
        }
        mGravity = gravity;
    }

    /**
     * Sets the spacing between the title and subtitle texts.
     * @param spacing Spacing
     */
    public void setTitleSpacing(int spacing) {
        mSpacing = spacing;
    }

    /**
     * Sets the text color of the title text.
     * @param color Title text color
     */
    public void setTitleTextColor(@ColorInt int color) {
        mTitlePaint.setColor(color);
        invalidate();
    }

    /**
     * Sets the text size of the title text.
     * @param textSize Title text size
     */
    public void setTitleTextSize(float textSize) {
        mTitlePaint.setTextSize(textSize);
        invalidate();
    }

    /**
     * Sets the paint alignment that draws the title.
     * @param align {@link android.graphics.Paint.Align}
     */
    public void setTitleTextAlign(Paint.Align align) {
        mTitlePaint.setTextAlign(align);
        invalidate();
    }

    /**
     * Sets the title text to be drawn.
     * @param title Title
     */
    public void setTitle(String title) {
        mTitle = title;
        invalidate();
    }

    /**
     * Sets the text color of the subtitle text.
     * @param color Subtitle text color
     */
    public void setSubtitleTextColor(@ColorInt int color) {
        mSubtitlePaint.setColor(color);
        invalidate();
    }

    /**
     * Sets the text size of the subtitle text.
     * @param textSize Subtitle text size
     */
    public void setSubtitleTextSize(float textSize) {
        mSubtitlePaint.setTextSize(textSize);
        invalidate();
    }

    /**
     * Sets the paint alignment that draws the subtitle.
     * @param align {@link android.graphics.Paint.Align}
     */
    public void setSubtitleTextAlign(Paint.Align align) {
        mSubtitlePaint.setTextAlign(align);
        invalidate();
    }

    /**
     * Sets the subtitle to be drawn.
     * @param subtitle Subtitle
     */
    public void setSubtitle(String subtitle) {
        mSubtitle = subtitle;
        invalidate();
    }

    /**
     * Sets the typeface used to draw the title and subtitle texts.
     * @param typeface {@link Typeface}
     */
    public void setTypeface(Typeface typeface) {
        mTitlePaint.setTypeface(typeface);
        mSubtitlePaint.setTypeface(typeface);
        invalidate();
    }

    private float getFullTextHeight() {
        return mTitleLayout.getHeight() // Height of all title lines (could be more than 1)
                + mSubtitleLayout.getHeight() // Height of all subtitle lines (could be more than 1)
                + mSpacing + getPaddingTop() + getPaddingBottom(); // Spacing with top & bottom padding
    }

    private float getDx(final int width,
                        final int horizontalGravity,
                        final Paint paint,
                        final StaticLayout layout) {
        final boolean centered = paint.getTextAlign() == Paint.Align.CENTER;
        final float dx;
        switch (horizontalGravity) { // No support for GravityCompat.END
            case Gravity.CENTER_HORIZONTAL:
                dx = (width >> 1) - (centered ? 0 : (layout.getWidth() >> 1) - getPaddingLeft());
                break;
            default:
            case GravityCompat.START:
                dx = getPaddingLeft();
                break;
        }
        return dx;
    }
}