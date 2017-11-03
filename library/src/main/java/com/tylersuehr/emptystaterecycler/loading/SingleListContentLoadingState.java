package com.tylersuehr.emptystaterecycler.loading;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * This subclass of {@link AbstractContentLoadingState} draws content items that
 * look like single image, single line mockup items.
 *
 * See for details: https://material.io/guidelines/components/lists.html#
 *
 * @author Tyler Suehr
 * @version 1.0
 */
class SingleListContentLoadingState extends AbstractContentLoadingState {
    /* Immutable constants */
    private final int large;

    /* Used for list item sizing */
    private int circleSize;
    private int lineHeight;


    SingleListContentLoadingState(Context c) {
        super(c);
        setDrawLoadingText(false);
        setNumberOfContentItems(5);

        DisplayMetrics dm = c.getResources().getDisplayMetrics();
        this.large = (int)(16f * dm.density);

        // Setup list content item defaults
        this.circleSize = (int)(40f * dm.density);
        this.lineHeight = (int)(12f * dm.density);
    }

    @Override
    protected void onSetupContentPaint(@NonNull Context c, @NonNull Paint contentPaint) {
        contentPaint.setColor(Color.GRAY);
    }

    @Override
    protected float renderContent(int numberOfContentItems, int availableWidth, int availableHeight, Canvas canvas, Paint contentPaint) {
        final int radius = (circleSize >> 1);
        final int verticalDistance = sizeOfContentItem();

        float dx = 0;
        float dy = 0;

        for (int i = 0; i < numberOfContentItems; i++) {
            dx = radius + large;
            dy = i * verticalDistance + radius + large;

            // Draw the image placeholder
            canvas.drawCircle(dx, dy, radius, contentPaint);

            int diff = (circleSize - lineHeight) >> 1;

            // Draw the first long line
            dx += radius + large;
            dy -= radius - diff;
            canvas.drawRect(
                    dx,
                    dy,
                    dx + (availableWidth - dx - (large << 1)),
                    dy + lineHeight,
                    contentPaint
            );
        }

        return dy;
    }

    @Override
    protected int sizeOfContentItem() {
        return (circleSize + large);
    }
}