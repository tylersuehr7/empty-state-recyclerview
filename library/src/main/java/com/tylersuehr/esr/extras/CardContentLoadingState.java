package com.tylersuehr.esr.extras;
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
 * look like single image multi-lines card mockup items.
 *
 * See for details: https://material.io/guidelines/components/cards.html
 *
 * @author Tyler Suehr
 * @version 1.0
 */
class CardContentLoadingState extends AbstractContentLoadingState {
    /* Immutable constants */
    private final int large;

    /* Used for list item sizing */
    private int circleSize;
    private int lineHeight;


    CardContentLoadingState(Context c) {
        super(c);
        setNumberOfContentItems(2);

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
    protected void renderContent(int numberOfContentItems, int availableWidth, int availableHeight, Canvas canvas, Paint contentPaint) {
        final int radius = (circleSize >> 1);
        final int verticalDistance = sizeOfContentItem();

        float dx = 0;
        float dy = 0;

        for (int i = 0; i < numberOfContentItems; i++) {
            // Draw the circle in the top-left side
            dx = radius + large;
            dy = i * verticalDistance + radius + large;
            canvas.drawCircle(dx, dy, radius, contentPaint);

            // Draw a short line to the right vertical-center of circle
            int offset = (circleSize - lineHeight) >> 1;
            dx += radius + large;
            dy -= radius - offset;
            canvas.drawRect(
                    dx,
                    dy,
                    dx + radius + (availableWidth >> 2),
                    dy + lineHeight,
                    contentPaint);

            // Draw a 4 long lines under the circle, following under each other
            final int longLineDistance = (lineHeight + large);
            for (int j = 0; j < 4; j++) {
                dx = large;
                dy = j * longLineDistance + ((i * verticalDistance) + circleSize + (large << 1));
                canvas.drawRect(
                        dx,
                        dy,
                        dx + (availableWidth - (large << (j == 3 ? 3 : 1))),
                        dy + lineHeight,
                        contentPaint);
            }
        }
    }

    @Override
    protected int sizeOfContentItem() {
        return circleSize + (6 * large) + (4 * lineHeight);
    }
}