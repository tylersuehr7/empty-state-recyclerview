package com.tylersuehr.esr;

import android.graphics.Canvas;
import android.support.annotation.CallSuper;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * Implementation of {@link StateRecyclerView.State} will be the subclass of all
 * state displays in this package.
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public abstract class AbstractState implements StateRecyclerView.State {
    /* Stores padding dimensions (left, top, right, bottom) */
    private final int[] mPadding = { 0, 0, 0, 0 };
    private boolean mConfigured = false;


    @CallSuper
    @Override
    public void onDrawState(StateRecyclerView rv, Canvas canvas) {
        if (!mConfigured) {
            onConfigure(rv.getMeasuredWidth(), rv.getMeasuredHeight());
            mConfigured = true;
        }
    }

    protected abstract void onConfigure(int availableWidth, int availableHeight);

    public void setPadding(int left, int top, int right, int bottom) {
        this.mPadding[0] = left;
        this.mPadding[1] = top;
        this.mPadding[2] = right;
        this.mPadding[3] = bottom;
    }

    public final int getPaddingLeft() {
        return mPadding[0];
    }

    public final int getPaddingRight() {
        return mPadding[2];
    }

    public final int getPaddingTop() {
        return mPadding[1];
    }

    public final int getPaddingBottom() {
        return mPadding[3];
    }

    protected final void invalidate() {
        mConfigured = false;
    }
}