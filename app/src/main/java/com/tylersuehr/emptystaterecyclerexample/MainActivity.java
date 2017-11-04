package com.tylersuehr.emptystaterecyclerexample;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.tylersuehr.emptystaterecycler.ContentItemLoadingStateFactory;
import com.tylersuehr.emptystaterecycler.EmptyStateRecyclerView;
import com.tylersuehr.emptystaterecycler.TextStateDisplay;

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
                new TextStateDisplay(this, "No content yet", "Please join or create some content."));
        rv.setStateDisplay(EmptyStateRecyclerView.STATE_ERROR,
                new TextStateDisplay(this, "SORRY...!", "Something went wrong :("));

        pretendRunLongTask(rv);
    }

    private void pretendRunLongTask(final EmptyStateRecyclerView rv) {
        rv.invokeState(EmptyStateRecyclerView.STATE_LOADING);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rv.invokeState(EmptyStateRecyclerView.STATE_ERROR);
            }
        }, 5000);
    }
}