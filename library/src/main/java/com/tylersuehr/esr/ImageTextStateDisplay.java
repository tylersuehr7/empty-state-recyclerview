package com.tylersuehr.esr;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public class ImageTextStateDisplay extends AbstractStateDisplay {
    private final int sixteenDp;

    private boolean configured = false;

    /* Properties for the title text */
    private final TextPaint titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private StaticLayout titleLayout;
    private String title;

    /* Properties for the subtitle text */
    private final TextPaint subtitlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private StaticLayout subtitleLayout;
    private String subtitle;

    /* Properties for the image */
    private Bitmap image;
    /* Space between the title and subtitle texts */
    private int titleSpacing;
    /* Space between the text and image */
    private int textImageSpacing;
    /* True if the text should be drawn above the image */
    private boolean drawTextFirst = false;


    public ImageTextStateDisplay(Context c, @DrawableRes int res) {
        this(c, res, "", "");
    }

    public ImageTextStateDisplay(Context c, @DrawableRes int res, @NonNull String title, @Nullable String subtitle) {
        this(c, BitmapFactory.decodeResource(c.getResources(), res), title, subtitle);
    }

    public ImageTextStateDisplay(Context c, @NonNull Bitmap bitmap, @NonNull String title, @Nullable String subtitle) {
        DisplayMetrics dm = c.getResources().getDisplayMetrics();
        this.sixteenDp = (int)(16f * dm.density);

        // Setup default sizes
        setPadding(sixteenDp, sixteenDp, sixteenDp, sixteenDp);
        this.titleSpacing = (int)(4f * dm.scaledDensity);
        this.textImageSpacing = sixteenDp;

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
        this.subtitle = (subtitle == null ? "Dank yo hommie future is a rapper sdlkfjsldjfslkdjfsldjfasdjfasljdfasjdflasjdflsajd" : subtitle);

        // Default to a single line of text
        this.subtitleLayout = new StaticLayout(this.subtitle,
                subtitlePaint,
                (int)subtitlePaint.measureText(this.subtitle),
                Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0, false);

        this.image = bitmap;
    }

    @Override
    public void onDrawState(StateRecyclerView rv, Canvas canvas) {
        if (drawTextFirst) {
            drawTextFirst(rv, canvas);
        } else {
            drawImageFirst(rv, canvas);
        }
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        invalidateConfig();
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
        invalidateConfig();
    }

    /**
     * Sets the text size of the title text.
     * @param textSize Title text size
     */
    public void setTitleTextSize(float textSize) {
        this.titlePaint.setTextSize(textSize);
        invalidateConfig();
    }

    /**
     * Sets the paint alignment that draws the title.
     * @param align {@link android.graphics.Paint.Align}
     */
    public void setTitleTextAlign(Paint.Align align) {
        this.titlePaint.setTextAlign(align);
        invalidateConfig();
    }

    /**
     * Sets the title text to be drawn.
     * @param title Title
     */
    public void setTitle(String title) {
        this.title = title;
        invalidateConfig();
    }

    /**
     * Sets the text color of the subtitle text.
     * @param color Subtitle text color
     */
    public void setSubtitleTextColor(@ColorInt int color) {
        this.subtitlePaint.setColor(color);
        invalidateConfig();
    }

    /**
     * Sets the text size of the subtitle text.
     * @param textSize Subtitle text size
     */
    public void setSubtitleTextSize(float textSize) {
        this.subtitlePaint.setTextSize(textSize);
        invalidateConfig();
    }

    /**
     * Sets the paint alignment that draws the subtitle.
     * @param align {@link android.graphics.Paint.Align}
     */
    public void setSubtitleTextAlign(Paint.Align align) {
        this.subtitlePaint.setTextAlign(align);
        invalidateConfig();
    }

    /**
     * Sets the subtitle to be drawn.
     * @param subtitle Subtitle
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        invalidateConfig();
    }

    /**
     * Sets the typeface used to draw the title and subtitle texts.
     * @param typeface {@link Typeface}
     */
    public void setTypeface(Typeface typeface) {
        this.titlePaint.setTypeface(typeface);
        this.subtitlePaint.setTypeface(typeface);
        invalidateConfig();
    }

    public void setImage(Bitmap bitmap) {
        this.image = bitmap;
        invalidateConfig();
    }

    public void setImage(Context c, @DrawableRes int res) {
        this.image = BitmapFactory.decodeResource(c.getResources(), res);
        invalidateConfig();
    }

    public void setTextImageSpacing(int spacing) {
        this.textImageSpacing = spacing;
        invalidateConfig();
    }

    public void setDrawTextFirst(boolean value) {
        this.drawTextFirst = value;
    }

    private void drawImageFirst(StateRecyclerView rv, Canvas canvas) {
        final int width = rv.getMeasuredWidth();
        final int height = rv.getMeasuredHeight();
        configure(width);

        float dy = (height >> 1) - ((image.getHeight() + sixteenDp + (int)getFullTextHeight()) >> 1);
        canvas.drawBitmap(image,
                (width >> 1) - ((image.getWidth() >> 1)),
                dy,
                null);

        dy += image.getHeight() + textImageSpacing;

        // Draw the title text
        canvas.save();
        canvas.translate(
                (width >> 1),
                dy);
        this.titleLayout.draw(canvas);
        canvas.restore();

        // Add spacing for under the text with the title spacing
        dy += titleLayout.getHeight() + titleSpacing;

        // Draw the subtitle text under the title text
        canvas.save();
        canvas.translate(
                (width >> 1),
                dy);
        this.subtitleLayout.draw(canvas);
        canvas.restore();
    }

    private void drawTextFirst(StateRecyclerView rv, Canvas canvas) {
        final int width = rv.getMeasuredWidth();
        final int height = rv.getMeasuredHeight();
        configure(width);

        float dy = (height >> 1) - ((image.getHeight() + sixteenDp + (int)getFullTextHeight()) >> 1);

        // Draw the title text
        canvas.save();
        canvas.translate(
                (width >> 1),
                dy);
        this.titleLayout.draw(canvas);
        canvas.restore();

        // Add spacing for under the text with the title spacing
        dy += titleLayout.getHeight() + titleSpacing;

        // Draw the subtitle text under the title text
        canvas.save();
        canvas.translate(
                (width >> 1),
                dy);
        this.subtitleLayout.draw(canvas);
        canvas.restore();

        dy += subtitleLayout.getHeight() + textImageSpacing;

        canvas.drawBitmap(image,
                (width >> 1) - ((image.getWidth() >> 1)),
                dy,
                null);
    }

    private void invalidateConfig() {
        this.configured = false;
    }

    private float getFullTextHeight() {
        return titleLayout.getHeight() // Height of all title lines (could be more than 1)
                + subtitleLayout.getHeight() // Height of all subtitle lines (could be more than 1)
                + titleSpacing + getPaddingTop() + getPaddingBottom(); // Spacing with top & bottom padding
    }

    private void configure(final int availableWidth) {
        if (!configured) {
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

            final int newImageSize = ((availableWidth + getPaddingLeft() + getPaddingRight()) / 3);
            this.image = Bitmap.createScaledBitmap(image, newImageSize, newImageSize, false);

            configured = true;
        }
    }
}