package com.tylersuehr.esr;

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
    private final int[] padding = { 0, 0, 0, 0 };


    public void setPadding(int left, int top, int right, int bottom) {
        this.padding[0] = left;
        this.padding[1] = top;
        this.padding[2] = right;
        this.padding[3] = bottom;
    }

    public final int getPaddingLeft() {
        return padding[0];
    }

    public final int getPaddingRight() {
        return padding[2];
    }

    public final int getPaddingTop() {
        return padding[1];
    }

    public final int getPaddingBottom() {
        return padding[3];
    }
}