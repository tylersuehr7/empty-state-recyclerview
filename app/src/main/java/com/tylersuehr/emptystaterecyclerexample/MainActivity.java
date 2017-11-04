package com.tylersuehr.emptystaterecyclerexample;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tylersuehr.emptystaterecycler.ContentItemLoadingStateFactory;
import com.tylersuehr.emptystaterecycler.EmptyStateRecyclerView;
import com.tylersuehr.emptystaterecycler.ImageTextStateDisplay;

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

        EmptyStateRecyclerView rv = (EmptyStateRecyclerView)findViewById(R.id.recycler);
        // setup any states here...

        rv.setStateDisplay(EmptyStateRecyclerView.STATE_LOADING,
                ContentItemLoadingStateFactory.newListLoadingState(this));
        rv.setStateDisplay(EmptyStateRecyclerView.STATE_EMPTY,
                new ImageTextStateDisplay(this, R.drawable.img_coffee,
                        "No coffee yet!", "Purchase some coffee to display."));

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