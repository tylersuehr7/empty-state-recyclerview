package com.tylersuehr.esr;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * @author Tyler Suehr
 * @version 1.0
 */
final class ImageUtils {
    static Bitmap drawableToBitmap(Drawable dr) {
        if (dr instanceof BitmapDrawable) {
            BitmapDrawable bpDr = (BitmapDrawable)dr;
            if (bpDr.getBitmap() != null) {
                return bpDr.getBitmap();
            }
        }

        final Bitmap bitmap;
        if (dr.getIntrinsicWidth() <= 0 || dr.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(dr.getIntrinsicWidth(), dr.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        dr.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        dr.draw(canvas);
        return bitmap;
    }
}