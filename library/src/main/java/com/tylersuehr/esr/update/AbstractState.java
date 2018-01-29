package com.tylersuehr.esr.update;

import android.graphics.Canvas;
import android.support.annotation.CallSuper;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * Semi-concrete implementation of {@link StateRecyclerView.State} that
 * affords padding manipulation, and a way to configure properties only
 * once during the drawing calls.
 *
 * Is parent of all other implementations of {@link StateRecyclerView.State}.
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public abstract class AbstractState implements StateRecyclerView.State {
    private boolean mConfigured = false;

    private int mPaddingLeft;
    private int mPaddingTop;
    private int mPaddingRight;
    private int mPaddingBottom;


    @CallSuper
    @Override
    public void onDraw(StateRecyclerView rv, Canvas canvas,
                       int availableWidth, int availableHeight) {
        if (!mConfigured) {
            onConfigure(rv, availableWidth, availableHeight);
            mConfigured = true;
        }
    }

    protected abstract void onConfigure(StateRecyclerView rv,
                                        int availableWidth,
                                        int availableHeight);

    protected void invalidate() {
        mConfigured = false;
    }

    public void setPadding(int left, int top, int right, int bottom) {
        mPaddingLeft = left;
        mPaddingTop = top;
        mPaddingRight = right;
        mPaddingBottom = bottom;
    }

    public int getPaddingLeft() {
        return mPaddingLeft;
    }

    public void setPaddingLeft(int left) {
        mPaddingLeft = left;
    }

    public int getPaddingRight() {
        return mPaddingRight;
    }

    public void setPaddingRight(int right) {
        mPaddingRight = right;
    }

    public int getPaddingTop() {
        return mPaddingTop;
    }

    public void setPaddingTop(int top) {
        mPaddingTop = top;
    }

    public int getPaddingBottom() {
        return mPaddingBottom;
    }

    public void setPaddingBottom(int bottom) {
        mPaddingBottom = bottom;
    }
}