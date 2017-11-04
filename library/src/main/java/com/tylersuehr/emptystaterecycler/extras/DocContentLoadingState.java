package com.tylersuehr.emptystaterecycler.extras;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * This subclass of {@link AbstractContentLoadingState} draws what looks like a
 * mockup document content item.
 *
 * @author Tyler Suehr
 * @version 1.0
 */
class DocContentLoadingState extends AbstractContentLoadingState {
    /* Immutable constants */
    private final int large;

    /* Used for content item sizing */
    private int lineHeight;


    DocContentLoadingState(Context c) {
        super(c);
        DisplayMetrics dm = c.getResources().getDisplayMetrics();
        this.large = (int)(16f * dm.density);

        // Setup list content item defaults
        this.lineHeight = (int)(12f * dm.density);
    }

    @Override
    protected void onSetupContentPaint(@NonNull Context c, @NonNull Paint contentPaint) {
        contentPaint.setColor(Color.GRAY);
    }

    @Override
    protected void renderContent(int numberOfContentItems, int availableWidth, int availableHeight, Canvas canvas, Paint contentPaint) {
        float dy = 0;

        // Since we're only incrementing the y-coordinate, we can allocate for
        // our line width size because it will never change... our line width
        // will be the full width minus 16dp on the right size (left 16dp is
        // accounted for when we draw at the left position)
        final int lineWidth = (availableWidth - large);

        // Only draw half of the maximum possible lines that could be drawn
        int itemsToDraw = (availableHeight / (lineHeight + large)) / 2;
        for (int i = 0; i < itemsToDraw; i++) {
            dy += large;
            // Draw a long line
            canvas.drawRect(
                    large,
                    dy,
                    lineWidth,
                    dy + lineHeight,
                    contentPaint);
            dy += lineHeight;
        }

        // Draw a short line
        dy += large;
        canvas.drawRect(
                large,
                dy,
                lineWidth - (lineWidth / 4),
                dy + lineHeight,
                contentPaint);
    }

    @Override
    protected int sizeOfContentItem() {
        return lineHeight;
    }
}