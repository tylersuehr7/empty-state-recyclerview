package com.tylersuehr.esr;
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
    /* Constants representing all the available possible states */
    public static final byte STATE_NONE    = -1;
    public static final byte STATE_LOADING  = 0;
    public static final byte STATE_EMPTY    = 1;
    public static final byte STATE_ERROR    = 2;
    public static final byte STATE_OK       = 3;

    /* Stores the state the recycler is in */
    private int mCurrentSelection = STATE_NONE;
    private State mCurrentState;
    /* Stores all the available states that can be displayed */
    private final SparseArray<State> mStates = new SparseArray<>();
    /* Stores observer for changes to our state value */
    private OnStateChangeListener mChangeListener;


    public StateRecyclerView(Context context) {
        this(context, null);
    }

    public StateRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateRecyclerView(Context c, @Nullable AttributeSet attrs, int defStyle) {
        super(c, attrs, defStyle);

        // Setup default states
        mStates.put(STATE_LOADING, new DefaultLoadingState(c, "Loading..."));
        mStates.put(STATE_EMPTY, new DefaultEmptyState(c, "No Content", "AWWW...!"));
        mStates.put(STATE_ERROR, new DefaultEmptyState(c, "Something Went Wrong", "SORRY...!"));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mCurrentSelection != STATE_NONE) {
            final State state = mCurrentState;
            state.onDrawState(this, canvas);
        }
    }

    /**
     * Adds a state for the given state key.
     *
     * Note: if a state already exists for the given state key,
     * it will be overwritten.
     *
     * @param stateKey Valid state key
     * @param state {@link State}
     */
    public void addState(int stateKey, @NonNull State state) {
        mStates.put(stateKey, state);
    }

    /**
     * Adds all states for all the given state keys.
     *
     * Note: if a state already exists for the given state key,
     * it will be overwritten.
     *
     * @param stateKeys Array of valid state keys
     * @param states Array of {@link State}
     */
    public void addStates(@NonNull int[] stateKeys, @NonNull State[] states) {
        if (stateKeys.length != states.length) {
            throw new IllegalArgumentException("There must be a state " +
                    "for every given state key!");
        }
        for (int i = 0; i < states.length; i++) {
            mStates.put(stateKeys[i], states[i]);
        }
    }

    /**
     * Removes the state for a given state key.
     *
     * @param stateKey Valid state key
     */
    public void removeState(int stateKey) {
        final int index = mStates.indexOfKey(stateKey);
        if (index > -1) {
            mStates.removeAt(index);
            if (stateKey == mCurrentSelection) {
                clearCurrentState();
            }
        }
    }

    /**
     * Removes all states.
     */
    public void clearStates() {
        if (mStates.size() > 0) {
            mStates.clear();
            clearCurrentState();
        }
    }

    /**
     * Invokes the given state by its state key.
     * @param stateKey Valid state key
     */
    public void invokeState(int stateKey) {
        if (mCurrentSelection == stateKey) { return; }

        // Try to find a valid state in the sparse array
        final State foundState = mStates.get(stateKey);
        if (stateKey != STATE_NONE && foundState == null) {
            throw new NullPointerException("No state exists for " + stateKey);
        }

        // Update the current selection and state (thread-safe)
        synchronized (this) {
            mCurrentSelection = stateKey;
            mCurrentState = foundState;
        }

        // Invoke a UI update
        invalidate();

        // Trigger any available listener
        if (mChangeListener != null) {
            mChangeListener.onStateChanged(mCurrentSelection);
        }
    }

    public void clearCurrentState() {
        synchronized (this) {
            mCurrentSelection = STATE_NONE;
            mCurrentState = null;
        }
        invalidate();
    }

    public State getCurrentState() {
        return mCurrentState;
    }

    public int getCurrentSelection() {
        return mCurrentSelection;
    }

    public void setOnStateChangedListener(OnStateChangeListener listener) {
        mChangeListener = listener;
    }

    public OnStateChangeListener getOnStateChangedListener() {
        return mChangeListener;
    }


    /**
     * Defines a drawable state to manipulate Canvas APIs.
     */
    public interface State {
        void onDrawState(StateRecyclerView rv, Canvas canvas);
    }

    /**
     * Defines callbacks for state change events.
     */
    public interface OnStateChangeListener {
        void onStateChanged(int state);
    }
}