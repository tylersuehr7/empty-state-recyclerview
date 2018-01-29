package com.tylersuehr.esr.update;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * Subclass of {@link RecyclerView} that manages a state and visually
 * represents that state to the user using the Android Canvas APIs.
 *
 * Conceptually, a state represents the context of which the data being
 * shown is in. It can be in a loading state, an empty state, a finished
 * state,... etc.
 *
 * This contains predefined states, but you can also use any integer value
 * to represent any custom state(s) you wish; obviously you cannot use any
 * of the predefined states' values though.
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public class StateRecyclerView extends RecyclerView {
    public static final byte STATE_NONE   = -1;
    public static final byte STATE_EMPTY   = 0;
    public static final byte STATE_ERROR   = 1;
    public static final byte STATE_LOADING = 2;
    public static final byte STATE_OK      = 3;

    private final SparseArray<State> mStates = new SparseArray<>();
    private int mCurrentSelection = STATE_NONE;
    private State mCurrentState;

    private OnStateChangeListener mChangeListener;


    public StateRecyclerView(Context context) {
        this(context, null);
    }

    public StateRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateRecyclerView(Context c, @Nullable AttributeSet attrs, int defStyle) {
        super(c, attrs, defStyle);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mCurrentSelection != STATE_NONE) {
            mCurrentState.onDraw(this, canvas,
                    getMeasuredWidth(),
                    getMeasuredHeight()
            );
        }
    }

    public void setCurrentState(int state) {
        // Find the state in the sparse array
        final State foundState = mStates.get(state);
        if (state != STATE_NONE && foundState == null) {
            throw new NullPointerException("No state exists for " + state + "!");
        }

        // Synchronize writing the current state and selection
        synchronized (this) {
            mCurrentState = foundState;
            mCurrentSelection = state;
        }

        // Update the recycler view UI
        requestLayout();
        invalidate();

        // Trigger any available callback
        if (mChangeListener != null) {
            mChangeListener.onStateChanged(mCurrentSelection);
        }
    }

    public void clearCurrentState() {
        synchronized (this) {
            mCurrentSelection = STATE_NONE;
            mCurrentState = null;
        }
        requestLayout();
        invalidate();
    }

    public void addState(int stateKey, @NonNull State state) {
        mStates.put(stateKey, state);
    }

    public void addStates(@NonNull int[] stateKeys,
                          @NonNull State[] states) {
        if (stateKeys.length != states.length) {
            throw new IllegalArgumentException("There must be a key for state!");
        }
        for (int i = 0; i < stateKeys.length; i++) {
            mStates.put(stateKeys[i], states[i]);
        }
    }

    public void clearStates() {
        mStates.clear();
    }

    public void setOnStateChangeListener(OnStateChangeListener listener) {
        mChangeListener = listener;
    }

    public OnStateChangeListener getOnStateChangeListener() {
        return mChangeListener;
    }


    /**
     * Defines a drawable state.
     */
    public interface State {
        void onDraw(StateRecyclerView rv, Canvas canvas,
                    int availableWidth, int availableHeight);
    }

    /**
     * Defines callbacks for state change events.
     */
    public interface OnStateChangeListener {
        void onStateChanged(int state);
    }
}