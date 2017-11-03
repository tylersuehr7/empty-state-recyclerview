package com.tylersuehr.emptystaterecycler.empty;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.tylersuehr.emptystaterecycler.EmptyStateRecyclerView;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * This implementation of {@link EmptyStateRecyclerView.StateDisplay} will display a
 * Bitmap image, ensuring that it is cropped at the center to fit width and height
 * dimensions.
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public class CenterCropBitmapEmptyState implements EmptyStateRecyclerView.StateDisplay {
    private boolean centerCropped = false;
    private int backgroundColor;
    private Bitmap image;
    private int gravity;


    private CenterCropBitmapEmptyState(Builder b) {
        this.image = b.bitmap;
        this.gravity = b.gravity;
        this.backgroundColor = b.backgroundColor;
    }

    @Override
    public void onDrawState(EmptyStateRecyclerView rv, Canvas canvas) {
        final int width = rv.getMeasuredWidth();
        final int height = rv.getMeasuredHeight();
        setupBitmap(width, height);

        // Set background color
        canvas.drawColor(backgroundColor);

        final int horizontalGravity = Gravity.getAbsoluteGravity(gravity,
                ViewCompat.getLayoutDirection(rv))&Gravity.HORIZONTAL_GRAVITY_MASK;
        final int verticalGravity = gravity&Gravity.VERTICAL_GRAVITY_MASK;

        // Account for horizontal gravity
        final float dx;
        switch (horizontalGravity) {
            case Gravity.CENTER_HORIZONTAL:
                dx = (width >> 1) - (image.getWidth() >> 1);
                break;
            case GravityCompat.END:
                dx = width - image.getWidth();
                break;
            default:
            case GravityCompat.START:
                dx = 0;
                break;
        }

        // Account for vertical gravity
        final float dy;
        switch (verticalGravity) {
            case Gravity.CENTER_VERTICAL:
                dy = (height >> 1) - (image.getHeight() >> 1);
                break;
            case Gravity.BOTTOM:
                dy = height - image.getHeight();
                break;
            default:
            case Gravity.TOP:
                dy = 0;
                break;
        }

        // Draw bitmap using locations based on gravity
        canvas.drawBitmap(image, dx, dy, null);
    }

    private void setupBitmap(final int width, final int height) {
        if (!centerCropped) {
            this.image = scaleCenterCrop(image, width, height);
            centerCropped = true;
        }
    }

    private static Bitmap scaleCenterCrop(Bitmap source, int newWidth, int newHeight) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }


    public static final class Builder {
        private final Context c;
        private Bitmap bitmap;
        private int gravity;
        private int backgroundColor;

        public Builder(Context c) {
            this.c = c;
            this.gravity = Gravity.CENTER;
            this.backgroundColor = Color.WHITE;
        }

        public Builder setImage(@DrawableRes int res) {
            this.bitmap = BitmapFactory.decodeResource(c.getResources(), res);
            return this;
        }

        public Builder setBackgroundColor(@ColorInt int color) {
            this.backgroundColor = color;
            return this;
        }

        public Builder setBackgroundResource(@ColorRes int res) {
            this.backgroundColor = ContextCompat.getColor(c, res);
            return this;
        }

        public Builder setImage(Bitmap bitmap) {
            this.bitmap = bitmap;
            return this;
        }

        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public CenterCropBitmapEmptyState build() {
            if (bitmap == null) {
                throw new NullPointerException("Please set an image that isn't null!");
            }
            return new CenterCropBitmapEmptyState(this);
        }
    }
}