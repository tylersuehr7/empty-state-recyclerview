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
import android.support.v4.content.ContextCompat;
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
public class TextualImageState extends AbstractState {
    private final int sixteenDp;
    
    /* Properties for the title text */
    private final TextPaint mTitlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private StaticLayout mTitleLayout;
    private String mTitle;

    /* Properties for the subtitle text */
    private final TextPaint mSubtitlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private StaticLayout mSubtitleLayout;
    private String mSubtitle;

    /* Properties for the image */
    private Bitmap mImage;
    /* Space between the title and subtitle texts */
    private int mTitleSpacing;
    /* Space between the text and image */
    private int mTextImageSpacing;
    /* True if the text should be drawn above the image */
    private boolean mDrawTextFirst = false;


    public TextualImageState(Context c, @DrawableRes int res) {
        this(c, res, "", "");
    }

    public TextualImageState(Context c, @DrawableRes int res, @NonNull String title, @Nullable String subtitle) {
        this(c, ImageUtils.drawableToBitmap(
                ContextCompat.getDrawable(c, res)), title, subtitle);
    }

    public TextualImageState(Context c, @NonNull Bitmap bitmap, @NonNull String title, @Nullable String subtitle) {
        DisplayMetrics dm = c.getResources().getDisplayMetrics();
        this.sixteenDp = (int)(16f * dm.density);

        // Setup default sizes
        setPadding(sixteenDp, sixteenDp, sixteenDp, sixteenDp);
        mTitleSpacing = (int)(4f * dm.scaledDensity);
        mTextImageSpacing = sixteenDp;

        // Setup title defaults
        mTitlePaint.setTextAlign(Paint.Align.CENTER);
        mTitlePaint.setTextSize(18f * dm.scaledDensity);
        mTitlePaint.setColor(Color.BLACK);
        mTitle = title;

        // Default to a single line of text
        mTitleLayout = new StaticLayout(title,
                mTitlePaint,
                (int) mTitlePaint.measureText(title),
                Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0, false);

        // Setup subtitle defaults
        mSubtitlePaint.setTextSize(14f * dm.scaledDensity);
        mSubtitlePaint.setTextAlign(Paint.Align.CENTER);
        mSubtitlePaint.setColor(Color.GRAY);
        mSubtitle = (subtitle == null ? "Dank yo hommie future is a rapper sdlkfjsldjfslkdjfsldjfasdjfasljdfasjdflasjdflsajd" : subtitle);

        // Default to a single line of text
        mSubtitleLayout = new StaticLayout(mSubtitle,
                mSubtitlePaint,
                (int) mSubtitlePaint.measureText(mSubtitle),
                Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0, false);

        mImage = bitmap;
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

        final int newImageSize = ((availableWidth + getPaddingLeft() + getPaddingRight()) / 3);
        mImage = Bitmap.createScaledBitmap(mImage, newImageSize, newImageSize, false);
    }

    @Override
    public void onDrawState(StateRecyclerView rv, Canvas canvas) {
        super.onDrawState(rv, canvas);
        if (mDrawTextFirst) {
            drawTextFirst(rv, canvas);
        } else {
            drawImageFirst(rv, canvas);
        }
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        invalidate();
    }

    /**
     * Sets the spacing between the title and subtitle texts.
     * @param spacing Spacing
     */
    public void setTitleSpacing(int spacing) {
        mTitleSpacing = spacing;
        // No invalidation needed
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

    public void setImage(Bitmap bitmap) {
        mImage = bitmap;
        invalidate();
    }

    public void setImage(Context c, @DrawableRes int res) {
        mImage = ImageUtils.drawableToBitmap(ContextCompat.getDrawable(c, res));
        invalidate();
    }

    public void setTextImageSpacing(int spacing) {
        mTextImageSpacing = spacing;
        invalidate();
    }

    public void setDrawTextFirst(boolean value) {
        mDrawTextFirst = value;
    }

    private void drawImageFirst(StateRecyclerView rv, Canvas canvas) {
        final int width = rv.getMeasuredWidth();
        final int height = rv.getMeasuredHeight();

        float dy = (height >> 1) - ((mImage.getHeight() + sixteenDp + (int)getFullTextHeight()) >> 1);
        canvas.drawBitmap(mImage,
                (width >> 1) - ((mImage.getWidth() >> 1)),
                dy,
                null);

        dy += mImage.getHeight() + mTextImageSpacing;

        // Draw the title text
        canvas.save();
        canvas.translate(
                (width >> 1),
                dy);
        mTitleLayout.draw(canvas);
        canvas.restore();

        // Add spacing for under the text with the title spacing
        dy += mTitleLayout.getHeight() + mTitleSpacing;

        // Draw the subtitle text under the title text
        canvas.save();
        canvas.translate(
                (width >> 1),
                dy);
        mSubtitleLayout.draw(canvas);
        canvas.restore();
    }

    private void drawTextFirst(StateRecyclerView rv, Canvas canvas) {
        final int width = rv.getMeasuredWidth();
        final int height = rv.getMeasuredHeight();

        float dy = (height >> 1) - ((mImage.getHeight() + sixteenDp + (int)getFullTextHeight()) >> 1);

        // Draw the title text
        canvas.save();
        canvas.translate(
                (width >> 1),
                dy);
        mTitleLayout.draw(canvas);
        canvas.restore();

        // Add spacing for under the text with the title spacing
        dy += mTitleLayout.getHeight() + mTitleSpacing;

        // Draw the subtitle text under the title text
        canvas.save();
        canvas.translate(
                (width >> 1),
                dy);
        mSubtitleLayout.draw(canvas);
        canvas.restore();

        dy += mSubtitleLayout.getHeight() + mTextImageSpacing;

        canvas.drawBitmap(mImage,
                (width >> 1) - ((mImage.getWidth() >> 1)),
                dy,
                null);
    }
    
    private float getFullTextHeight() {
        return mTitleLayout.getHeight() // Height of all title lines (could be more than 1)
                + mSubtitleLayout.getHeight() // Height of all subtitle lines (could be more than 1)
                + mTitleSpacing + getPaddingTop() + getPaddingBottom(); // Spacing with top & bottom padding
    }
}