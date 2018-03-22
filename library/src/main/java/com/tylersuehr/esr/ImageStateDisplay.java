package com.tylersuehr.esr;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * This implementation of {@link EmptyStateRecyclerView.StateDisplay} allows you to display
 * an image and manipulate it.
 *
 * With this you can do the following:
 * (1) Set any Drawable or Bitmap as the image
 * (2) Stretch or crop the image using scale type
 * (3) Align the image using gravity
 * (4) Set margins to adjust image alignment
 *
 * @see Builder to easily instantiate this
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public class ImageStateDisplay extends AbstractStateDisplay {
    /* Constants for image scale type */
    public static final byte NONE           = 0; // No scaling will be applied
    public static final byte FIT_XY         = 1; // Stretch to fit screen dimensions
    public static final byte FIT_WIDTH      = 2; // Stretch to fit screen width
    public static final byte FIT_HEIGHT     = 3; // Stretch to fit screen height
    public static final byte CROP_TO_WIDTH  = 4; // Cropped to fit screen width
    public static final byte CROP_TO_HEIGHT = 5; // Cropped to fit screen height
    public static final byte CROP_XY        = 6; // Cropped to fit larges screen dimension

    private boolean imageConfigured = false;

    /* Stores the scale type to apply to the image */
    private byte scaleType = NONE;
    /* Stores the gravity for the image */
    private int imageGravity;
    /* Stores a reference to the image */
    private Bitmap image;


    @Override
    public void onDrawState(EmptyStateRecyclerView rv, Canvas canvas) {
        final int width = rv.getMeasuredWidth();
        final int height = rv.getMeasuredHeight();
        configureImage(width, height);

        final int horizontalGravity = Gravity.getAbsoluteGravity(imageGravity,
                ViewCompat.getLayoutDirection(rv))&Gravity.HORIZONTAL_GRAVITY_MASK;
        final int verticalGravity = imageGravity&Gravity.VERTICAL_GRAVITY_MASK;

        // Account for horizontal gravity
        float dx;
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
        float dy;
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

        // Account for the set margins
        dx -= getPaddingLeft(); // Left margin
        dx += getPaddingRight(); // Right margin
        dy += getPaddingTop(); // Top margin
        dy -= getPaddingBottom(); // Bottom margin

        // Draw bitmap using locations based on gravity
        canvas.drawBitmap(image, dx, dy, null);
    }

    public void setScaleType(byte scaleType) {
        this.scaleType = scaleType;
        invalidateImage();
    }

    public void setImageGravity(int gravity) {
        this.imageGravity = gravity;
        // No need for invalidation
    }

    public void setImage(Bitmap bitmap) {
        this.image = bitmap;
        invalidateImage();
    }

    public void setImage(Drawable drawable) {
        this.image = ImageUtils.drawableToBitmap(drawable);
        invalidateImage();
    }

    public void setImage(Context c, @DrawableRes int res) {
        setImage(ContextCompat.getDrawable(c, res));
    }

    public void resizeImage(int width, int height) {
        if (image == null) {
            throw new NullPointerException("Please set an image before calling resizeImage()!");
        }
        this.image = Bitmap.createScaledBitmap(image, width, height, false);
        invalidateImage();
    }

    protected void stretchImage(final int screenWidth, final int screenHeight) {
        switch (scaleType) {
            case FIT_XY:
                this.image = Bitmap.createScaledBitmap(image, screenWidth, screenHeight, true);
                break;
            case FIT_WIDTH:
                this.image = Bitmap.createScaledBitmap(image, screenWidth, image.getHeight(), true);
                break;
            case FIT_HEIGHT:
                this.image = Bitmap.createScaledBitmap(image, image.getWidth(), screenHeight, true);
                break;
        }
    }

    protected void cropImage(final int screenWidth, final int screenHeight) {
        final int sourceWidth = image.getWidth();
        final int sourceHeight = image.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        final float xScale = (float)screenWidth / sourceWidth;
        final float yScale = (float)screenHeight / sourceHeight;
        final float scale;
        switch (scaleType) {
            case CROP_TO_WIDTH: // Final scaling will be the width scale
                scale = xScale;
                break;
            case CROP_TO_HEIGHT: // Final scaling will be the height scale
                scale = yScale;
                break;
            default: // Final scaling will be the bigger of the width and height
                scale = Math.max(xScale, yScale);
                break;
        }

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (screenWidth - scaledWidth) / 2;
        float top = (screenHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source
        // bitmap will now be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(screenWidth, screenHeight, image.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(image, null, targetRect, null);
        this.image = dest;
    }

    private void invalidateImage() {
        this.imageConfigured = false;
    }

    private void configureImage(final int width, final int height) {
        if (!imageConfigured) {
            switch (scaleType) {
                case FIT_XY:
                case FIT_WIDTH:
                case FIT_HEIGHT:
                    stretchImage(width, height);
                    break;
                case CROP_XY:
                case CROP_TO_WIDTH:
                case CROP_TO_HEIGHT:
                    cropImage(width, height);
                    break;
            }
            imageConfigured = true;
        }
    }


    /**
     * Internal class to help instantiate {@link ImageStateDisplay}.
     */
    public static final class Builder {
        private final Context c;
        private final int[] padding = { 0, 0, 0, 0 };
        private byte scaleType;
        private int gravity;
        private Bitmap image;


        public Builder(Context c) {
            this.c = c;
        }

        public Builder setImage(@DrawableRes int res) {
            return setImage(ContextCompat.getDrawable(c, res));
        }

        public Builder setImage(Drawable dr) {
            this.image = ImageUtils.drawableToBitmap(dr);
            return this;
        }

        public Builder setImage(Bitmap bp) {
            this.image = bp;
            return this;
        }

        public Builder resizeImage(int widthDp, int heightDp) {
            if (image == null) {
                throw new NullPointerException("Please set image before calling resizeImage()!");
            }

            final float density = c.getResources().getDisplayMetrics().density;
            final int desiredWidth = (int)(widthDp * density);
            final int desiredHeight = (int)(heightDp * density);

            this.image = Bitmap.createScaledBitmap(image, desiredWidth, desiredHeight, false);
            return this;
        }

        public Builder setImageGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setScaleType(byte scaleType) {
            this.scaleType = scaleType;
            return this;
        }

        public Builder setPadding(int leftDp, int topDp, int rightDp, int bottomDp) {
            final float density = c.getResources().getDisplayMetrics().density;
            this.padding[0] = (int)(leftDp * density);
            this.padding[1] = (int)(topDp * density);
            this.padding[2] = (int)(rightDp * density);
            this.padding[3] = (int)(bottomDp * density);
            return this;
        }

        public ImageStateDisplay build() {
            if (image == null) {
                throw new NullPointerException("Image cannot be null!");
            }

            ImageStateDisplay state = new ImageStateDisplay();
            state.setPadding(padding[0], padding[1], padding[2], padding[3]);
            state.scaleType = scaleType;
            state.imageGravity = gravity;
            state.image = image;
            return state;
        }
    }
}