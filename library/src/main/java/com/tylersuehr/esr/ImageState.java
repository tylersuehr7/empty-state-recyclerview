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
 * This implementation of {@link StateRecyclerView.State} allows you to display
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
public class ImageState extends AbstractState {
    /* Constants for image scale type */
    public static final byte NONE           = 0; // No scaling will be applied
    public static final byte FIT_XY         = 1; // Stretch to fit screen dimensions
    public static final byte FIT_WIDTH      = 2; // Stretch to fit screen width
    public static final byte FIT_HEIGHT     = 3; // Stretch to fit screen height
    public static final byte CROP_TO_WIDTH  = 4; // Cropped to fit screen width
    public static final byte CROP_TO_HEIGHT = 5; // Cropped to fit screen height
    public static final byte CROP_XY        = 6; // Cropped to fit larges screen dimension
    
    /* Stores the scale type to apply to the image */
    private byte mScaleType = NONE;
    /* Stores the gravity for the image */
    private int mGravity;
    /* Stores a reference to the image */
    private Bitmap mImage;


    @Override
    public void onDrawState(StateRecyclerView rv, Canvas canvas) {
        super.onDrawState(rv, canvas);

        final int width = rv.getMeasuredWidth();
        final int height = rv.getMeasuredHeight();

        final int horizontalGravity = Gravity.getAbsoluteGravity(mGravity,
                ViewCompat.getLayoutDirection(rv))&Gravity.HORIZONTAL_GRAVITY_MASK;
        final int verticalGravity = mGravity &Gravity.VERTICAL_GRAVITY_MASK;

        // Account for horizontal gravity
        float dx;
        switch (horizontalGravity) {
            case Gravity.CENTER_HORIZONTAL:
                dx = (width >> 1) - (mImage.getWidth() >> 1);
                break;
            case GravityCompat.END:
                dx = width - mImage.getWidth();
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
                dy = (height >> 1) - (mImage.getHeight() >> 1);
                break;
            case Gravity.BOTTOM:
                dy = height - mImage.getHeight();
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
        canvas.drawBitmap(mImage, dx, dy, null);
    }

    @Override
    protected void onConfigure(int availableWidth, int availableHeight) {
        switch (mScaleType) {
            case FIT_XY:
            case FIT_WIDTH:
            case FIT_HEIGHT:
                stretchImage(availableWidth, availableHeight);
                break;
            case CROP_XY:
            case CROP_TO_WIDTH:
            case CROP_TO_HEIGHT:
                cropImage(availableWidth, availableHeight);
                break;
        }
    }

    public void setScaleType(byte scaleType) {
        mScaleType = scaleType;
        invalidate();
    }

    public void setImageGravity(int gravity) {
        mGravity = gravity;
    }

    public void setImage(Bitmap bitmap) {
        mImage = bitmap;
        invalidate();
    }

    public void setImage(Drawable drawable) {
        mImage = ImageUtils.drawableToBitmap(drawable);
        invalidate();
    }

    public void setImage(Context c, @DrawableRes int res) {
        setImage(ContextCompat.getDrawable(c, res));
    }

    public void resizeImage(int width, int height) {
        if (mImage == null) {
            throw new NullPointerException("Please set an image before calling resizeImage()!");
        }
        mImage = Bitmap.createScaledBitmap(mImage, width, height, false);
        invalidate();
    }

    protected void stretchImage(final int screenWidth, final int screenHeight) {
        switch (mScaleType) {
            case FIT_XY:
                mImage = Bitmap.createScaledBitmap(mImage, screenWidth, screenHeight, true);
                break;
            case FIT_WIDTH:
                mImage = Bitmap.createScaledBitmap(mImage, screenWidth, mImage.getHeight(), true);
                break;
            case FIT_HEIGHT:
                mImage = Bitmap.createScaledBitmap(mImage, mImage.getWidth(), screenHeight, true);
                break;
        }
    }

    protected void cropImage(final int screenWidth, final int screenHeight) {
        final int sourceWidth = mImage.getWidth();
        final int sourceHeight = mImage.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        final float xScale = (float)screenWidth / sourceWidth;
        final float yScale = (float)screenHeight / sourceHeight;
        final float scale;
        switch (mScaleType) {
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
        Bitmap dest = Bitmap.createBitmap(screenWidth, screenHeight, mImage.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(mImage, null, targetRect, null);
        mImage = dest;
    }


    /**
     * Internal class to help instantiate {@link ImageState}.
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

        public ImageState build() {
            if (image == null) {
                throw new NullPointerException("Image cannot be null!");
            }

            ImageState state = new ImageState();
            state.setPadding(padding[0], padding[1], padding[2], padding[3]);
            state.mScaleType = scaleType;
            state.mGravity = gravity;
            state.mImage = image;
            return state;
        }
    }
}