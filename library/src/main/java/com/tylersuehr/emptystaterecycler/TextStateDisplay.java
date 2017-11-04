package com.tylersuehr.emptystaterecycler;
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
 * Subclass of {@link AbstractStateDisplay} that contains properties for two texts (one above
 * the other one), a title and a subtitle perhaps, and affords an API to draw and manipulate
 * them.
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public class TextStateDisplay extends AbstractStateDisplay {
    private boolean textLayoutsConfigured = false;

    /* Properties for the title text */
    private final TextPaint titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private StaticLayout titleLayout;
    private String title;

    /* Properties for the subtitle text */
    private final TextPaint subtitlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private StaticLayout subtitleLayout;
    private String subtitle;

    /* Stores the gravity for the text */
    private int textGravity = Gravity.CENTER;
    /* Space between the title and subtitle texts */
    private int titleSpacing;


    public TextStateDisplay(Context c) {
        this(c, "", "");
    }

    public TextStateDisplay(Context c, @NonNull String title, @Nullable String subtitle) {
        DisplayMetrics dm = c.getResources().getDisplayMetrics();

        // Setup default sizes
        final int large = (int)(16f * dm.density);
        setPadding(large, large, large, large);
        this.titleSpacing = (int)(4f * dm.scaledDensity);

        // Setup title defaults
        this.titlePaint.setTextAlign(Paint.Align.CENTER);
        this.titlePaint.setTextSize(18f * dm.scaledDensity);
        this.titlePaint.setColor(Color.BLACK);
        this.title = title;

        // Default to a single line of text
        this.titleLayout = new StaticLayout(title,
                titlePaint,
                (int)titlePaint.measureText(title),
                Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0, false);

        // Setup subtitle defaults
        this.subtitlePaint.setTextSize(14f * dm.scaledDensity);
        this.subtitlePaint.setTextAlign(Paint.Align.CENTER);
        this.subtitlePaint.setColor(Color.GRAY);
        this.subtitle = subtitle;

        // Default to a single line of text
        this.subtitleLayout = new StaticLayout(subtitle,
                subtitlePaint,
                (int)subtitlePaint.measureText(subtitle),
                Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0, false);
    }

    @Override
    public void onDrawState(EmptyStateRecyclerView rv, Canvas canvas) {
        final int width = rv.getMeasuredWidth();
        final int height = rv.getMeasuredHeight();
        configureTextLayouts(width);

        // Account for vertical text gravity
        final int verticalGravity = textGravity&Gravity.VERTICAL_GRAVITY_MASK;
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

        final int horizontalGravity = Gravity.getAbsoluteGravity(textGravity,
                ViewCompat.getLayoutDirection(rv))&Gravity.HORIZONTAL_GRAVITY_MASK;

        // Draw the title text
        canvas.save();
        canvas.translate(
                getDx(width, horizontalGravity, titlePaint, titleLayout),
                dy);
        this.titleLayout.draw(canvas);
        canvas.restore();

        // Add spacing for under the text with the title spacing
        dy += titleLayout.getHeight() + titleSpacing;

        // Draw the subtitle text under the title text
        canvas.save();
        canvas.translate(
                getDx(width, horizontalGravity, subtitlePaint, subtitleLayout),
                dy);
        this.subtitleLayout.draw(canvas);
        canvas.restore();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        invalidateText();
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
                this.titlePaint.setTextAlign(Paint.Align.LEFT);
                this.subtitlePaint.setTextAlign(Paint.Align.LEFT);
                break;
            case Gravity.CENTER_HORIZONTAL:
                this.titlePaint.setTextAlign(Paint.Align.CENTER);
                this.subtitlePaint.setTextAlign(Paint.Align.CENTER);
                break;
        }
        this.textGravity = gravity;
        // No invalidation needed
    }

    /**
     * Sets the spacing between the title and subtitle texts.
     * @param spacing Spacing
     */
    public void setTitleSpacing(int spacing) {
        this.titleSpacing = spacing;
        // No invalidation needed
    }

    /**
     * Sets the text color of the title text.
     * @param color Title text color
     */
    public void setTitleTextColor(@ColorInt int color) {
        this.titlePaint.setColor(color);
        invalidateText();
    }

    /**
     * Sets the text size of the title text.
     * @param textSize Title text size
     */
    public void setTitleTextSize(float textSize) {
        this.titlePaint.setTextSize(textSize);
        invalidateText();
    }

    /**
     * Sets the paint alignment that draws the title.
     * @param align {@link android.graphics.Paint.Align}
     */
    public void setTitleTextAlign(Paint.Align align) {
        this.titlePaint.setTextAlign(align);
        invalidateText();
    }

    /**
     * Sets the title text to be drawn.
     * @param title Title
     */
    public void setTitle(String title) {
        this.title = title;
        invalidateText();
    }

    /**
     * Sets the text color of the subtitle text.
     * @param color Subtitle text color
     */
    public void setSubtitleTextColor(@ColorInt int color) {
        this.subtitlePaint.setColor(color);
        invalidateText();
    }

    /**
     * Sets the text size of the subtitle text.
     * @param textSize Subtitle text size
     */
    public void setSubtitleTextSize(float textSize) {
        this.subtitlePaint.setTextSize(textSize);
        invalidateText();
    }

    /**
     * Sets the paint alignment that draws the subtitle.
     * @param align {@link android.graphics.Paint.Align}
     */
    public void setSubtitleTextAlign(Paint.Align align) {
        this.subtitlePaint.setTextAlign(align);
        invalidateText();
    }

    /**
     * Sets the subtitle to be drawn.
     * @param subtitle Subtitle
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        invalidateText();
    }

    /**
     * Sets the typeface used to draw the title and subtitle texts.
     * @param typeface {@link Typeface}
     */
    public void setTypeface(Typeface typeface) {
        this.titlePaint.setTypeface(typeface);
        this.subtitlePaint.setTypeface(typeface);
        invalidateText();
    }

    private void invalidateText() {
        this.textLayoutsConfigured = false;
    }

    private float getFullTextHeight() {
        return titleLayout.getHeight() // Height of all title lines (could be more than 1)
                + subtitleLayout.getHeight() // Height of all subtitle lines (could be more than 1)
                + titleSpacing + getPaddingTop() + getPaddingBottom(); // Spacing with top & bottom padding
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

    private void configureTextLayouts(final int availableWidth) {
        if (!textLayoutsConfigured) {
            final int totalNeededPadding = getPaddingLeft() + getPaddingRight();

            // Create new static layout only if needed!
            if ((titleLayout.getWidth() + totalNeededPadding) > availableWidth) {
                this.titleLayout = new StaticLayout(title,
                        titlePaint,
                        availableWidth,
                        Layout.Alignment.ALIGN_NORMAL,
                        1.15f, 0, false);
            }

            // Create new static layout only if needed!
            if ((subtitleLayout.getWidth() + totalNeededPadding) > availableWidth) {
                this.subtitleLayout = new StaticLayout(subtitle,
                        subtitlePaint,
                        availableWidth,
                        Layout.Alignment.ALIGN_NORMAL,
                        1.15f, 0, false);
            }

            textLayoutsConfigured = true;
        }
    }
}