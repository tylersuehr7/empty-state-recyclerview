package com.tylersuehr.emptystaterecycler;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * A default implementation of {@link EmptyStateRecyclerView.StateDisplay} that will just
 * draw text in the center of the view... intended to be used for any state.
 *
 * @see Builder for instance creation
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public final class DefaultStateDisplay implements EmptyStateRecyclerView.StateDisplay {
    private final TextPaint paint;
    private final String text;


    private DefaultStateDisplay(final Builder b) {
        this.paint = b.paint;
        this.text = b.text;
    }

    @Override
    public void onDrawState(EmptyStateRecyclerView rv, Canvas canvas) {
        // Get available width and height for drawing space
        final int width = rv.getMeasuredWidth();
        final int height = rv.getMeasuredHeight();

        final float dx = (width >> 1);
        final float dy = (height >> 1);

        canvas.drawText(text, dx, dy, paint);
    }


    /**
     * Internal structure used to build instances of {@link DefaultStateDisplay}.
     */
    public static final class Builder {
        private final Context c;
        private final TextPaint paint;
        private String text;

        public Builder(Context c) {
            this.c = c;
            this.paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            this.paint.setTextSize(16f * c.getResources().getDisplayMetrics().scaledDensity);
            this.paint.setTextAlign(Paint.Align.CENTER);
            this.paint.setColor(Color.BLACK);
        }

        public Builder setTextSize(float textSize) {
            this.paint.setTextSize(textSize);
            return this;
        }

        public Builder setTextSize(@DimenRes int res) {
            return setTextSize(c.getResources().getDimension(res));
        }

        public Builder setTextColor(@ColorInt int color) {
            this.paint.setColor(color);
            return this;
        }

        public Builder setTextColorResource(@ColorRes int res) {
            return setTextColor(ContextCompat.getColor(c, res));
        }

        public Builder setTypeface(Typeface typeface) {
            this.paint.setTypeface(typeface);
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public DefaultStateDisplay build() {
            return new DefaultStateDisplay(this);
        }
    }
}