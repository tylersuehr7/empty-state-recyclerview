package com.tylersuehr.esrexample;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.tylersuehr.esr.ContentItemLoadingStateFactory;
import com.tylersuehr.esr.StateRecyclerView;
import com.tylersuehr.esr.TextStateDisplay;

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

        StateRecyclerView rv = (StateRecyclerView)findViewById(R.id.recycler);
        // setup any states here...

        rv.addState(StateRecyclerView.STATE_LOADING,
                ContentItemLoadingStateFactory.newListLoadingState(this));
        rv.addState(StateRecyclerView.STATE_EMPTY,
                new TextStateDisplay(this, "No content yet", "Please join or create some content."));
        rv.addState(StateRecyclerView.STATE_ERROR,
                new TextStateDisplay(this, "SORRY...!", "Something went wrong :("));

        pretendRunLongTask(rv);
    }

    private void pretendRunLongTask(final StateRecyclerView rv) {
        rv.invokeState(StateRecyclerView.STATE_LOADING);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rv.invokeState(StateRecyclerView.STATE_ERROR);
            }
        }, 5000);
    }
}