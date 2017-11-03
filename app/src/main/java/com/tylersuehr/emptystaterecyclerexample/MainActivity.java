package com.tylersuehr.emptystaterecyclerexample;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tylersuehr.emptystaterecycler.EmptyStateRecyclerView;
import com.tylersuehr.emptystaterecycler.loading.AbstractContentLoadingState;
import com.tylersuehr.emptystaterecycler.loading.ContentLoadingStateFactory;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AbstractContentLoadingState state = ContentLoadingStateFactory.newListLoadingState(this);
        state.setNumberOfContentItems(4);

        EmptyStateRecyclerView rv = (EmptyStateRecyclerView)findViewById(R.id.recycler);
        rv.setStateDisplay(EmptyStateRecyclerView.STATE_LOADING, state);

        pretendRunLongTask(rv);
    }

    private void pretendRunLongTask(final EmptyStateRecyclerView rv) {
        rv.invokeState(EmptyStateRecyclerView.STATE_LOADING);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rv.invokeState(EmptyStateRecyclerView.STATE_EMPTY);
            }
        }, 5000);
    }
}