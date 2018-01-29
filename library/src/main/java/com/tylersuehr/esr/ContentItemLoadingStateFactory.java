package com.tylersuehr.esr;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * Factory to create new stock instances of {@link AbstractContentItemLoadingState}.
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public abstract class ContentItemLoadingStateFactory {
    private ContentItemLoadingStateFactory() {}

    /**
     * Creates new {@link AbstractContentItemLoadingState} that renders list mockup
     * content items.
     *
     * @param c {@link Context}
     * @return {@link AbstractContentItemLoadingState}
     */
    public static AbstractContentItemLoadingState newListLoadingState(Context c) {
        return new ListContentLoadingState(c);
    }

    /**
     * Creates new {@link AbstractContentItemLoadingState} that renders single list
     * mockup content items.
     *
     * @param c {@link Context}
     * @return {@link AbstractContentItemLoadingState}
     */
    public static AbstractContentItemLoadingState newSingleListLoadingState(Context c) {
        return new SingleListContentLoadingState(c);
    }

    /**
     * Creates new {@link AbstractContentItemLoadingState} that renders multi-line
     * card mockup content items.
     *
     * @param c {@link Context}
     * @return {@link AbstractContentItemLoadingState}
     */
    public static AbstractContentItemLoadingState newCardLoadingState(Context c) {
        return new CardContentLoadingState(c);
    }

    /**
     * Creates new {@link AbstractContentItemLoadingState} that renders a document
     * -like mockup content item.
     *
     * @param c {@link Context}
     * @return {@link AbstractContentItemLoadingState}
     */
    public static AbstractContentItemLoadingState newDocLoadingState(Context c) {
        return new DocContentLoadingState(c);
    }


    /**
     * Semi-concrete implementation of {@link StateRecyclerView.StateDisplay} that
     * provides an API to create 'content item' loading state displays.
     *
     * A content item loading state display is a display that shows mockup-like shapes
     * that animate to resemble the shimmering effect used by Facebook and other companies.
     *
     * This affords the concrete ability for loading text to be drawn at an offset after
     * calculating and drawing the content, and also to animate the drawn content items.
     *
     * This is available to be subclassed by any other classes outside of this parent class,
     * but is also the subclass for all other internal classes.
     */
    public static abstract class AbstractContentItemLoadingState implements StateRecyclerView.StateDisplay {
        private static final int DEFAULT_ANIM_DURATION = 900;

        /* Properties for content loading drawing */
        private final Paint contentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private boolean animateContentItems = true;
        private int numberOfContentItems = 3;
        private ValueAnimator anim;


        public AbstractContentItemLoadingState(Context c) {
            onSetupContentPaint(c, contentPaint);
        }

        @Override
        public final void onDrawState(final StateRecyclerView rv, Canvas canvas) {
            final int width = rv.getMeasuredWidth();
            final int height = rv.getMeasuredHeight();

            // Draw all of our content items
            renderContent(numberOfContentItems, width, height, canvas, contentPaint);

            // Setup and start animation, if possible
            if (animateContentItems) {
                if (anim == null) {
                    this.anim = ObjectAnimator.ofObject(contentPaint, "color", new ArgbEvaluator(),
                            Color.parseColor("#E0E0E0"), Color.parseColor("#BDBDBD"), Color.parseColor("#9E9E9E"));
                    onInterceptAnimatorCreation(anim);
                    this.anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            rv.invalidate();
                        }
                    });
                    this.anim.start();
                }
            }
        }

        /**
         * Intercepts the animator's creation. Override this if you want to set
         * custom properties on the animator.
         *
         * @param anim {@link ValueAnimator}
         */
        protected void onInterceptAnimatorCreation(@NonNull ValueAnimator anim) {
            anim.setDuration(DEFAULT_ANIM_DURATION);
            anim.setRepeatMode(ValueAnimator.REVERSE);
            anim.setRepeatCount(ValueAnimator.INFINITE);
        }

        /**
         * Sets up the content paint used to draw content items.
         *
         * @param c {@link Context}
         * @param contentPaint {@link Paint} to setup
         */
        protected abstract void onSetupContentPaint(@NonNull Context c,
                                                    @NonNull Paint contentPaint);

        /**
         * Render the content at a given index in the content item sequence.
         *
         * @param numberOfContentItems Number of content items to draw
         * @param availableWidth Width canvas real-estate
         * @param availableHeight Height canvas real-estate
         * @param canvas {@link Canvas} to draw on
         * @param contentPaint {@link Paint} to draw with
         */
        protected abstract void renderContent(int numberOfContentItems,
                                              int availableWidth,
                                              int availableHeight,
                                              Canvas canvas,
                                              Paint contentPaint);

        /**
         * Get the height of a single content item.
         *
         * @return Height in pixels
         */
        protected abstract int sizeOfContentItem();

        /**
         * Sets if the content items will animate or not.
         *
         * @param animateContentItems True to animate content items
         */
        public void setAnimateContentItems(boolean animateContentItems) {
            this.animateContentItems = animateContentItems;
        }

        /**
         * Sets the number of content items to draw.
         *
         * @param numberOfContentItems Number of content items displayed
         */
        public void setNumberOfContentItems(int numberOfContentItems) {
            this.numberOfContentItems = numberOfContentItems;
        }
    }

    /**
     * This subclass of {@link AbstractContentItemLoadingState} draws content items that
     * look like single image, double lines mockup items.
     *
     * See for details: https://material.io/guidelines/components/lists.html#
     */
    private static final class ListContentLoadingState extends AbstractContentItemLoadingState {
        /* Immutable constants */
        private final int small;
        private final int large;

        /* Used for list item sizing */
        private int circleSize;
        private int lineHeight;


        ListContentLoadingState(Context c) {
            super(c);
            DisplayMetrics dm = c.getResources().getDisplayMetrics();
            this.small = (int)(8f * dm.density);
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
                dx = radius + large;
                dy = i * verticalDistance + radius + large;

                // Draw the image placeholder
                canvas.drawCircle(dx, dy, radius, contentPaint);

                int diff = (circleSize - (lineHeight * 2 + small)) >> 1;

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

                // Draw the second semi-long line
                dy += lineHeight + small;
                canvas.drawRect(
                        dx,
                        dy,
                        dx + (availableWidth - dx - (large << 3)),
                        dy + lineHeight,
                        contentPaint
                );
            }
        }

        @Override
        protected int sizeOfContentItem() {
            return (circleSize + large);
        }
    }

    /**
     * This subclass of {@link AbstractContentItemLoadingState} draws content items that
     * look like single image, single line mockup items.
     *
     * See for details: https://material.io/guidelines/components/lists.html#
     */
    private static final class SingleListContentLoadingState extends AbstractContentItemLoadingState {
        /* Immutable constants */
        private final int large;

        /* Used for list item sizing */
        private int circleSize;
        private int lineHeight;


        SingleListContentLoadingState(Context c) {
            super(c);
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
        }

        @Override
        protected int sizeOfContentItem() {
            return (circleSize + large);
        }
    }

    /**
     * This subclass of {@link AbstractContentItemLoadingState} draws content items that
     * look like single image multi-lines card mockup items.
     *
     * See for details: https://material.io/guidelines/components/cards.html
     */
    private static final class CardContentLoadingState extends AbstractContentItemLoadingState {
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

    /**
     * This subclass of {@link AbstractContentItemLoadingState} draws what looks like a
     * mockup document content item.
     */
    private static final class DocContentLoadingState extends AbstractContentItemLoadingState {
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
}